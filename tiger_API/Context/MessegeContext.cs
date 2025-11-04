using Microsoft.EntityFrameworkCore;
using tiger_API.Modell;

namespace tiger_API.Context
{
    public class MessegeContext : DbContext
    {
        public DbSet<Message> Messages { get; set; }
        public DbSet<Users> Users { get; set; }

        public MessegeContext()
        {
            Database.EnsureCreated();
            Messages.Load();
            Users.Load();
        }
        protected override void OnConfiguring(DbContextOptionsBuilder optionsBuilder)
        {
            optionsBuilder.UseSqlServer(DbConnection.config);
        }
    }
}
