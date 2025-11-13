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
    /// Логика взаимодействия для UserCard.xaml
    /// </summary>
    public partial class UserCard : UserControl
    {
        public UserCard()
        {
            InitializeComponent();
            this.Loaded += UserCard_Loaded;
        }

        private void UserCard_Loaded(object sender, RoutedEventArgs e)
        {
            UserDeleted += ((CheckUsers)ParentPage()).RemoveUser;
        }
        private Page ParentPage()
        {
            if (Window.GetWindow(this) is Window window && window.Content is Page page)
            {
                return page;
            }
            DependencyObject parent = this;
            while (parent != null)
            {
                if (parent is Page p)
                    return p;
                parent = VisualTreeHelper.GetParent(parent);
            }

            return null;
        }
        public event Action<Users> UserDeleted;

        private async void DeleteUser(object sender, RoutedEventArgs e)
        {
            if (DataContext is Users user)
            {
                var result = MessageBox.Show(
                    $"Удалить пользователя {user.FirstName} {user.LastName}?",
                    "Подтверждение",
                    MessageBoxButton.YesNo,
                    MessageBoxImage.Question);

                if (result == MessageBoxResult.Yes)
                {
                    var api = new TaigerDesktop.Connect.ApiContext();
                    bool success = await api.DeleteUserAsync(user.Id);

                    if (success)
                    {
                        MessageBox.Show("Пользователь удалён.");
                        UserDeleted?.Invoke(user);
                    }
                    else
                    {
                        MessageBox.Show("Ошибка при удалении.");
                    }
                }
            }
        }
    }
}
