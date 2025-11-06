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
                Userid1 = senderId,
                Userid2 = recipientId,
                Text = text ?? string.Empty,
                SendAt = DateTime.UtcNow
            };

            _messegeContext.Message.Add(message);
            await _messegeContext.SaveChangesAsync();
        }

        public async Task<List<Message>> GetConversationAsync(int u1, int u2)
        {
            return await _messegeContext.Message
                .Where(m =>
                    (m.Userid1 == u1 && m.Userid2 == u2) ||
                    (m.Userid1 == u2 && m.Userid2 == u1))
                .OrderBy(m => m.SendAt)
                .ToListAsync();
        }
        public async Task DeleteMessageAsync(int messageId)
        {
            var message = await _messegeContext.Message.FindAsync(messageId);
            if (message == null)
                throw new KeyNotFoundException("Сообщение не найдено.");

            _messegeContext.Message.Remove(message);
            await _messegeContext.SaveChangesAsync();
        }

        public async Task DeleteConversationAsync(int userId1, int userId2)
        {
            var messages = await _messegeContext.Message
                .Where(m =>
                    (m.Userid1 == userId1 && m.Userid2 == userId2) ||
                    (m.Userid1 == userId2 && m.Userid2 == userId1))
                .ToListAsync();

            if (messages.Count == 0)
                throw new KeyNotFoundException("Переписка между пользователями не найдена.");

            _messegeContext.Message.RemoveRange(messages);
            await _messegeContext.SaveChangesAsync();
        }
    }
}
