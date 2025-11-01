using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
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
using TaigerDesktop.Connect;
using TaigerDesktop.Models;
using TaigerDesktop.View;

namespace TaigerDesktop.Pages
{
    /// <summary>
    /// Логика взаимодействия для CheckPhotos.xaml
    /// </summary>
    public partial class CheckPhotos : Page
    {
        private readonly ApiContext _api = new();
        public ObservableCollection<PhotosUsers> UserPhotos { get; set; } = new();

        public CheckPhotos()
        {
            InitializeComponent();
            DataContext = this; 
            Loaded += OnLoaded;
        }

        private async void OnLoaded(object sender, System.Windows.RoutedEventArgs e)
        {
            await LoadPhotos();
        }
        public void RemovePhoto(PhotosUsers photo)
        {
            UserPhotos.Remove(photo);
        }
        private async Task LoadPhotos()
        {
            var photos = await _api.GetPhotosByUsersIdAsync();
            UserPhotos.Clear();
            foreach (var photo in photos)
            {
                UserPhotos.Add(photo);
            }
        }
    }
}
