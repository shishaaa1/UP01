using Microsoft.EntityFrameworkCore;
using tiger_API.Context;
using tiger_API.Itreface;
using tiger_API.Modell;

namespace tiger_API.Service
{
    public class AuditService : IAuditService
    {
        private readonly AuditContext _context;
        private readonly IHttpContextAccessor _http;

        public AuditService(AuditContext context, IHttpContextAccessor http)
        {
            _context = context;
            _http = http;
        }

        public async Task LogAsync(int userId, string action, string? entity = null, string? details = null)
        {
            var log = new UserActivityLog
            {
                UserId = userId,
                Action = action,
                Entity = entity,
                Details = details,
                IpAddress = _http.HttpContext?.Connection?.RemoteIpAddress?.ToString()
            };

            _context.UserActivityLogs.Add(log);
            await _context.SaveChangesAsync();
        }
        public async Task<List<UserActivityLog>> GetAllUsersLogsAsync()
        {
            return await _context.UserActivityLogs.ToListAsync();
        }
        public async Task<List<UserActivityLog>> GetLogsByIdUser(int userId)
        {
            return await _context.UserActivityLogs
                .Where(l => l.UserId == userId)
                .ToListAsync();
        }

    }

}
