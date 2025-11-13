using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Data;
using System.Windows.Documents;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Imaging;
using System.Windows.Shapes;

namespace TaigerDesktop.Pages
{
    /// <summary>
    /// Логика взаимодействия для PhotoViewerWindow.xaml
    /// </summary>
    public partial class PhotoViewerWindow : Window
    {
        public PhotoViewerWindow(byte[] imageBytes)
        {
            InitializeComponent();
            if (imageBytes != null)
            {
                var bitmap = new BitmapImage();
                using (var stream = new System.IO.MemoryStream(imageBytes))
                {
                    bitmap.BeginInit();
                    bitmap.CacheOption = BitmapCacheOption.OnLoad;
                    bitmap.StreamSource = stream;
                    bitmap.EndInit();
                    bitmap.Freeze();
                }
                imageControl.Source = bitmap; 
            }
        }

        private void Close_Click(object sender, RoutedEventArgs e)
        {
            Close();
        }
    }
}
