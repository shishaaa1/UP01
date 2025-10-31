using System.Configuration;
using System.Data;
using System.Windows;
using TaigerDesktop.Connect;

namespace TaigerDesktop
{
    /// <summary>
    /// Interaction logic for App.xaml
    /// </summary>
    public partial class App : Application
    {
        public static string CurrentAdminLogin { get; set; }
        public static ApiContext ApiContext { get; private set; }

        protected override void OnStartup(StartupEventArgs e)
        {
            base.OnStartup(e);
            // Создаем единственный экземпляр ApiContext для всего приложения
            ApiContext = new ApiContext();
        }

        protected override void OnExit(ExitEventArgs e)
        {
            ApiContext?.Logout();
            base.OnExit(e);
        }
    }

}
