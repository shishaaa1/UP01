using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Text.RegularExpressions;
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
    /// Логика взаимодействия для ViewLogs.xaml
    /// </summary>
    public partial class ViewLogs : UserControl
    {
        private readonly ApiContext _apiContext;
        private List<UserActivityLog> _allLogs = new();
        public ViewLogs()
        {
            InitializeComponent();
            _apiContext = App.ApiContext;
            LoadAllLogs(null, null);
        }
        private async void LoadAllLogs(object sender, RoutedEventArgs e)
        {
            ShowLoading(true);
            try
            {
                var logs = await _apiContext.GetAllLogsAsync();
                _allLogs = logs;
                LogsGrid.ItemsSource = logs;
                UserIdFilter.Clear();
            }
            finally
            {
                ShowLoading(false);
            }
        }

        private async void LoadFilteredLogs(object sender, RoutedEventArgs e)
        {
            if (string.IsNullOrWhiteSpace(UserIdFilter.Text))
            {
                MessageBox.Show("Введите ID пользователя", "Внимание",
                    MessageBoxButton.OK, MessageBoxImage.Warning);
                return;
            }

            if (int.TryParse(UserIdFilter.Text, out int userId) && userId > 0)
            {
                ShowLoading(true);
                try
                {
                    var logs = await _apiContext.GetLogsByUserIdAsync(userId);
                    LogsGrid.ItemsSource = logs;
                    if (logs.Count == 0)
                    {
                        MessageBox.Show($"Логи для пользователя с ID {userId} не найдены", "Информация",
                            MessageBoxButton.OK, MessageBoxImage.Information);
                    }
                }
                finally
                {
                    ShowLoading(false);
                }
            }
            else
            {
                MessageBox.Show("Введите корректный числовой ID пользователя", "Ошибка",
                    MessageBoxButton.OK, MessageBoxImage.Error);
            }
        }

        private void RefreshLogs(object sender, RoutedEventArgs e)
        {
            if (string.IsNullOrWhiteSpace(UserIdFilter.Text))
                LoadAllLogs(null, null);
            else
                LoadFilteredLogs(null, null);
        }

        private void ShowLoading(bool isLoading)
        {
            LoadingOverlay.Visibility = isLoading ? Visibility.Visible : Visibility.Collapsed;
        }
        private void UserIdFilter_PreviewTextInput(object sender, System.Windows.Input.TextCompositionEventArgs e)
        {
            e.Handled = !Regex.IsMatch(e.Text, @"^\d+$");
        }
    }
}
