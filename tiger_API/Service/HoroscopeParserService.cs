using System.Text.RegularExpressions;
using HtmlAgilityPack;
using tiger_API.Interfaces;
using tiger_API.Modell;

namespace tiger_API.Service
{
    public class HoroscopeParserService: IHoroscopeParser
    {
        private readonly HttpClient _httpClient;
        private readonly ILogger<HoroscopeParserService> _logger;
        private const string BaseUrl = "https://www.chita.ru/horoscope/";

        private static readonly Regex DatePattern = new(@"(Прогноз на .*?Обновлено \d{2}\.\d{2}\.\d{4} в \d{2}:\d{2})", RegexOptions.Compiled);

        public HoroscopeParserService(HttpClient httpClient, ILogger<HoroscopeParserService> logger)
        {
            _httpClient = httpClient;
            _logger = logger;

            // Настраиваем HttpClient один раз
            _httpClient.DefaultRequestHeaders.UserAgent.ParseAdd("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36");
        }

        public async Task<DailyHoroscope?> GetTodayHoroscopeAsync(CancellationToken ct = default)
        {
            try
            {
                string html = await _httpClient.GetStringAsync(BaseUrl, ct);

                var htmlDoc = new HtmlDocument();
                htmlDoc.LoadHtml(html);

                var result = new DailyHoroscope();

                // 1. Дата прогноза (ищем текст, содержащий "Прогноз на" и "Обновлено")
                var dateNode = htmlDoc.DocumentNode.SelectSingleNode("//*[contains(text(), 'Прогноз на') and contains(text(), 'Обновлено')]");
                if (dateNode != null)
                {
                    result.ForecastDate = dateNode.InnerText.Trim();

                    var match = DatePattern.Match(result.ForecastDate);
                    if (match.Success && DateTime.TryParse(match.Groups[1].Value.Split("Обновлено ")[1], out var updated))
                    {
                        result.UpdatedAt = updated;
                    }
                }

                // 2. Ищем все заголовки вида "## Гороскоп для ..."
                // Но т.к. сайт отдаёт markdown-подобный контент → часто это <h2> или просто текст
                var signNodes = htmlDoc.DocumentNode.SelectNodes("//h2 | //h3 | //*[contains(@class,'title') or contains(text(),'Гороскоп для')]");
                if (signNodes == null || signNodes.Count == 0)
                {
                    _logger.LogWarning("Не найдены заголовки знаков зодиака");
                    return null;
                }

                var signs = new Dictionary<string, ZodiacForecast>();

                foreach (var header in signNodes)
                {
                    string title = header.InnerText.Trim();
                    if (!title.Contains("Гороскоп для")) continue;

                    string signName = title.Replace("Гороскоп для", "").Trim();

                    // Следующий элемент — обычно диапазон дат
                    var next = header.NextSibling;
                    while (next != null && string.IsNullOrWhiteSpace(next.InnerText))
                        next = next.NextSibling;

                    string dateRange = next?.InnerText?.Trim() ?? "";

                    // Собираем текст гороскопа до следующего заголовка
                    var textParts = new List<string>();
                    var current = next?.NextSibling;

                    while (current != null)
                    {
                        if (current.Name == "h2" || current.Name == "h3" || current.InnerText.Contains("Гороскоп для"))
                            break;

                        string text = current.InnerText.Trim();
                        if (!string.IsNullOrWhiteSpace(text) && text != "Показать полностью")
                            textParts.Add(text);

                        current = current.NextSibling;
                    }

                    string forecastText = string.Join(" ", textParts).Trim();

                    if (!string.IsNullOrEmpty(signName) && !string.IsNullOrEmpty(forecastText))
                    {
                        signs[signName] = new ZodiacForecast
                        {
                            SignName = signName,
                            DateRange = dateRange,
                            Text = forecastText
                        };
                    }
                }

                result.Signs = signs;
                return result.Signs.Count > 0 ? result : null;
            }
            catch (HttpRequestException ex)
            {
                _logger.LogError(ex, "Ошибка HTTP при загрузке гороскопа");
                return null;
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Неожиданная ошибка парсинга гороскопа");
                return null;
            }
        }

        // Реализация кэша — можно добавить через IMemoryCache
        public Task<DailyHoroscope?> GetTodayHoroscopeCachedAsync(TimeSpan? maxAge = null, CancellationToken ct = default)
        {
            // Здесь можно добавить логику кэширования
            // Пока просто прокидываем в основной метод
            return GetTodayHoroscopeAsync(ct);
        }
    }
}
