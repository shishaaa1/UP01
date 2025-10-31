using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.ComponentModel;
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

namespace TaigerDesktop.Pages
{
    /// <summary>
    /// Логика взаимодействия для CheckUsers.xaml
    /// </summary>
    public partial class CheckUsers : Page
    {
        private readonly ApiContext _api = new();
        private string _searchQuery = "";
        private List<Users> _allUsers = new();
        public ObservableCollection<Users> FilteredUsers { get; set; } = new();
        public string SearchQuery
        {
            get => _searchQuery;
            set
            {
                if (_searchQuery != value)
                {
                    _searchQuery = value;
                    OnPropertyChanged();
                    FilterUsers(); // фильтруем при каждом изменении
                }
            }
        }

        public CheckUsers()
        {
            InitializeComponent();
            DataContext = this; // привязка к самому себе
            Loaded += OnLoaded;
        }

        private async void OnLoaded(object sender, System.Windows.RoutedEventArgs e)
        {
            await LoadUsers();
        }

        private async Task LoadUsers()
        {
            var users = await _api.GetAllUsersAsync();
            _allUsers = users ?? new List<Users>();
            FilterUsers(); // показать всех изначально
        }

        private void FilterUsers()
        {
            var query = SearchQuery?.Trim().ToLower() ?? "";

            FilteredUsers.Clear();

            if (string.IsNullOrEmpty(query))
            {
                // Показать всех
                foreach (var user in _allUsers)
                    FilteredUsers.Add(user);
            }
            else
            {
                // Фильтрация по ID, имени, фамилии или логину
                var filtered = _allUsers.Where(u =>
                    u.Id.ToString() == query || // точное совпадение по ID
                    (u.FirstName != null && u.FirstName.ToLower().Contains(query)) ||
                    (u.LastName != null && u.LastName.ToLower().Contains(query)) ||
                    (u.Login != null && u.Login.ToLower().Contains(query))
                );

                foreach (var user in filtered)
                    FilteredUsers.Add(user);
            }
        }

        // INotifyPropertyChanged
        public event PropertyChangedEventHandler PropertyChanged;
        protected virtual void OnPropertyChanged([System.Runtime.CompilerServices.CallerMemberName] string propertyName = null)
        {
            PropertyChanged?.Invoke(this, new PropertyChangedEventArgs(propertyName));
        }
        public void RemoveUser(Users user)
        {
            _allUsers.Remove(user);
            FilterUsers(); // перезапустить фильтрацию
        }
    }
}
