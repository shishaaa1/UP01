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
using TaigerDesktop.Models;
using TaigerDesktop.Pages;

namespace TaigerDesktop.View
{
    /// <summary>
    /// Логика взаимодействия для AdministratorCard.xaml
    /// </summary>
    public partial class AdministratorCard : UserControl
    {
        public AdministratorCard()
        {
            InitializeComponent();
            this.Loaded += AdminCard_Loaded;
        }
        private void AdminCard_Loaded(object sender, RoutedEventArgs e)
        {
            // Подписываемся на событие удаления
            AdministratorDeleted += ((CheckAdministrator)ParentPage()).RemoveAdmin;
        }

        private void EditAdministrator(object sender, RoutedEventArgs e)
        {
            if (DataContext is Admin admin)
            {
                var editPage = new AddAdministrator(admin);

                // Попробуем получить Frame из родительской страницы
                var parentPage = ParentPage();
                if (parentPage != null && parentPage.NavigationService != null)
                {
                    parentPage.NavigationService.Navigate(editPage);
                }
                else
                {
                    // Попробуем получить Frame напрямую из окна
                    var window = Window.GetWindow(this);
                    if (window != null)
                    {
                        // Предположим, в окне есть Frame с именем MainFrame
                        var frame = window.FindName("MainFrame") as Frame;
                        if (frame != null)
                        {
                            frame.Navigate(editPage);
                        }
                        else
                        {
                            // Если Frame не найден, ищем через визуальное дерево
                            frame = FindFrameInVisualTree(window);
                            if (frame != null)
                            {
                                frame.Navigate(editPage);
                            }
                            else
                            {
                                MessageBox.Show("Не удалось найти Frame для навигации.", "Ошибка", MessageBoxButton.OK, MessageBoxImage.Warning);
                            }
                        }
                    }
                    else
                    {
                        MessageBox.Show("Не удалось найти окно для навигации.", "Ошибка", MessageBoxButton.OK, MessageBoxImage.Warning);
                    }
                }
            }
        }
        private Frame FindFrameInVisualTree(DependencyObject parent)
        {
            for (int i = 0; i < VisualTreeHelper.GetChildrenCount(parent); i++)
            {
                var child = VisualTreeHelper.GetChild(parent, i) as DependencyObject;
                if (child is Frame frame)
                {
                    return frame;
                }

                frame = FindFrameInVisualTree(child);
                if (frame != null)
                    return frame;
            }
            return null;
        }
        private async void DeleteAdmin(object sender, RoutedEventArgs e)
        {
            if (DataContext is Admin admin)
            {
                var result = MessageBox.Show(
                    $"Удалить пользователя {admin.Login} {admin.Nickname}?",
                    "Подтверждение",
                    MessageBoxButton.YesNo,
                    MessageBoxImage.Question);

                if (result == MessageBoxResult.Yes)
                {
                    var api = new TaigerDesktop.Connect.ApiContext();
                    bool success = await api.DeleteAdminAsync(admin.Id);

                    if (success)
                    {
                        MessageBox.Show("Пользователь удалён.");
                        AdministratorDeleted?.Invoke(admin);
                    }
                    else
                    {
                        MessageBox.Show("Ошибка при удалении.");
                    }
                }
            }
        }
        private Page ParentPage()
        {
            // Сначала ищем окно, потом его Content как Page
            if (Window.GetWindow(this) is Window window && window.Content is Page page)
            {
                return page;
            }

            // Если не нашли — ищем вверх по визуальному дереву
            DependencyObject parent = this;
            while (parent != null)
            {
                if (parent is Page p)
                    return p;
                parent = VisualTreeHelper.GetParent(parent);
            }

            return null;
        }
        public event Action<Admin> AdministratorDeleted;

    }
}
