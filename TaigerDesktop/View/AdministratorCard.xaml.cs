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
            // Подписываемся на событие удаления через родительскую страницу
            if (ParentPage() is CheckAdministrator checkAdmin)
            {
                AdministratorDeleted += checkAdmin.RemoveAdmin;
            }
        }

        private void EditAdministrator(object sender, RoutedEventArgs e)
        {
            if (DataContext is Admin admin)
            {
                var editPage = new AddAdministrator(admin);

                NavigateTo(editPage);
            }
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

        // Единый метод для навигации — как в UserCard, но вынесем отдельно
        private void NavigateTo(Page page)
        {
            var parentPage = ParentPage();
            if (parentPage?.NavigationService != null)
            {
                parentPage.NavigationService.Navigate(page);
                return;
            }

            var window = Window.GetWindow(this);
            if (window == null) return;

            // Ищем Frame в визуальном дереве
            var frame = FindFrame(window);
            if (frame != null)
            {
                frame.Navigate(page);
            }
            else
            {
                MessageBox.Show("Не удалось найти Frame для навигации.", "Ошибка", MessageBoxButton.OK, MessageBoxImage.Warning);
            }
        }

        private Frame FindFrame(DependencyObject root)
        {
            for (int i = 0; i < VisualTreeHelper.GetChildrenCount(root); i++)
            {
                var child = VisualTreeHelper.GetChild(root, i);
                if (child is Frame frame)
                    return frame;

                frame = FindFrame(child);
                if (frame != null)
                    return frame;
            }
            return null;
        }

        // Общий метод поиска родительской страницы
        private Page ParentPage()
        {
            var window = Window.GetWindow(this);
            if (window?.Content is Page page)
                return page;

            DependencyObject parent = this;
            while (parent != null)
            {
                if (parent is Page p)
                    return p;
                parent = VisualTreeHelper.GetParent(parent);
            }

            return null;
        }

        // Событие удаления
        public event Action<Admin> AdministratorDeleted;
    }

}

