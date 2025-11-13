using System.Windows;
using System.Windows.Controls;
using System.Windows.Input;
using TaigerDesktop.Connect;

namespace TaigerDesktop
{
    public partial class Authorisation : Page
    {
        private readonly ApiContext _apiContext;

        public Authorisation()
        {
            InitializeComponent();
            _apiContext = App.ApiContext;
            _apiContext.Logout();
        }

        private async void LoginButton_Click(object sender, RoutedEventArgs e)
        {
            await PerformLoginAsync();
        }

        private void HandleSuccessfulLogin(string login)
        {
            if (Window.GetWindow(this) is MainWindow mainWindow)
            {
                mainWindow.ShowMenu();
                mainWindow.MainFrame.Navigate(new Pages.HomePage());
                mainWindow.SetActiveButton(mainWindow.BthHome);
            }
        }

        private void ShowErrorMessage(string message)
        {
            MessageBox.Show(message, "Ошибка авторизации",
                MessageBoxButton.OK, MessageBoxImage.Error);
            Password.Password = string.Empty;
            Password.Focus();
        }

        private void SetLoginButtonState(bool isEnabled, string text)
        {
            LoginButton.Content = text;
            LoginButton.IsEnabled = isEnabled;
        }
        private void Login_KeyDown(object sender, System.Windows.Input.KeyEventArgs e)
        {
            if (e.Key == System.Windows.Input.Key.Enter)
            {
                Password.Focus();
            }
        }
        private async Task PerformLoginAsync()
        {
            string login = Login.Text.Trim();
            string password = Password.Password;

            if (string.IsNullOrEmpty(login) || string.IsNullOrEmpty(password))
            {
                MessageBox.Show("Введите логин и пароль", "Ошибка", MessageBoxButton.OK, MessageBoxImage.Warning);
                return;
            }

            SetLoginButtonState(isEnabled: false, text: "Вход...");

            try
            {
                bool isSuccess = await _apiContext.LoginAdminAAsync(login, password);

                if (isSuccess)
                {
                    HandleSuccessfulLogin(login);
                }
                else
                {
                    ShowErrorMessage("Неверный логин или пароль");
                }
            }
            catch (System.Net.Http.HttpRequestException ex)
            {
                ShowErrorMessage($"Ошибка подключения к серверу: {ex.Message}");
            }
            catch (Exception ex)
            {
                ShowErrorMessage($"Произошла ошибка: {ex.Message}");
            }
            finally
            {
                SetLoginButtonState(isEnabled: true, text: "Войти");
            }
        }
        private async void Password_KeyDown(object sender, KeyEventArgs e)
        {
            if (e.Key == Key.Enter)
            {
                await PerformLoginAsync();
            }
        }
    }
}