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

namespace TaigerDesktop
{
    /// <summary>
    /// Логика взаимодействия для Authorisation.xaml
    /// </summary>
    public partial class Authorisation : Page
    {
        public Authorisation()
        {
            InitializeComponent();
        }
        private void LoginButton_Click(object sender, RoutedEventArgs e)
        {
            string login = Login.Text;
            string password = Password.Password;

            // Пример проверки (замени на свою логику)
            if (IsValidAdmin(login, password))
            {
                // Сохраняем логин
                App.CurrentAdminLogin = login;

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
            else
            {
                MessageBox.Show("Неверный логин или пароль", "Ошибка", MessageBoxButton.OK, MessageBoxImage.Error);
            }
        }

        private bool IsValidAdmin(string login, string password)
        {
            // Замени на реальную проверку (БД, API и т.д.)
            return login == "admin" && password == "123";
        }
    }
}
