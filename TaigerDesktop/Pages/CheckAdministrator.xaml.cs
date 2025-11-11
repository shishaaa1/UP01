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
        private ObservableCollection<Admin> _filteredAdmins;
        private string _searchQuery;
        public ObservableCollection<Admin> AllAdmins
        {
            get => _allAdmins;
            set
            {
                _allAdmins = value;
                OnPropertyChanged();
                ApplyFilter(); // Обновляем фильтр при загрузке
            }
        }

        public ObservableCollection<Admin> FilteredAdmins
        {
            get => _filteredAdmins;
            set
            {
                _filteredAdmins = value;
                OnPropertyChanged();
            }
        }

        public string SearchQuery
        {
            get => _searchQuery;
            set
            {
                _searchQuery = value;
                OnPropertyChanged();
                ApplyFilter();
            }
        }

        public CheckAdministrator()
        {
            InitializeComponent();
            DataContext = this;
            LoadAdmins();

        }
        private async void LoadAdmins()
        {
            try
            {
                var api = new ApiContext();
                var admins = await api.GetAllAdminsAsync();

                AllAdmins = new ObservableCollection<Admin>(admins ?? new List<Admin>());
                ApplyFilter();
            }
            catch (Exception ex)
            {
                MessageBox.Show($"Ошибка загрузки администраторов: {ex.Message}", "Ошибка",
                    MessageBoxButton.OK, MessageBoxImage.Error);
                AllAdmins = new ObservableCollection<Admin>();
            }
        }

        private void ApplyFilter()
        {
            if (AllAdmins == null)
            {
                FilteredAdmins = new ObservableCollection<Admin>();
                return;
            }

            if (string.IsNullOrWhiteSpace(SearchQuery))
            {
                FilteredAdmins = new ObservableCollection<Admin>(AllAdmins);
            }
            else
            {
                var filtered = AllAdmins.Where(a =>
                    (a.Login?.Contains(SearchQuery, StringComparison.OrdinalIgnoreCase) == true) ||
                    (a.Nickname?.Contains(SearchQuery, StringComparison.OrdinalIgnoreCase) == true) ||
                    (a.Id.ToString().Contains(SearchQuery))
                ).ToList();

                FilteredAdmins = new ObservableCollection<Admin>(filtered);
            }
        }

        public void RemoveAdmin(Admin admin)
        {
            AllAdmins?.Remove(admin);
            FilteredAdmins?.Remove(admin);
        }

        public event PropertyChangedEventHandler PropertyChanged;

        protected virtual void OnPropertyChanged([CallerMemberName] string propertyName = null)
        {
            PropertyChanged?.Invoke(this, new PropertyChangedEventArgs(propertyName));
        }

    }
}
