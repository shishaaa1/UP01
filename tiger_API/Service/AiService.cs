using Microsoft.EntityFrameworkCore;
using System.Text.Json;
using tiger_API.Context;
using tiger_API.Modell;
using tiger_API.Itreface;

namespace tiger_API.Service
{
    public class AiService : AiInterface
    {
        private readonly UsersContext _usersContext;
        private readonly IConfiguration _configuration;
        private readonly string _apiKey;
        private readonly HttpClient _httpClient;
        private readonly ILogger<AiService> _logger;
        private readonly string _defaultModel = "Qwen/Qwen2.5-7B-Instruct";

        public AiService(
    UsersContext usersContext,
    IConfiguration configuration,
    IHttpClientFactory httpClientFactory,
    ILogger<AiService> logger)
        {
            _usersContext = usersContext;
            _configuration = configuration;
            _logger = logger;
            _httpClient = httpClientFactory.CreateClient("HuggingFace");

            _apiKey = _configuration["HuggingFace:ApiKey"]
                      ?? Environment.GetEnvironmentVariable("HF_TOKEN");
            if (string.IsNullOrEmpty(_apiKey))
            {
                throw new InvalidOperationException("HuggingFace API key is not configured.");
            }
        }
        public async Task<AssistantStatus> GetAssistantStatusAsync()
        {
            try
            {
                _logger.LogInformation("Checking AI assistant status");

                // Проверяем, что ключ существует
                if (string.IsNullOrEmpty(_apiKey))
                {
                    return new AssistantStatus
                    {
                        IsWorking = false,
                        StatusMessage = "API key not configured",
                        CheckedAt = DateTime.UtcNow
                    };
                }

                // Проверяем доступность модели через простой запрос
                var requestData = new
                {
                    model = _defaultModel,
                    messages = new[]
                    {
                new
                {
                    role = "user",
                    content = "ping"
                }
            },
                    max_tokens = 10
                };

                var jsonContent = JsonSerializer.Serialize(requestData);
                var content = new StringContent(jsonContent, System.Text.Encoding.UTF8, "application/json");

                var response = await _httpClient.PostAsync("chat/completions", content);

                if (response.IsSuccessStatusCode)
                {
                    return new AssistantStatus
                    {
                        IsWorking = true,
                        StatusMessage = "AI Assistant is working properly",
                        ModelName = _defaultModel,
                        CheckedAt = DateTime.UtcNow
                    };
                }
                else
                {
                    var errorContent = await response.Content.ReadAsStringAsync();
                    _logger.LogWarning("AI status check failed: {StatusCode} - {Content}", response.StatusCode, errorContent);

                    return new AssistantStatus
                    {
                        IsWorking = false,
                        StatusMessage = $"AI Assistant error: {response.StatusCode}",
                        ModelName = _defaultModel,
                        CheckedAt = DateTime.UtcNow
                    };
                }
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Error checking AI assistant status");

                return new AssistantStatus
                {
                    IsWorking = false,
                    StatusMessage = $"Error: {ex.Message}",
                    ModelName = _defaultModel,
                    CheckedAt = DateTime.UtcNow
                };
            }
        }
        public async Task<string> GetChatCompletionAsync(string userMessage)
        {
            return await GetChatCompletionAsync(_defaultModel, userMessage);
        }

        public async Task<string> GetChatCompletionAsync(string model, string userMessage)
        {
            try
            {
                _logger.LogInformation("Sending AI request for model: {Model}", model);
                var requestData = new
                {
                    model = model,
                    messages = new[]
                    {
                new
                {
                    role = "user",
                    content = userMessage
                }
            },
                    max_tokens = 500,
                    temperature = 0.7
                };
                var jsonContent = JsonSerializer.Serialize(requestData);
                var content = new StringContent(jsonContent, System.Text.Encoding.UTF8, "application/json");
                var response = await _httpClient.PostAsync("chat/completions", content);

                if (!response.IsSuccessStatusCode)
                {
                    var errorContent = await response.Content.ReadAsStringAsync();
                    _logger.LogError("AI API error: {StatusCode} - {Content}", response.StatusCode, errorContent);
                    return $"API Error: {response.StatusCode} - {errorContent}";
                }

                var responseJson = await response.Content.ReadAsStringAsync();
                _logger.LogDebug("AI Response: {Response}", responseJson);
                using var document = JsonDocument.Parse(responseJson);
                var root = document.RootElement;

                var message = root
                    .GetProperty("choices")[0]
                    .GetProperty("message")
                    .GetProperty("content")
                    .GetString();

                _logger.LogInformation("AI response received successfully");
                return message ?? "No response from AI";
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Error getting AI completion for model {Model}", model);
                return $"Error: {ex.Message}";
            }
        }

        public async Task<string> GenerateMatchSuggestionAsync(int userId)
        {
            try
            {
                var user = await _usersContext.Users.FindAsync(userId);
                if (user == null)
                {
                    _logger.LogWarning("User {UserId} not found for match suggestions", userId);
                    return "User not found";
                }

                var potentialMatches = await _usersContext.Users
                    .Where(u => u.Id != userId && u.Sex != user.Sex)
                    .Take(5)
                    .ToListAsync();

                if (!potentialMatches.Any())
                {
                    _logger.LogInformation("No potential matches found for user {UserId}", userId);
                    return "No potential matches found";
                }

                var matchesInfo = string.Join("\n", potentialMatches.Select((m, i) =>
                    $"{i + 1}. {m.FirstName} {m.LastName}, Bio: {(string.IsNullOrEmpty(m.BIO) ? "No bio" : m.BIO.Substring(0, Math.Min(100, m.BIO.Length)) + "...")}"));

                var prompt = $@"
                    User looking for matches: {user.FirstName} {user.LastName}
                    Bio: {user.BIO ?? "No bio provided"}
                    Sex: {user.Sex}
                    
                    Potential matches:
                    {matchesInfo}
                    
                    Analyze these potential matches and recommend the best one with:
                    1. Compatibility score (1-10) and why
                    2. Suggested first message
                    3. Topics for conversation
                ";

                _logger.LogInformation("Generating match suggestions for user {UserId}", userId);
                return await GetChatCompletionAsync(prompt);
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Error generating match suggestions for user {UserId}", userId);
                return $"Error generating suggestions: {ex.Message}";
            }
        }

        public async Task<string> GenerateIcebreakerAsync(int user1Id, int user2Id)
        {
            try
            {
                var user1 = await _usersContext.Users.FindAsync(user1Id);
                var user2 = await _usersContext.Users.FindAsync(user2Id);

                if (user1 == null || user2 == null)
                {
                    _logger.LogWarning("One or both users not found: {User1Id}, {User2Id}", user1Id, user2Id);
                    return "One or both users not found";
                }

                var prompt = $@"
                    Generate 3 icebreaker conversation starters for a dating app:
                    
                    User 1: {user1.FirstName} ({user1.Sex})
                    Bio: {user1.BIO ?? "No bio"}
                    
                    User 2: {user2.FirstName} ({user2.Sex})
                    Bio: {user2.BIO ?? "No bio"}
                    
                    Provide icebreakers that:
                    1. Are personalized based on their profiles
                    2. Are open-ended to encourage response
                    3. Are friendly and appropriate
                    4. Can lead to deeper conversation
                ";

                _logger.LogInformation("Generating icebreakers for users {User1Id} and {User2Id}", user1Id, user2Id);
                return await GetChatCompletionAsync(prompt);
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Error generating icebreakers for users {User1Id} and {User2Id}", user1Id, user2Id);
                return $"Error generating icebreakers: {ex.Message}";
            }
        }
    }
}