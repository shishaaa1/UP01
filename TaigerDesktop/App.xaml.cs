using System.ComponentModel;
using System.Runtime.CompilerServices;
using System.Windows;
using TaigerDesktop.Connect;

namespace TaigerDesktop
{
    public partial class App : Application, INotifyPropertyChanged
    {
        // Экземплярные поля (будут храниться в App.Instance)
        private string _currentAdminLogin;
        private string _currentAdminName;

        // ЭКЗЕМПЛЯРНЫЕ СВОЙСТВА — видны для привязки
        public string CurrentAdminLogin
        {
            get => _currentAdminLogin;
            set
            {
                _currentAdminLogin = value;
                OnPropertyChanged();
            }
        }

        public string CurrentAdminName
        {
            get => _currentAdminName;
            set
            {
                _currentAdminName = value;
                OnPropertyChanged();
            }
        }

        // Статический доступ к экземпляру
        public static App Instance { get; private set; }

        // Контекст API
        public static ApiContext ApiContext { get; private set; }

        protected override void OnStartup(StartupEventArgs e)
        {
            base.OnStartup(e);
            Instance = this;
            ApiContext = new ApiContext();
        }

        protected override void OnExit(ExitEventArgs e)
        {
            ApiContext?.Logout();
            base.OnExit(e);
        }

        // Статический метод для установки данных из любого места
        public static void SetAdminData(string login, string nickname)
        {
            if (Instance != null)
            {
                Instance.CurrentAdminLogin = login;
                Instance.CurrentAdminName = nickname;
            }
        }

        public static void ClearAdminData()
        {
            if (Instance != null)
            {
                Instance.CurrentAdminLogin = null;
                Instance.CurrentAdminName = null;
            }
        }

        // Реализация INotifyPropertyChanged
        public event PropertyChangedEventHandler PropertyChanged;

        private void OnPropertyChanged([CallerMemberName] string propertyName = null)
        {
            PropertyChanged?.Invoke(this, new PropertyChangedEventArgs(propertyName));
        }
    }
}