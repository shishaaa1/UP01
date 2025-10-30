using Microsoft.EntityFrameworkCore;
using tiger_API.Modell;

namespace tiger_API.Context
{
    public class UsersContext : DbContext
    {
        public DbSet<Users> users {  get; set; }

        public UsersContext()
        {
            Database.EnsureCreated();
            users.Load();
        }

        protected override void OnConfiguring(DbContextOptionsBuilder optionsBuilder)
        {
            optionsBuilder.UseMySql("", new MySqlServerVersion(new Version(8,0,11)));
        }
    }
}
