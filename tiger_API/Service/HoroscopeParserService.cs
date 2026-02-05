using HtmlAgilityPack;
using System.Net;
using tiger_API.Interfaces;
using tiger_API.Modell;

namespace tiger_API.Service
{
    public class HoroscopeParserService : IHoroscopeParser
    {
        private readonly HttpClient _httpClient;
        private readonly ILogger<HoroscopeParserService> _logger;
        private const string BaseUrl = "https://horoscopes.rambler.ru/";

        private static readonly Dictionary<string, string> ZodiacMap = new()
        {
            { "Овен", "aries" }, { "Телец", "taurus" }, { "Близнецы", "gemini" },
            { "Рак", "cancer" }, { "Лев", "leo" }, { "Дева", "virgo" },
            { "Весы", "libra" }, { "Скорпион", "scorpio" }, { "Стрелец", "sagittarius" },
            { "Козерог", "capricorn" }, { "Водолей", "aquarius" }, { "Рыбы", "pisces" }
        };

        public HoroscopeParserService(HttpClient httpClient, ILogger<HoroscopeParserService> logger)
        {
            _httpClient = httpClient;
            _logger = logger;
        }

        public async Task<DailyHoroscope?> GetTodayHoroscopeAsync(CancellationToken ct = default)
        {
            var result = new DailyHoroscope
            {
                ForecastDate = $"Прогноз на {DateTime.Now:dd.MM.yyyy}",
                UpdatedAt = DateTime.Now,
                Signs = new Dictionary<string, ZodiacForecast>()
            };

            foreach (var entry in ZodiacMap)
            {
                try
                {
                    // Имитируем чистый запрос браузера каждый раз
                    using var request = new HttpRequestMessage(HttpMethod.Get, $"{BaseUrl}{entry.Value}/");
                    request.Headers.Add("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/121.0.0.0 Safari/537.36");

                    var response = await _httpClient.SendAsync(request, ct);
                    var html = await response.Content.ReadAsStringAsync(ct);

                    var doc = new HtmlDocument();
                    doc.LoadHtml(html);
                    var pNodes = doc.DocumentNode.SelectNodes("//article//p")
                                 ?? doc.DocumentNode.SelectNodes("//div[contains(@class, 'article')]//p")
                                 ?? doc.DocumentNode.SelectNodes("//p"); // Если совсем всё плохо, берем все <p>

                    if (pNodes != null)
                    {
                        // Фильтруем мусор (короткие фразы, ссылки, рекламу)
                        var paragraphs = pNodes
                            .Select(n => WebUtility.HtmlDecode(n.InnerText).Trim())
                            .Where(t => t.Length > 40 && !t.Contains("Рамблер") && !t.Contains("Подписаться"))
                            .ToList();

                        if (paragraphs.Any())
                        {
                            result.Signs[entry.Key] = new ZodiacForecast
                            {
                                SignName = entry.Key,
                                Text = string.Join("\n\n", paragraphs),
                                ImageUrl = $"https://img.icons8.com/color/96/{entry.Value}.png"
                            };
                            continue;
                        }
                    }
                    _logger.LogWarning("Не удалось извлечь текст для {Sign}", entry.Key);
                }
                catch (Exception ex)
                {
                    _logger.LogError(ex, "Ошибка при парсинге {Sign}", entry.Key);
                }
            }

            return result.Signs.Count > 0 ? result : null;
        }
    }
}