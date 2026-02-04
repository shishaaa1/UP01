using Microsoft.AspNetCore.Mvc;

[Route("api/[controller]")]
[ApiController]
public class GamificationController : ControllerBase
{
    private readonly IGamification _gamification;

    public GamificationController(IGamification gamification)
    {
        _gamification = gamification;
    }

    [HttpGet("{userId}")]
    public async Task<IActionResult> GetStats(int userId)
    {
        // 1. Сколько всего дней аккаунту
        var accountAge = await _gamification.GetAccountAgeDays(userId);

        // 2. В скольких из этих дней пользователь реально заходил
        var activeDays = await _gamification.CountDay(userId);

        // 3. Текущая серия (стрик)
        var streak = await _gamification.CountOfDay(userId);

        // 4. Всего поставлено лайков (включая старые)
        var likes = await _gamification.NumberOfUsersLiked(userId);

        return Ok(new
        {
            UserId = userId,
            DaysSinceRegistration = accountAge,
            TotalActiveDays = activeDays,
            CurrentStreak = streak,
            TotalLikesGiven = likes
        });
    }
}