namespace tiger_API.Itreface
{
    public interface IConversationAIService
    {
        Task<string> GenerateConversationContinuationAsync(int user1Id, int user2Id);
    }
}
