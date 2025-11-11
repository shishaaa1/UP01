using System.Text;
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

namespace TaigerDesktop
{
    /// <summary>
    /// Interaction logic for MainWindow.xaml
    /// </summary>
    public partial class MainWindow : Window
    {
        public ApiContext ApiContext => App.ApiContext;
        public MainWindow()
        {
            InitializeComponent();
            // Загружаем начальную страницу, например, статистику
            HideMenu();

            // Переходим на страницу авторизации
            MainFrame.Navigate(new Authorisation());
        }

        // Метод для выхода (можно привязать к кнопке выхода)
        public void Logout()
        {
            ApiContext.Logout();
            HideMenu();
            MainFrame.Navigate(new Authorisation());
        }

        // Показываем меню после авторизации
        public void ShowMenu()
        {
            // Показываем элементы меню
            MenuBorder.Visibility = Visibility.Visible;
            BthHome.Visibility = Visibility.Visible;
            BtnUsers.Visibility = Visibility.Visible;
            BtnAddAdmin.Visibility = Visibility.Visible;
            // Добавьте другие кнопки меню по необходимости
        }

        // Скрываем меню при выходе
        public void HideMenu()
        {
            MenuBorder.Visibility = Visibility.Collapsed;
            BthHome.Visibility = Visibility.Collapsed;
            BtnUsers.Visibility = Visibility.Collapsed;
            BtnAddAdmin.Visibility = Visibility.Collapsed;
        }

        // Показать меню после авторизации
       

        private void NavigateToPage(object sender, RoutedEventArgs e)
        {
            if (sender is Button button)
            {
                SetActiveButton(button);
                switch (button.Name)
                {
                    case "BthHome":
                        MainFrame.Navigate(new Pages.HomePage());
                        break;
                    case "BtnStatistics":
                        MainFrame.Navigate(new Pages.CheckStat());
                        break;
                    case "BtnPhotos":
                        MainFrame.Navigate(new Pages.CheckPhotos());
                        break;
                    case "BtnUsers":
                        MainFrame.Navigate(new Pages.CheckUsers());
                        break;
                    case "BtnAddAdmin":
                        MainFrame.Navigate(new Pages.AddAdministrator());
                        break;
                    case "BtnAllAdmin":
                        MainFrame.Navigate(new Pages.CheckAdministrator());
                        break;
                }
            }
        }

        private void OnLogoutClick(object sender, RoutedEventArgs e)
        {
            var result = MessageBox.Show("Вы уверены, что хотите выйти?", "Выход",
        MessageBoxButton.YesNo, MessageBoxImage.Question);
            if (result == MessageBoxResult.Yes)
            {
                Logout(); // Используем единый метод выхода
            }
        }

        public void SetActiveButton(Button activeButton)
        {
            var buttons = new[] { BthHome, BtnStatistics, BtnPhotos, BtnUsers, BtnAddAdmin };
            foreach (var btn in buttons)
            {
                btn.Style = (Style)FindResource("MenuItemButtonStyle");
            }
            if (activeButton != null)
                activeButton.Style = (Style)FindResource("ActiveMenuItemStyle");
        }
    }
}
