using System.Windows;
using System.Windows.Controls;
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

            // Очищаем состояние при загрузке страницы авторизации
            _apiContext.Logout();
        }

        private async void LoginButton_Click(object sender, RoutedEventArgs e)
        {
            string login = Login.Text.Trim();
            string password = Password.Password;

            // Валидация
            if (string.IsNullOrEmpty(login) || string.IsNullOrEmpty(password))
            {
                MessageBox.Show("Введите логин и пароль", "Ошибка",
                    MessageBoxButton.OK, MessageBoxImage.Warning);
                return;
            }

            // Показываем индикатор загрузки
            SetLoginButtonState(isEnabled: false, text: "Вход...");

            try
            {
                // Авторизация через API
                bool isSuccess = await _apiContext.LoginAdminAsync(login, password);

                if (isSuccess)
                {
                    // Успешная авторизация
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

        private void HandleSuccessfulLogin(string login)
        {
            // Получаем MainWindow
            if (Window.GetWindow(this) is MainWindow mainWindow)
            {
                // Показываем меню
                mainWindow.ShowMenu();

                // Переходим на HomePage
                mainWindow.MainFrame.Navigate(new Pages.HomePage());

                // Активируем кнопку "Домой"
                mainWindow.SetActiveButton(mainWindow.BthHome);
            }
        }

        private void ShowErrorMessage(string message)
        {
            MessageBox.Show(message, "Ошибка авторизации",
                MessageBoxButton.OK, MessageBoxImage.Error);

            // Очищаем поле пароля и фокусируемся на нем
            Password.Password = string.Empty;
            Password.Focus();
        }

        private void SetLoginButtonState(bool isEnabled, string text)
        {
            LoginButton.Content = text;
            LoginButton.IsEnabled = isEnabled;
        }

        // Обработка нажатия Enter в полях ввода
        private void Login_KeyDown(object sender, System.Windows.Input.KeyEventArgs e)
        {
            if (e.Key == System.Windows.Input.Key.Enter)
            {
                Password.Focus();
            }
        }

        private void Password_KeyDown(object sender, System.Windows.Input.KeyEventArgs e)
        {
            if (e.Key == System.Windows.Input.Key.Enter)
            {
                LoginButton_Click(sender, e);
            }
        }
    }
}