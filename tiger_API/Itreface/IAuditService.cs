namespace tiger_API.Itreface
{
    public interface IAuditService
    {
        Task LogAsync(int userId, string action, string? entity = null, string? details = null);
    }
}
