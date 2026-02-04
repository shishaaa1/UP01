using Microsoft.AspNetCore.Mvc;
using tiger_API.Interfaces;
using tiger_API.Modell;
using tiger_API.Service;

namespace tiger_API.Controllers;

[ApiController]
[Route("api/[controller]")]
public class HoroscopeController : ControllerBase
{
    private readonly IHoroscopeParser _horoscopeService;

    public HoroscopeController(IHoroscopeParser horoscopeService)
    {
        _horoscopeService = horoscopeService;
    }

    [HttpGet("today")]
    [ProducesResponseType(typeof(DailyHoroscope), 200)]
    [ProducesResponseType(204)]
    [ProducesResponseType(500)]
    public async Task<IActionResult> GetToday()
    {
        var result = await _horoscopeService.GetTodayHoroscopeAsync(HttpContext.RequestAborted);

        if (result == null || !result.Signs.Any())
            return NoContent();

        return Ok(result);
    }
}