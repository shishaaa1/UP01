using Microsoft.EntityFrameworkCore;
using tiger_API.Modell;

namespace tiger_API.Context
{
    public class iSLikeContext:DbContext
    {
        public DbSet<Islike> Islike { get; set; }
        public DbSet<Users> Users{ get; set; }

        public iSLikeContext()
        {
            Database.EnsureCreated();
            Users.Load();
            Islike.Load();
        }
        protected override void OnConfiguring(DbContextOptionsBuilder optionsBuilder)
        {
            optionsBuilder.UseSqlServer(DbConnection.config);
        }
    }
}
