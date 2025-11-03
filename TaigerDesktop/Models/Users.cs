using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Media.Imaging;

namespace TaigerDesktop.Models
{
    public class Users
    {
        public int Id { get; set; }
        public string FirstName { get; set; }
        public string LastName { get; set; }
        public DateTime Birthday { get; set; }
        public string BIO { get; set; }
        public DateTime CreatedAt { get; set; }
        public string Login { get; set; }
        public string Password { get; set; }
        public DateTime BirthDay => Birthday;
        public bool Sex { get; set; }
        public string AvatarPath { get; set; }
        private BitmapImage ByteArrayToImage(byte[] byteArray)
        {
            if (byteArray == null || byteArray.Length == 0) return null;

            var image = new BitmapImage();
            using (var stream = new System.IO.MemoryStream(byteArray))
            {
                stream.Position = 0;
                image.BeginInit();
                image.CacheOption = BitmapCacheOption.OnLoad;
                image.StreamSource = stream;
                image.EndInit();
                image.Freeze();
            }
            return image;
        }
    }
}

