using Microsoft.EntityFrameworkCore.Metadata.Conventions;

namespace tiger_API.Modell
{
    public class Islike
    {
        public int Id {  get; set; }
        public int FromUserid { get; set; }
        public int ToUserid { get; set; }
        public bool IsLike { get; set; }
        public DateTime CreatedAt { get; set; }
    }
}
