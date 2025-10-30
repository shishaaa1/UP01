using Microsoft.EntityFrameworkCore.Metadata.Conventions;

namespace tiger_API.Modell
{
    public class IsLike
    {
        public int Id {  get; set; }
        public int FromUser { get; set; }
        public int ToUser { get; set; }
        public bool isLike { get; set; }
        public DateTime CreateAt { get; set; }
    }
}
