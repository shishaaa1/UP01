using System;
using System.Collections.Generic;
using System.Net.Http;
using System.Net.Http.Json;
using System.Threading.Tasks;
using TaigerDesktop.Models;

namespace TaigerDesktop.Connect
{
    public class ApiContext
    {
        private readonly HttpClient _httpClient;
        public string CurrentLogin { get; private set; }
        public bool IsAuthenticated { get; private set; }

        public ApiContext()
        {
            _httpClient = new HttpClient
            {
                BaseAddress = new Uri("https://localhost:7252/api/")
            };
            IsAuthenticated = false;
            CurrentLogin = string.Empty;
        }

        // Метод авторизации администратора
        public async Task<bool> LoginAdminAsync(string login, string password)
        {
            try
            {
                var formData = new Dictionary<string, string>
                {
                    { "login", login },
                    { "password", password }
                };

                var content = new FormUrlEncodedContent(formData);
                var response = await _httpClient.PostAsync("AdminController/LoginAdmin", content);

                if (response.IsSuccessStatusCode)
                {
                    CurrentLogin = login;
                    IsAuthenticated = true;
                    return true;
                }

                // Опционально: прочитать тело ошибки для отладки
                var error = await response.Content.ReadAsStringAsync();
                Console.WriteLine($"Ошибка входа: {error}");

                return false;
            }
            catch (Exception ex)
            {
                Console.WriteLine($"Ошибка авторизации: {ex.Message}");
                return false;
            }
        }

        public void Logout()
        {
            CurrentLogin = string.Empty;
            IsAuthenticated = false;
        }



        public async Task<Admin> AddAdminAsync(Admin user)
        {
            try
            {
                // 1. Правильный Dictionary
                var formData = new Dictionary<string, string>
        {
            { "Nickname", user.Nickname },
            { "Login", user.Login },
            { "Password", user.Password }
        };

                var content = new FormUrlEncodedContent(formData);

                var response = await _httpClient.PostAsync("AdminController/AddAdmin", content);

                if (response.IsSuccessStatusCode)
                {
                    return user;
                }
                var error = await response.Content.ReadAsStringAsync();
                Console.WriteLine($"Ошибка добавления админа: {error}");
                return null;
            }
            catch (Exception ex)
            {
                Console.WriteLine($"Исключение при добавлении админа: {ex.Message}");
                return null;
            }
        }
        public async Task<List<Users>> GetAllUsersAsync()
        {
            try
            {
                var response = await _httpClient.GetAsync("UserController/GetUsers");
                if (response.IsSuccessStatusCode)
                {
                    return await response.Content.ReadFromJsonAsync<List<Users>>() ?? new List<Users>();
                }
                return new List<Users>();
            }
            catch (Exception ex)
            {
                Console.WriteLine($"Ошибка получения пользователей: {ex.Message}");
                return new List<Users>();
            }
        }
        public async Task<List<PhotosUsers>> GetAllPhotosAsync()
        {
            try
            {
                var response = await _httpClient.GetAsync("PhotosController/GetPhotoByUserId");
                if (response.IsSuccessStatusCode)
                {
                    return await response.Content.ReadFromJsonAsync<List<PhotosUsers>>() ?? new List<PhotosUsers>();
                }
                return new List<PhotosUsers>();
            }
            catch (Exception ex) {
                Console.WriteLine($"Ошибка получения пользоваталей: {ex.Message}");
                return new List<PhotosUsers>();
            }
        }
        public async Task<bool> DeleteUserAsync(int userId)
        {
            try
            {
                var response = await _httpClient.DeleteAsync($"UserController/DeleteUser");
                return response.IsSuccessStatusCode;
            }
            catch (Exception ex)
            {
                Console.WriteLine($"Ошибка удаления пользователя: {ex.Message}");
                return false;
            }
        }
        public async Task<bool> DeletePhotoAsync(int photoId)
        {
            try
            {
                var response = await _httpClient.DeleteAsync($"UserController/DeletePhoto");
                return response.IsSuccessStatusCode;
            }
            catch (Exception ex)
            {
                Console.WriteLine($"Ошибка удаления фото пользователя: {ex.Message}");
                return false;
            }
        }
        
        public async Task<List<DailyStat>> GetStatsLast30DaysAsync()
        {
            var response = await _httpClient.GetAsync("UserController/CountUsersToday");
            if (response.IsSuccessStatusCode)
            {
                return await response.Content.ReadFromJsonAsync<List<DailyStat>>() ?? new List<DailyStat>();
            }
            return new List<DailyStat>();
        }
    }
}