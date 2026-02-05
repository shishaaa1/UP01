using tiger_API.Modell;

namespace tiger_API.Itreface
{
    public interface IAuditService
    {
        Task LogAsync(int userId, string action, string? entity = null, string? details = null);
        Task<List<UserActivityLog>> GetAllUsersLogsAsync();
        Task<List<UserActivityLog>> GetLogsByIdUser(int userId);
        Task ClearAllLogsAsync();
        Task TrimLogsAsync(int keepCount = 100);
    }
}
