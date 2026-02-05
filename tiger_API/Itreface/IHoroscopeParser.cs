
using tiger_API.Modell;

namespace tiger_API.Interfaces;

public interface IHoroscopeParser
{
    Task<DailyHoroscope?> GetTodayHoroscopeAsync(CancellationToken ct = default);
}