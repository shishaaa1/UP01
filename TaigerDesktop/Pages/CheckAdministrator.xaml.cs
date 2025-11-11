using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.ComponentModel;
using System.Linq;
using System.Runtime.CompilerServices;
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

namespace TaigerDesktop.Pages
{
    /// <summary>
    /// Логика взаимодействия для CheckAdministrator.xaml
    /// </summary>
    public partial class CheckAdministrator : Page
    {
        private ObservableCollection<Admin> _allAdmins;
        private string _searchQuery;
        public CheckAdministrator()
        {
            InitializeComponent();
        }
        public ObservableCollection<Admin> AllAdmins
        {
            get => _allAdmins;
            set
            {
                _allAdmins = value;
                OnPropertyChanged();
            }
        }
        public void RemoveAdmin(Admin admin)
        {
            AllAdmins?.Remove(admin);
        }
        public async void LoadUsers()
        {
            try
            {
                var api = new ApiContext();
                var admins = await api.GetAllAdminsAsync();


                AllAdmins = admins != null
                    ? new ObservableCollection<Admin>(admins)
                    : new ObservableCollection<Admin>();
            }
            catch (Exception ex)
            {
                System.Windows.MessageBox.Show($"Ошибка загрузки: {ex.Message}", "Ошибка",
                    System.Windows.MessageBoxButton.OK, System.Windows.MessageBoxImage.Error);
                AllAdmins = new ObservableCollection<Admin>();
            }
        }
        public event PropertyChangedEventHandler PropertyChanged;

        protected virtual void OnPropertyChanged([CallerMemberName] string propertyName = null)
        {
            PropertyChanged?.Invoke(this, new PropertyChangedEventArgs(propertyName));
        }
    }
}
