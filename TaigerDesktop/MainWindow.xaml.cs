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

namespace TaigerDesktop
{
    /// <summary>
    /// Interaction logic for MainWindow.xaml
    /// </summary>
    public partial class MainWindow : Window
    {
        public MainWindow()
        {
            InitializeComponent();
            // Загружаем начальную страницу, например, статистику
            MainFrame.Navigate(new Authorisation());
        }

        // Показать меню после авторизации
        public void ShowMenu()
        {
            MenuBorder.Visibility = Visibility.Visible;
            // Растягиваем колонку меню
            MenuColumn.Width = new GridLength(240);
        }

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
                }
            }
        }

        private void OnLogoutClick(object sender, RoutedEventArgs e)
        {
            var result = MessageBox.Show("Вы уверены, что хотите выйти?", "Выход",
                MessageBoxButton.YesNo, MessageBoxImage.Question);
            if (result == MessageBoxResult.Yes)
            {
                // Скрываем меню
                MenuBorder.Visibility = Visibility.Collapsed;
                MenuColumn.Width = new GridLength(0);

                // Очищаем логин
                App.CurrentAdminLogin = null;

                // Возвращаемся на авторизацию
                MainFrame.Navigate(new Authorisation());
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
