using Microsoft.EntityFrameworkCore;

namespace tiger_API
{
    public class DbConnection : DbContext
    {
        //public static readonly string config = "Server=DESKTOP-U29GOO8\\SQLEXPRESS;Trusted_Connection=True;Database=tinder;TrustServerCertificate=True;";
        public static readonly string config = "Server=10.0.201.112;TrustServerCertificate=True;Database=base2_ISP_22_4_1;User=ISP_22_4_1;PWD=ck2PjQQBIo15_";
    }
}
