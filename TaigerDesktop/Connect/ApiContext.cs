using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Net.Http;
using System.Text.Json.Serialization;
using System.Net.Http.Json;
using TaigerDesktop.Models;
using Azure;

namespace TaigerDesktop.Connect
{
    public class ApiContext
    {
        private readonly HttpClient _httpClient;
        public ApiContext()
        {
            _httpClient = new HttpClient
            {
                BaseAddress = new Uri("https://localhost:7252/api/")
            };
        }
        public async Task<List<Users>> GetUsersAsync()
        {
            var result = await _httpClient.GetFromJsonAsync<List<Users>>("Users");
            return result ?? new List<Users>();
        }
        public async Task<bool> AddAdmin(Admin admin)
        {
            var response = await _httpClient.PostAsJsonAsync("Admin",admin);
            return response.IsSuccessStatusCode;
        }
        public async Task<bool> DeleteUser(int id)
        {
            var response = await _httpClient.DeleteAsync($"Admin/{id}");
            return response.IsSuccessStatusCode;
        }
        public async Task<List<PhotosUsers>> GetPhotosForCheckAsync()
        {
            var result = await _httpClient.GetFromJsonAsync<List<PhotosUsers>>("Photos/forcheck");
            return result ?? new List<PhotosUsers>();
        }
        // Connect/ApiContext.cs
        public async Task<bool> DeletePhoto(int id)
        {
            var response = await _httpClient.DeleteAsync($"Photos/{id}");
            return response.IsSuccessStatusCode;
        }

    }
}
