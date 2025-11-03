using Microsoft.EntityFrameworkCore;
using System.Reflection.Metadata;
using tiger_API.Modell;

namespace tiger_API.Context
{
    public class PhotosUserContext : DbContext
    {
        public DbSet<PhotosUsers> Photos { get; set; }
        public DbSet<Users> Users { get; set; }

        public PhotosUserContext()
        {
            Database.EnsureCreated();
            Photos.Load();
            Users.Load();
        }
        protected override void OnConfiguring(DbContextOptionsBuilder optionsBuilder)
        {
            optionsBuilder.UseSqlServer(DbConnection.config);
        }
    }
}
