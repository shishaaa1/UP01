using Microsoft.EntityFrameworkCore;
using tiger_API.Modell;

namespace tiger_API.Context
{
    public class PhotosUserContext : DbContext
    {
        public DbSet<PhotosUsers> Photos { get; set; }

        public PhotosUserContext()
        {
            Database.EnsureCreated();
            Photos.Load();
        }
        protected override void OnConfiguring(DbContextOptionsBuilder optionsBuilder)
        {
            optionsBuilder.UseSqlServer(DbConnection.config);
        }
    }
}
