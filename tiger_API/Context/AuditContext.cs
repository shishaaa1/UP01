using Microsoft.EntityFrameworkCore;
using tiger_API.Modell;

namespace tiger_API.Context
{
    public class AuditContext : DbContext
    {
        public AuditContext(DbContextOptions<AuditContext> options) : base(options) { }

        public DbSet<UserActivityLog> UserActivityLogs { get; set; }
        public DbSet<Users> Users { get; set; }
        public AuditContext()
        {
            Database.EnsureCreated();
            UserActivityLogs.Load();
            Users.Load();
        }
    }

}
