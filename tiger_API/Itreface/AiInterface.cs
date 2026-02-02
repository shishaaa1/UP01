namespace tiger_API.Itreface
{
    public interface AiInterface
    {
        Task<string> GetChatCompletionAsync(string userMessage);
        Task<string> GetChatCompletionAsync(string model, string userMessage);
        Task<string> GenerateMatchSuggestionAsync(int userId);
        Task<string> GenerateIcebreakerAsync(int user1Id, int user2Id);
    }
}