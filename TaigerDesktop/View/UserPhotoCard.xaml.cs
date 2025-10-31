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
using System.Windows.Navigation;
using System.Windows.Shapes;
using TaigerDesktop.Models;
using TaigerDesktop.Pages;

namespace TaigerDesktop.View
{
    /// <summary>
    /// Логика взаимодействия для UserPhotoCard.xaml
    /// </summary>
    public partial class UserPhotoCard : UserControl
    {
        public UserPhotoCard()
        {
            InitializeComponent();
        }
        private void OpenPhotoViewer(object sender, RoutedEventArgs e)
        {
            if (DataContext is PhotosUsers photo && photo.photos != null)
            {
                var viewer = new PhotoViewerWindow(photo.photos);
                viewer.ShowDialog();
            }
        }

        private async void DeletePhoto(object sender, RoutedEventArgs e)
        {
            if (DataContext is PhotosUsers photo)
            {
                var result = MessageBox.Show("Удалить фото?", "Подтверждение",
                    MessageBoxButton.YesNo, MessageBoxImage.Question);

                if (result == MessageBoxResult.Yes)
                {
                    var api = new TaigerDesktop.Connect.ApiContext();
                    bool success = await api.DeletePhotoAsync(photo.Id);

                    if (success)
                    {
                        MessageBox.Show("Фото удалено.");
                        // Уведомить родителя
                        if (VisualTreeHelper.GetParent(this) is DependencyObject parent)
                        {
                            var page = FindParent<CheckPhotos>(parent);
                            page?.RemovePhoto(photo);
                        }
                    }
                    else
                    {
                        MessageBox.Show("Ошибка при удалении.");
                    }
                }
            }
        }

        // Вспомогательный метод поиска родителя
        private static T FindParent<T>(DependencyObject child) where T : DependencyObject
        {
            while (child != null && !(child is T))
            {
                child = VisualTreeHelper.GetParent(child);
            }
            return child as T;
        }
    }
}
