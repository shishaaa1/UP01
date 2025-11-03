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
    public partial class CheckUsers : Page
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
        public async void LoadUsers()
        {
            try
            {
                var api = new TaigerDesktop.Connect.ApiContext();
                var users = await api.GetAllUsersAsync();

                MessageBox.Show($"API вернул: {users?.Count ?? 0} пользователей");
                AllUsers = new ObservableCollection<Users>(users);
            }
            catch (Exception ex)
            {
                MessageBox.Show("ОШИБКА API: " + ex.Message);
            }
        }
        private void OnUserDeleted(Users user)
        {
            RemoveUser(user);
        }
        public CheckUsers()
        {
            InitializeComponent();
            DataContext = this;
            LoadUsers();
        }

        private void ApplyFilter()
        {
            if (AllUsers == null) return;

            if (string.IsNullOrWhiteSpace(SearchQuery))
            {
                FilteredUsers = new ObservableCollection<Users>(AllUsers);
            }
            else
            {
                var filtered = AllUsers.Where(u =>
                    u.FirstName?.Contains(SearchQuery, StringComparison.OrdinalIgnoreCase) == true ||
                    u.LastName?.Contains(SearchQuery, StringComparison.OrdinalIgnoreCase) == true ||
                    u.Id.ToString().Contains(SearchQuery) ||
                    u.Login?.Contains(SearchQuery, StringComparison.OrdinalIgnoreCase) == true
                ).ToList();

                FilteredUsers = new ObservableCollection<Users>(filtered);
            }
        }

        // Метод для удаления пользователя из списка
        public void RemoveUser(Users user)
        {
            AllUsers?.Remove(user);
            // Фильтр применится автоматически благодаря привязке данных
        }

        public event PropertyChangedEventHandler PropertyChanged;

        protected virtual void OnPropertyChanged([CallerMemberName] string propertyName = null)
        {
            PropertyChanged?.Invoke(this, new PropertyChangedEventArgs(propertyName));
        }
    }
}
