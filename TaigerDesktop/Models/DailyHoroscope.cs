using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace TaigerDesktop.Models
{
    public class DailyHoroscope
    {
        public string ForecastDate { get; set; } = string.Empty;
        public DateTime? UpdatedAt { get; set; }
        public Dictionary<string, ZodiacForecast> Signs { get; set; } = new();
    }

    public class ZodiacForecast
    {
        public string SignName { get; set; } = string.Empty;
        public string DateRange { get; set; } = string.Empty;
        public string Text { get; set; } = string.Empty;
    }
}
