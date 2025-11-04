using Microsoft.AspNetCore.SignalR;
using Microsoft.EntityFrameworkCore;
using System;
using tiger_API.Context;
using tiger_API.Itreface;

namespace tiger_API.Hubs
{
    public class ChatHub : Hub
    {
        private readonly IMessageService _messageService;
        private readonly MessegeContext _db;

        public ChatHub(IMessageService messageService, MessegeContext db)
        {
            _messageService = messageService;
            _db = db;
        }

        public async Task SendMessage(int senderId, string password, int recipientId, string text)
        {
            var user = await _db.Users
                .FirstOrDefaultAsync(u => u.Id == senderId && u.Password == password);

            if (user == null)
                throw new HubException("Неверный логин или пароль.");

            await Groups.AddToGroupAsync(Context.ConnectionId, senderId.ToString());
            await Groups.AddToGroupAsync(Context.ConnectionId, recipientId.ToString());

            await _messageService.SendMessageAsync(senderId, recipientId, text);

            var dto = new { SenderId = senderId, Text = text, SentAt = DateTime.UtcNow };

            await Clients.Group(senderId.ToString()).SendAsync("ReceiveMessage", dto);
            await Clients.Group(recipientId.ToString()).SendAsync("ReceiveMessage", dto);
        }
    }
}
