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

namespace TaigerDesktop.View
{
    /// <summary>
    /// Логика взаимодействия для PhotoViewerWindow.xaml
    /// </summary>
    public partial class PhotoViewerWindow : Window
    {
        public PhotoViewerWindow(string imagePath)
        {
            InitializeComponent();
            DataContext = imagePath;
        }

        private void CloseButton_Click(object sender, RoutedEventArgs e)
        {
            Close();
        }

        protected override void OnKeyDown(System.Windows.Input.KeyEventArgs e)
        {
            if (e.Key == System.Windows.Input.Key.Escape)
                Close();
            base.OnKeyDown(e);
        }
    }
}
