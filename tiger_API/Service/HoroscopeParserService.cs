using System.Text.RegularExpressions;
using HtmlAgilityPack;
using tiger_API.Interfaces;
using tiger_API.Modell;

namespace tiger_API.Service
{
    public class HoroscopeParserService : IHoroscopeParser
    {
        private readonly HttpClient _httpClient;
        private readonly ILogger<HoroscopeParserService> _logger;
        private const string BaseUrl = "https://www.chita.ru/horoscope/";

        // 1. ВОТ ТОТ САМЫЙ СЛОВАРЬ. Теперь картинки будут всегда.
        private static readonly Dictionary<string, string> ZodiacStaticImages = new()
        {
            { "Овен", "https://img.icons8.com/color/96/aries.png" },
            { "Телец", "https://img.icons8.com/color/96/taurus.png" },
            { "Близнецы", "https://img.icons8.com/color/96/gemini.png" },
            { "Рак", "https://img.icons8.com/color/96/cancer.png" },
            { "Лев", "https://img.icons8.com/color/96/leo.png" },
            { "Дева", "https://img.icons8.com/color/96/virgo.png" },
            { "Весы", "https://img.icons8.com/color/96/libra.png" },
            { "Скорпион", "https://img.icons8.com/color/96/scorpio.png" },
            { "Стрелец", "https://img.icons8.com/color/96/sagittarius.png" },
            { "Козерог", "https://img.icons8.com/color/96/capricorn.png" },
            { "Водолей", "https://img.icons8.com/color/96/aquarius.png" },
            { "Рыбы", "https://img.icons8.com/color/96/pisces.png" }
        };

        private static readonly string[] ZodiacNames = ZodiacStaticImages.Keys.ToArray();

        public HoroscopeParserService(HttpClient httpClient, ILogger<HoroscopeParserService> logger)
        {
            _httpClient = httpClient;
            _logger = logger;
            _httpClient.DefaultRequestHeaders.UserAgent.ParseAdd("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36");
        }

        public async Task<DailyHoroscope?> GetTodayHoroscopeAsync(CancellationToken ct = default)
        {
            try
            {
                string html = await _httpClient.GetStringAsync(BaseUrl, ct);
                var htmlDoc = new HtmlDocument();
                htmlDoc.LoadHtml(html);

                var result = new DailyHoroscope
                {
                    UpdatedAt = DateTime.Now,
                    Signs = new Dictionary<string, ZodiacForecast>()
                };

                // Парсим дату с сайта
                var dateNode = htmlDoc.DocumentNode.SelectSingleNode("//div[contains(@class, 'pub-date')] | //time");
                result.ForecastDate = dateNode?.InnerText.Trim() ?? $"Прогноз на {DateTime.Now:dd.MM.yyyy}";

                var headers = htmlDoc.DocumentNode.SelectNodes("//h2 | //h3");
                if (headers == null) return null;

                var signsDict = new Dictionary<string, ZodiacForecast>();

                foreach (var header in headers)
                {
                    string headerText = header.InnerText.Trim();
                    var detectedSign = ZodiacNames.FirstOrDefault(z => headerText.Contains(z, StringComparison.OrdinalIgnoreCase));

                    if (string.IsNullOrEmpty(detectedSign)) continue;

                    var forecast = new ZodiacForecast
                    {
                        SignName = detectedSign,
                        // Берем картинку из нашего словаря по названию знака
                        ImageUrl = ZodiacStaticImages[detectedSign]
                    };

                    // Собираем контент после заголовка
                    var textParts = new List<string>();
                    var currentNode = header.NextSibling;

                    while (currentNode != null)
                    {
                        if (currentNode.Name == "h2" || currentNode.Name == "h3") break;

                        string innerText = currentNode.InnerText.Trim();
                        if (!string.IsNullOrWhiteSpace(innerText))
                        {
                            // Если строка похожа на диапазон дат "21 марта — 20 апреля"
                            if (Regex.IsMatch(innerText, @"\d{1,2}\s[а-я]+\s[—–-]\s\d{1,2}\s[а-я]+"))
                            {
                                forecast.DateRange = innerText;
                            }
                            // Если это не мусор, то это текст прогноза
                            else if (innerText.Length > 5 && !innerText.Contains("Показать полностью"))
                            {
                                textParts.Add(innerText);
                            }
                        }
                        currentNode = currentNode.NextSibling;
                    }

                    forecast.Text = string.Join("\n\n", textParts).Trim();

                    if (!string.IsNullOrEmpty(forecast.Text))
                    {
                        signsDict[detectedSign] = forecast;
                    }
                }

                result.Signs = signsDict;
                return result.Signs.Count > 0 ? result : null;
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Ошибка при парсинге гороскопа");
                return null;
            }
        }

        public Task<DailyHoroscope?> GetTodayHoroscopeCachedAsync(TimeSpan? maxAge = null, CancellationToken ct = default)
        {
            return GetTodayHoroscopeAsync(ct);
        }
    }
}