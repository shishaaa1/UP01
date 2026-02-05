using tiger_API.Modell;

public class DailyHoroscope
{
    public string ForecastDate { get; set; } = string.Empty;
    public DateTime? UpdatedAt { get; set; }
    public Dictionary<string, ZodiacForecast> Signs { get; set; } = new();
}