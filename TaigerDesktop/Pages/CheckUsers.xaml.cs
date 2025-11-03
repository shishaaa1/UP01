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
    /// Логика взаимодействия для CheckUsers.xaml
    /// </summary>
    public partial class CheckUsers : Page, INotifyPropertyChanged
    {
        private ObservableCollection<Users> _allUsers;
        private ObservableCollection<Users> _filteredUsers;
        private string _searchQuery;

        public ObservableCollection<Users> AllUsers
        {
            get => _allUsers;
            set
            {
                _allUsers = value;
                OnPropertyChanged();
                ApplyFilter();
            }
        }

        public ObservableCollection<Users> FilteredUsers
        {
            get => _filteredUsers;
            set
            {
                _filteredUsers = value;
                OnPropertyChanged(); // ← критически важно!
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
        public CheckUsers()
        {
            InitializeComponent();
            DataContext = this;
            LoadUsers();
        }

        public async void LoadUsers()
        {
            try
            {
                var api = new ApiContext();
                var users = await api.GetAllUsersAsync();

                // Для отладки — можно убрать после проверки
                // System.Windows.MessageBox.Show($"Загружено: {users?.Count ?? 0} пользователей");

                AllUsers = users != null
                    ? new ObservableCollection<Users>(users)
                    : new ObservableCollection<Users>();
            }
            catch (Exception ex)
            {
                System.Windows.MessageBox.Show($"Ошибка загрузки: {ex.Message}", "Ошибка",
                    System.Windows.MessageBoxButton.OK, System.Windows.MessageBoxImage.Error);
                AllUsers = new ObservableCollection<Users>();
            }
        }

        private void ApplyFilter()
        {
            if (AllUsers == null)
            {
                FilteredUsers = new ObservableCollection<Users>();
                return;
            }

            if (string.IsNullOrWhiteSpace(SearchQuery))
            {
                FilteredUsers = new ObservableCollection<Users>(AllUsers);
            }
            else
            {
                var filtered = AllUsers.Where(u =>
                    (u.FirstName?.Contains(SearchQuery, StringComparison.OrdinalIgnoreCase) == true) ||
                    (u.LastName?.Contains(SearchQuery, StringComparison.OrdinalIgnoreCase) == true) ||
                    (u.Id.ToString().Contains(SearchQuery)) ||
                    (u.Login?.Contains(SearchQuery, StringComparison.OrdinalIgnoreCase) == true)
                ).ToList();

                FilteredUsers = new ObservableCollection<Users>(filtered);
            }
        }

        // Метод вызывается из UserCard через FindParent
        public void RemoveUser(Users user)
        {
            AllUsers?.Remove(user);
            FilteredUsers?.Remove(user);
        }

        // INotifyPropertyChanged
        public event PropertyChangedEventHandler PropertyChanged;

        protected virtual void OnPropertyChanged([CallerMemberName] string propertyName = null)
        {
            PropertyChanged?.Invoke(this, new PropertyChangedEventArgs(propertyName));
        }
    }
}
