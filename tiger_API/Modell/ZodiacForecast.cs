namespace tiger_API.Modell
{
    public class ZodiacForecast
    {
        public string SignName { get; set; } = string.Empty;              // "Овнов", "Тельцов"...
        public string DateRange { get; set; } = string.Empty;             // "21 марта - 19 апреля"
        public string Text { get; set; } = string.Empty;                  // основной текст гороскопа
    }
}
