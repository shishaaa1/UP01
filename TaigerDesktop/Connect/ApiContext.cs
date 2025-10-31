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

        // Метод выхода
        public void Logout()
        {
            CurrentLogin = string.Empty;
            IsAuthenticated = false;
        }

        // Получение списка администраторов
        public async Task<List<Admin>> GetAdminsAsync()
        {
            CheckAuthentication();
            var result = await _httpClient.GetFromJsonAsync<List<Admin>>("Admin");
            return result ?? new List<Admin>();
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

        public async Task<bool> DeleteAdminAsync(int id)
        {
            CheckAuthentication();
            var response = await _httpClient.DeleteAsync($"Admin/{id}");
            return response.IsSuccessStatusCode;
        }

        // Методы для работы с пользователями
        public async Task<List<Users>> GetUsersAsync()
        {
            CheckAuthentication();
            var result = await _httpClient.GetFromJsonAsync<List<Users>>("Users");
            return result ?? new List<Users>();
        }

        public async Task<List<PhotosUsers>> GetPhotosForCheckAsync()
        {
            CheckAuthentication();
            var result = await _httpClient.GetFromJsonAsync<List<PhotosUsers>>("Photos/forcheck");
            return result ?? new List<PhotosUsers>();
        }
        public async Task<bool> DeleteUser(int id)
        {
            CheckAuthentication();
            var result = await _httpClient.DeleteAsync($"Users/{id}");
            return result.IsSuccessStatusCode;
        }
        public async Task<bool> DeletePhotoAsync(int id)
        {
            CheckAuthentication();
            var response = await _httpClient.DeleteAsync($"Photos/{id}");
            return response.IsSuccessStatusCode;
        }

        // Проверка авторизации перед запросами
        private void CheckAuthentication()
        {
            if (!IsAuthenticated)
            {
                throw new UnauthorizedAccessException("Требуется авторизация администратора");
            }
        }
    }
}