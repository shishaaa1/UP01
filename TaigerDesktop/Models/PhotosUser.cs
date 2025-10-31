using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Media.Imaging;

namespace TaigerDesktop.Models
{
    public class PhotosUsers
    {
        public int Id { get; set; }
        public int UserId { get; set; }
        public byte[] photos { get; set; }

        // Дополнительные свойства для биндинга
        public string UserName { get; set; } // Имя пользователя для отображения
        public string Login { get; set; } // Логин пользователя

        public BitmapImage ImageSource => ByteArrayToImage(photos);

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
