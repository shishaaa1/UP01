namespace tiger_API.Itreface
{
    public interface AiInterface
    {
        Task<string> GetChatCompletionAsync(string userMessage);
        Task<string> GetChatCompletionAsync(string model, string userMessage);
        Task<string> GenerateMatchSuggestionAsync(int userId);
        Task<string> GenerateIcebreakerAsync(int user1Id, int user2Id);
        Task<AssistantStatus> GetAssistantStatusAsync();
    }

    public class AssistantStatus
    {
        public bool IsWorking { get; set; }
        public string StatusMessage { get; set; } = string.Empty;
        public string ModelName { get; set; } = string.Empty;
        public DateTime CheckedAt { get; set; }
        public string Version { get; set; } = "1.0";
    }
}
