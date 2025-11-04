using tiger_API.Modell;

namespace tiger_API.Itreface
{
    public interface IMessageService
    {
        Task SendMessageAsync(int senderId, int recipientId, string text);
        Task<List<Message>> GetConversationAsync(int user1Id, int user2Id);
    }
}
