
using tiger_API.Modell;

namespace tiger_API.Interfaces;

public interface IHoroscopeParser
{
    /// <summary>
    /// Загружает и парсит гороскоп на сегодня с https://www.chita.ru/horoscope/
    /// </summary>
    /// <param name="cancellationToken">токен отмены</param>
    /// <returns>Модель со всеми знаками или null при ошибке</returns>
    Task<DailyHoroscope?> GetTodayHoroscopeAsync(CancellationToken cancellationToken = default);

    /// <summary>
    /// Версия с кэшированием (если используете IMemoryCache или другой кэш)
    /// </summary>
    Task<DailyHoroscope?> GetTodayHoroscopeCachedAsync(TimeSpan? maxAge = null, CancellationToken ct = default);
}