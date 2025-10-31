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
            _apiContext = new ApiContext();
        }

        private async void Button_Click(object sender, RoutedEventArgs e)
        {
            await AddAdmin();
        }
        private async Task AddAdmin()
        {
            // Валидация
            if (string.IsNullOrWhiteSpace(Name.Text))
            {
                MessageBox.Show("Введите имя (никнейм).", "Ошибка", MessageBoxButton.OK, MessageBoxImage.Warning);
                return;
            }

            if (string.IsNullOrWhiteSpace(Login.Text))
            {
                MessageBox.Show("Введите логин.", "Ошибка", MessageBoxButton.OK, MessageBoxImage.Warning);
                return;
            }

            if (string.IsNullOrWhiteSpace(Password.Password))
            {
                MessageBox.Show("Введите пароль.", "Ошибка", MessageBoxButton.OK, MessageBoxImage.Warning);
                return;
            }

            // Создаём объект
            var newAdmin = new Admin
            {
                Nickname = Name.Text.Trim(),
                Login = Login.Text.Trim(),
                Password = Password.Password.Trim()
            };

            // Отключаем кнопку, чтобы избежать повторных нажатий
            addButt.IsEnabled = false;

            try
            {
                Admin result = await _apiContext.AddAdminAsync(newAdmin);

                if (result != null)
                {
                    MessageBox.Show("Администратор успешно добавлен!", "Успех", MessageBoxButton.OK, MessageBoxImage.Information);
                    // Очищаем форму
                    Name.Clear();
                    Login.Clear();
                    Password.Clear();
                }
                else
                {
                    MessageBox.Show("Не удалось добавить администратора.\nВозможно, логин уже существует.", "Ошибка", MessageBoxButton.OK, MessageBoxImage.Error);
                }
            }
            catch (Exception ex)
            {
                MessageBox.Show($"Произошла ошибка: {ex.Message}", "Ошибка", MessageBoxButton.OK, MessageBoxImage.Error);
            }
            finally
            {
                addButt.IsEnabled = true; // Включаем кнопку обратно
            }
        }
    }
}

