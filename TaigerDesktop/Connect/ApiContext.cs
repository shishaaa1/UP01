using System;
using System.Collections.Generic;
using System.IO;
using System.Net.Http;
using System.Net.Http.Json;
using System.Text.Json;
using System.Text.Json.Serialization;
using System.Threading.Tasks;
using System.Windows.Media;
using System.Windows.Media.Imaging;
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
        public async Task<bool> LoginAdminAAsync(string login, string password)
        {
            try
            {
                var formData = new Dictionary<string, string>
        {
            { "login", login },
            { "password", password }
        };

                var content = new FormUrlEncodedContent(formData);
                var response = await _httpClient.PostAsync("AdminController/GetLoginAndNick", content);

                if (response.IsSuccessStatusCode)
                {
                    var responseJson = await response.Content.ReadAsStringAsync();
                    var result = JsonSerializer.Deserialize<AdminLoginResponse>(responseJson, new JsonSerializerOptions
                    {
                        PropertyNameCaseInsensitive = true
                    });

                    if (result != null)
                    {
                        CurrentLogin = result.Login;
                        IsAuthenticated = true;

                        // Передаём данные в App для привязки
                        App.SetAdminData(result.Login, result.Nickname);
                        return true;
                    }
                }

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
        public async Task<bool> EditAdminAsync(Admin admin)
        {
            try
            {
                // Убедимся, что Id передан
                if (admin.Id <= 0)
                {
                    Console.WriteLine("Ошибка: Id администратора не указан.");
                    return false;
                }

                // Подготовим данные для отправки
                var formData = new Dictionary<string, string>
        {
            { "Id", admin.Id.ToString() },
            { "Nickname", admin.Nickname },
            { "Login", admin.Login },
            { "Password", admin.Password }
        };

                var content = new FormUrlEncodedContent(formData);

                // Отправляем PUT-запрос (или POST — в зависимости от API)
                var response = await _httpClient.PostAsync("AdminController/EditAdmin", content);

                if (response.IsSuccessStatusCode)
                {
                    Console.WriteLine("Администратор успешно обновлён.");
                    return true;
                }

                var error = await response.Content.ReadAsStringAsync();
                Console.WriteLine($"Ошибка обновления админа: {error}");
                return false;
            }
            catch (Exception ex)
            {
                Console.WriteLine($"Исключение при обновлении админа: {ex.Message}");
                return false;
            }
        }
        public async Task<List<Users>> GetAllUsersAsync()
        {
            try
            {
                var response = await _httpClient.GetAsync("UserController/GetUsers");

                if (response.IsSuccessStatusCode)
                {
                    var users = await response.Content.ReadFromJsonAsync<List<Users>>();
                    return users ?? new List<Users>();
                }

                return new List<Users>();
            }
            catch (Exception ex)
            {
                Console.WriteLine($"Ошибка получения пользователей: {ex.Message}");
                return new List<Users>();
            }
        }
        public async Task<List<Admin>> GetAllAdminsAsync()
        {
            try
            {
                var response = await _httpClient.GetAsync("AdminController/GetAllAdmins");

                if (response.IsSuccessStatusCode)
                {
                    var users = await response.Content.ReadFromJsonAsync<List<Admin>>();
                    return users ?? new List<Admin>();
                }

                return new List<Admin>();
            }
            catch (Exception ex)
            {
                Console.WriteLine($"Ошибка получения пользователей: {ex.Message}");
                return new List<Admin>();
            }
        }
        public async Task<List<PhotosUsers>> GetPhotosByUsersIdAsync()
        {
            try
            {
                var response = await _httpClient.GetAsync("PhotoController/GetAllPhotos");

                if (response.IsSuccessStatusCode)
                {
                    var dtos = await response.Content.ReadFromJsonAsync<List<UserPhotoDto>>();
                    Console.WriteLine($"Получено фото: {dtos.Count} шт.");
                    foreach (var dto in dtos)
                    {
                        Console.WriteLine($"ID: {dto.PhotoId}, User: {dto.FirstName} {dto.Login}, PhotoData: {dto.PhotoData?.Length} байт");
                    }

                    return dtos?.Select(dto => new PhotosUsers
                    {

                        Id = dto.PhotoId,
                        UserId = dto.UserId,
                        Photobill = dto.PhotoData,
                        UserName = $"{dto.FirstName} {dto.LastName}",
                        Login = dto.Login

                    }).ToList() ?? new List<PhotosUsers>();
                    
                }
                else
                {
                    Console.WriteLine($"Ошибка: {response.StatusCode} - {response.ReasonPhrase}");
                    return new List<PhotosUsers>();
                }
            }
            catch (Exception ex)
            {
                Console.WriteLine($"Ошибка загрузки фото: {ex.Message}");
                return new List<PhotosUsers>();
            }
        }
        public async Task<bool> DeleteAdminAsync(int adminId)
        {
            try
            {
                var response = await _httpClient.DeleteAsync($"AdminController/DeleteAdmin/{adminId}");
                return response.IsSuccessStatusCode;
            }
            catch (Exception ex)
            {
                Console.WriteLine($"Ошибка удаления пользователя: {ex.Message}");
                return false;
            }
        }
        
        public async Task<bool> DeleteUserAsync(int userId)
        {
            try
            {
                var response = await _httpClient.DeleteAsync($"UserController/DeleteUser/{userId}");
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
                var response = await _httpClient.DeleteAsync($"PhotoController/DeletePhoto?id={photoId}");
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
        private class UserPhotoDto
        {
            public int PhotoId { get; set; }
            public int UserId { get; set; }
            public string FirstName { get; set; }
            public string LastName { get; set; }
            public string Login { get; set; }
            public byte[] PhotoData { get; set; }
        }

        private class AdminLoginResponse
        {
            public int AdminId { get; set; }
            public string Login { get; set; }
            public string Nickname { get; set; }
        }

    }

}