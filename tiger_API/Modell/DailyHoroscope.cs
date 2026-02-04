namespace tiger_API.Modell
{
    public class DailyHoroscope
    {
        public string ForecastDate { get; set; } = string.Empty;          // "Прогноз на 4 февраля · Обновлено 04.02.2026 в 00:00"
        public DateTime? UpdatedAt { get; set; }                          // попытка распарсить время обновления
        public IReadOnlyDictionary<string, ZodiacForecast> Signs { get; set; }
            = new Dictionary<string, ZodiacForecast>();
    }
}
