using Microsoft.EntityFrameworkCore;

namespace tiger_API
{
    public class DbConnection : DbContext
    {
        public static readonly string config = "Server=DESKTOP-U29GOO8\\SQLEXPRESS;Trusted_Connection=True;Database=tinder;TrustServerCertificate=True;";
    }
}
