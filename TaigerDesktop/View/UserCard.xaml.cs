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
        }

        private void EditUser(object sender, RoutedEventArgs e)
        {
            if (DataContext is Users user)
            {
                // TODO: Открыть окно редактирования
                MessageBox.Show($"Редактировать пользователя: {user.FirstName} {user.LastName}");
            }
        }

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
                    bool success = await api.DeleteUser(user.Id);

                    if (success)
                    {
                        MessageBox.Show("Пользователь удалён.");
                        // Обновить список (через событие или ViewModel)
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
