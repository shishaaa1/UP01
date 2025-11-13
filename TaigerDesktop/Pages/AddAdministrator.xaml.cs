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
        private Admin _editingAdmin = null;
        public AddAdministrator()
        {
            InitializeComponent();
            _apiContext = new ApiContext();
            InitializeAddMode();
        }
        public AddAdministrator(Admin adminToEdit) : this()
        {
            _editingAdmin = adminToEdit;
            InitializeEditMode();
        }
        private void InitializeAddMode()
        {
            title.Text = "Добавить администратора";
            addButt.Content = "Добавить администратора";
            tName.Text = "Имя";
            tPassword.Text = "Пароль";
            Name.Clear();
            Login.Clear();
            Password.Clear();
            ToolTipService.SetToolTip(Name, "Введите имя нового администратора");
            ToolTipService.SetToolTip(Login, "Введите уникальный логин");
            ToolTipService.SetToolTip(Password, "Введите пароль (минимум 8 символов)");
        }

        private void InitializeEditMode()
        {
            title.Text = "Редактировать администратора";
            addButt.Content = "Сохранить изменения";
            tName.Text = "Имя(Введите новый Nickname)";
            tPassword.Text = "Пароль(Введите новый пароль)";
            Name.Text = _editingAdmin.Nickname;
            Login.Text = _editingAdmin.Login;
            Password.Password = _editingAdmin.Password;
            ToolTipService.SetToolTip(Name, "Если не хотите менять Nickname — оставьте старый");
            ToolTipService.SetToolTip(Login, "Изменение логина может повлиять на авторизацию");
            ToolTipService.SetToolTip(Password, "Если не хотите менять пароль — введите старый");// Не рекомендуется, если пароль хешируется
        }
        private async void Button_Click(object sender, RoutedEventArgs e)
        {
            if (_editingAdmin != null)
            {
                await EditAdmin();
            }
            else
            {
                await AddAdmin();
            }
        }
        private async Task AddAdmin()
        {
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
            var newAdmin = new Admin
            {
                Nickname = Name.Text.Trim(),
                Login = Login.Text.Trim(),
                Password = Password.Password.Trim()
            };
            addButt.IsEnabled = false;

            try
            {
                Admin result = await _apiContext.AddAdminAsync(newAdmin);

                if (result != null)
                {
                    MessageBox.Show("Администратор успешно добавлен!", "Успех", MessageBoxButton.OK, MessageBoxImage.Information);
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
                addButt.IsEnabled = true;
            }
        }
        private async Task EditAdmin()
        {
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
            _editingAdmin.Nickname = Name.Text.Trim();
            _editingAdmin.Login = Login.Text.Trim();
            _editingAdmin.Password = Password.Password.Trim();

            addButt.IsEnabled = false;

            try
            {
                bool success = await _apiContext.EditAdminAsync(_editingAdmin);

                if (success)
                {
                    MessageBox.Show("Администратор успешно обновлён!", "Успех", MessageBoxButton.OK, MessageBoxImage.Information);
                    if (NavigationService.CanGoBack)
                        NavigationService.GoBack();
                }
                else
                {
                    MessageBox.Show("Не удалось обновить администратора.", "Ошибка", MessageBoxButton.OK, MessageBoxImage.Error);
                }
            }
            catch (Exception ex)
            {
                MessageBox.Show($"Произошла ошибка: {ex.Message}", "Ошибка", MessageBoxButton.OK, MessageBoxImage.Error);
            }
            finally
            {
                addButt.IsEnabled = true;
            }
        }
    }
}

