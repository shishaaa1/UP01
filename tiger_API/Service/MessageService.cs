using Azure.Messaging;
using Microsoft.EntityFrameworkCore;
using tiger_API.Context;
using tiger_API.Itreface;
using tiger_API.Modell;

namespace tiger_API.Service
{
    public class MessageService : IMessageService
    {
        private readonly MessegeContext _messegeContext;

        public MessageService(MessegeContext messegeContext)
        {
            _messegeContext = messegeContext;
        }

        public async Task SendMessageAsync(int senderId, int recipientId, string text)
        {
            if (senderId == recipientId)
                throw new ArgumentException("Нельзя писать самому себе.");

            var sender = await _messegeContext.Users.FindAsync(senderId);
            var recipient = await _messegeContext.Users.FindAsync(recipientId);

            if (sender == null || recipient == null)
                throw new ArgumentException("Пользователь не найден.");

            if (sender.Sex == recipient.Sex)
                throw new InvalidOperationException("Общение разрешено только между пользователями разного пола.");

            var message = new Message
            {
                User1 = senderId,
                User2 = recipientId,
                Text = text ?? string.Empty,
                SentAt = DateTime.UtcNow
            };

            _messegeContext.Messages.Add(message);
            await _messegeContext.SaveChangesAsync();
        }

        public async Task<List<Message>> GetConversationAsync(int u1, int u2)
        {
            return await _messegeContext.Messages
                .Where(m =>
                    (m.User1 == u1 && m.User2 == u2) ||
                    (m.User1 == u2 && m.User2 == u1))
                .OrderBy(m => m.SentAt)
                .ToListAsync();
        }
    }
}
