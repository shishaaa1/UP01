using Microsoft.EntityFrameworkCore;
using tiger_API.Modell;

namespace tiger_API.Context
{
    public class AdminContext : DbContext
    {
        public DbSet<Admin> Admin { get; set; }

        public AdminContext()
        {
            Database.EnsureCreated();
            Admin.Load();
        }
        protected override void OnConfiguring(DbContextOptionsBuilder optionsBuilder)
        {
            optionsBuilder.UseSqlServer(DbConnection.config);
        }
    }
}
