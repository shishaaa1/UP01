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
using TaigerDesktop.Connect;
using TaigerDesktop.Models;

namespace TaigerDesktop.Pages
{
    /// <summary>
    /// Логика взаимодействия для AddAdministrator.xaml
    /// </summary>
    public partial class AddAdministrator : Page
    {
        private readonly ApiContext _apiContext;
        public AddAdministrator()
        {
            InitializeComponent();
            _apiContext=new ApiContext();
        }

        private async void Button_Click(object sender, RoutedEventArgs e)
        {
            await AddAdmin();
        }
        private async Task AddAdmin()
        {
            if (string.IsNullOrWhiteSpace(Login.Text) ||
                string.IsNullOrWhiteSpace(Password.Password) ||
                string.IsNullOrWhiteSpace(Name.Text))
            {
                MessageBox.Show("Заполните все обязательные поля!", "Ошибка",
                    MessageBoxButton.OK, MessageBoxImage.Warning);
                return;
            }
            var admin = new Admin
            {
                Login = Login.Text.Trim(),
                Password = Password.Password,
                Nickname = Name.Text.Trim()
            };

            try
            {
                bool success = await _apiContext.AddAdminAsync(admin);

                if (success)
                {
                    MessageBox.Show("Администратор успешно добавлен!", "Успех",
                        MessageBoxButton.OK, MessageBoxImage.Information);

                    Login.Text = "";
                    Password.Password = "";
                    Name.Text = "";
                }
                else
                {
                    MessageBox.Show("Не удалось добавить администратора. Проверьте данные.", "Ошибка",
                        MessageBoxButton.OK, MessageBoxImage.Error);
                }
            }
            catch (Exception ex)
            {
                MessageBox.Show($"Произошла ошибка: {ex.Message}", "Ошибка",
                    MessageBoxButton.OK, MessageBoxImage.Error);
            }
        }
    }
}

