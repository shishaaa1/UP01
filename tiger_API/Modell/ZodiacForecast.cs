public class ZodiacForecast
{
    public string SignName { get; set; } = string.Empty;
    public string DateRange { get; set; } = string.Empty;
    public string Text { get; set; } = string.Empty;
    // Новое поле для картинки
    public string? ImageUrl { get; set; }
}