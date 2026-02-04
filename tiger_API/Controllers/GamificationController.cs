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
        var accountAge = await _gamification.GetAccountAgeDays(userId);
        var activeDays = await _gamification.CountDay(userId);
        var streak = await _gamification.CountOfDay(userId);
        var likes = await _gamification.NumberOfUsersLiked(userId);

        // Добавляем вызов
        var matches = await _gamification.CountMutualLikes(userId);

        return Ok(new
        {
            UserId = userId,
            DaysSinceRegistration = accountAge,
            TotalActiveDays = activeDays,
            CurrentStreak = streak,
            TotalLikesGiven = likes,
            TotalMatches = matches // Отдаем число взаимных лайков
        });
    }
    [Route("GetAchiv")]
    [HttpGet]
    public async Task<IActionResult> GetAchiv(int userId)
    {
        // 1. Получаем все данные (можно параллельно для скорости, но можно и последовательно)
        var accountAge = await _gamification.GetAccountAgeDays(userId);
        var likesGiven = await _gamification.NumberOfUsersLiked(userId);

        // Новое: получаем количество взаимных лайков
        var mutualLikes = await _gamification.CountMutualLikes(userId);

        // Логика ачивок
        var completedTasks = new
        {
            // Лайки (вы ставите)
            FirstLike = likesGiven >= 1,
            TenLikes = likesGiven >= 10,
            OneHundredLikes = likesGiven >= 100,

            // Возраст аккаунта
            FirstDayOnAccount = accountAge >= 1,
            TenDaysOnAccount = accountAge >= 10,
            OneHundredDaysOnAccount = accountAge >= 100,

            // ВЗАИМНЫЕ ЛАЙКИ (МЭТЧИ) - Новые ачивки
            FirstMatch = mutualLikes >= 1,       // Случился первый мэтч
            FiveMatches = mutualLikes >= 5,      // 5 взаимных симпатий
            DatingGuru = mutualLikes >= 50       // Гуру дейтинга
        };

        return Ok(new
        {
            CompletedTasks = completedTasks
        });
    }



}