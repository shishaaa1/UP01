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
    [Route("GetCountStats")]
    [HttpGet]
    public async Task<IActionResult> GetCountStats(int userId)
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
            TotalLikesGiven = likes,
        });
    }
    [Route("GetAchiv")]
    [HttpGet]
    public async Task<IActionResult> GetAchiv(int userId)
    {
        var accountAge = await _gamification.GetAccountAgeDays(userId);
        var activeDays = await _gamification.CountDay(userId);
        var streak = await _gamification.CountOfDay(userId);
        var likes = await _gamification.NumberOfUsersLiked(userId);

        var completedTasks = new
        {
            FirstLike = likes >= 1,
            TenLikes = likes >= 10,
            OneHundredLikes = likes >= 100,
            FirstDayOnAccount = accountAge >= 1,
            TenDaysOnAccount = accountAge >= 10,
            OneHundredDaysOnAccount = accountAge >= 100
        };

        return Ok(new
        {
            CompletedTasks = completedTasks
        });
    }


}