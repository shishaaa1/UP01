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
using TaigerDesktop.Connect;
using TaigerDesktop.Models;

namespace TaigerDesktop.Pages
{
    /// <summary>
    /// Логика взаимодействия для HoroscopePage.xaml
    /// </summary>
    public partial class HoroscopePage : UserControl
    {
        private readonly ApiContext _apiContext;
        private DailyHoroscope? _currentHoroscope;
        public HoroscopePage()
        {
            InitializeComponent();
            _apiContext = App.ApiContext;
            LoadHoroscope();
        }

        private async void LoadHoroscope()
        {
            ShowLoading(true);
            ErrorOverlay.Visibility = Visibility.Collapsed;

            try
            {
                var horoscope = await _apiContext.GetHoroscopeAsync();

                if (horoscope != null && horoscope.Signs.Any())
                {
                    _currentHoroscope = horoscope;

                    // Обновляем заголовок
                    ForecastDateText.Text = horoscope.ForecastDate;

                    if (horoscope.UpdatedAt.HasValue)
                    {
                        UpdatedAtText.Text = $"Обновлено: {horoscope.UpdatedAt.Value:dd.MM.yyyy HH:mm}";
                        UpdatedAtText.Visibility = Visibility.Visible;
                    }
                    else
                    {
                        UpdatedAtText.Visibility = Visibility.Collapsed;
                    }

                    // Сортируем знаки зодиака в правильном порядке
                    var orderedSigns = OrderZodiacSigns(horoscope.Signs.Values.ToList());
                    HoroscopeItemsControl.ItemsSource = orderedSigns;

                    LoadingOverlay.Visibility = Visibility.Collapsed;
                }
                else
                {
                    ShowError();
                }
            }
            catch (Exception ex)
            {
                Console.WriteLine($"Ошибка загрузки гороскопа: {ex.Message}");
                ShowError();
            }
            finally
            {
                ShowLoading(false);
            }
        }

        private void RefreshHoroscope(object sender, System.Windows.RoutedEventArgs e)
        {
            LoadHoroscope();
        }

        private void ShowLoading(bool isLoading)
        {
            LoadingOverlay.Visibility = isLoading ? Visibility.Visible : Visibility.Collapsed;
        }

        private void ShowError()
        {
            LoadingOverlay.Visibility = Visibility.Collapsed;
            ErrorOverlay.Visibility = Visibility.Visible;
        }

        // Сортировка знаков зодиака в правильном порядке
        private List<ZodiacForecast> OrderZodiacSigns(List<ZodiacForecast> signs)
        {
            var zodiacOrder = new[]
            {
                "Овен", "Телец", "Близнецы", "Рак", "Лев", "Дева",
                "Весы", "Скорпион", "Стрелец", "Козерог", "Водолей", "Рыбы"
            };

            return signs
                .OrderBy(s =>
                {
                    var index = Array.FindIndex(zodiacOrder, z =>
                        s.SignName.Contains(z, StringComparison.OrdinalIgnoreCase));
                    return index >= 0 ? index : int.MaxValue;
                })
                .ToList();
        }
    }
}
