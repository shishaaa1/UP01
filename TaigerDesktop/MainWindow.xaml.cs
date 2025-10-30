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
            SetActiveButton(BtnStatistics);
        }

        private void NavigateToPage(object sender, RoutedEventArgs e)
        {
            if (sender is Button button)
            {
                SetActiveButton(button);

                switch (button.Name)
                {
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
                    case "BthHome":
                        MainFrame.Navigate(new Pages.HomePage());
                        break;
                }
            }
        }

        private void OnLogoutClick(object sender, RoutedEventArgs e)
        {
            // Здесь можно добавить подтверждение или очистку сессии
            var result = MessageBox.Show("Вы уверены, что хотите выйти?", "Выход",
                MessageBoxButton.YesNo, MessageBoxImage.Question);
            if (result == MessageBoxResult.Yes)
            {
                // Возвращаемся на страницу авторизации
                var loginWindow = new Authorisation(); // или как у тебя называется окно авторизации
                //loginWindow.Show();
                this.Close();
            }
        }

        // Визуальная индикация активного пункта
        private void SetActiveButton(Button activeButton)
        {
            // Сброс всех кнопок к базовому стилю
            foreach (var btn in new[] { BtnStatistics, BtnPhotos, BtnUsers, BtnAddAdmin })
            {
                btn.Style = (Style)FindResource("MenuItemButtonStyle");
            }

            // Установка активного стиля
            activeButton.Style = (Style)FindResource("ActiveMenuItemStyle");
        }
    }
}
