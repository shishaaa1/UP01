using Microsoft.EntityFrameworkCore;
using tiger_API.Context;
using tiger_API.Modell;

public class GamificationService : IGamification
{
    private readonly UsersContext _context;
    private readonly iSLikeContext _likeContext;

    public GamificationService(UsersContext context, iSLikeContext likeContext)
    {
        _context = context;
        _likeContext = likeContext;
    }

    // Метод: Сколько дней пользователь с нами (по дате регистрации)
    public async Task<int> GetAccountAgeDays(int userId)
    {
        var user = await _context.Users
            .AsNoTracking()
            .FirstOrDefaultAsync(u => u.Id == userId);

        if (user == null) return 0;

        var age = (DateTime.UtcNow.Date - user.CreatedAt.Date).Days;
        return age + 1; // +1 чтобы в день регистрации уже было "1 день"
    }

    public async Task<int> CountDay(int userId)
    {
        var stats = await GetOrCreateStatsAsync(userId);
        return stats.TotalLoginDays;
    }

    public async Task<int> CountOfDay(int userId)
    {
        var stats = await GetOrCreateStatsAsync(userId);
        var today = DateTime.UtcNow.Date;

        if (!stats.LastLoginDate.HasValue) return 0;

        if (stats.LastLoginDate.Value.Date == today)
            return stats.ConsecutiveLoginDays;

        if (stats.LastLoginDate.Value.Date == today.AddDays(-1))
            return stats.ConsecutiveLoginDays + 1;

        return 1;
    }

    public async Task<int> NumberOfUsersLiked(int userId)
    {
        var stats = await GetOrCreateStatsAsync(userId);
        return stats.LikesGivenCount;
    }

    private async Task<Gamification> GetOrCreateStatsAsync(int userId)
    {
        var stats = await _context.Gamifications
            .FirstOrDefaultAsync(g => g.UserId == userId);

        // Ключевой момент: считаем старые лайки напрямую из контекста лайков
        int actualLikesCount = await _likeContext.Islike
            .AsNoTracking()
            .CountAsync(l => l.FromUserid == userId && l.IsLike == true);

        if (stats == null)
        {
            stats = new Gamification
            {
                UserId = userId,
                TotalLoginDays = 1, // Раз он делает запрос, значит зашел сегодня
                ConsecutiveLoginDays = 1,
                LikesGivenCount = actualLikesCount, // ПОДТЯГИВАЕМ СТАРЫЕ ЛАЙКИ
                LastLoginDate = DateTime.UtcNow.Date
            };
            _context.Gamifications.Add(stats);
        }
        else
        {
            // Если запись уже есть, но лайки в ней не совпадают с историей — обновляем
            if (stats.LikesGivenCount != actualLikesCount)
            {
                stats.LikesGivenCount = actualLikesCount;
            }
        }

        await _context.SaveChangesAsync();
        return stats;
    }

    public async Task UpdateLoginStreakAsync(int userId)
    {
        var stats = await GetOrCreateStatsAsync(userId);
        var today = DateTime.UtcNow.Date;

        if (!stats.LastLoginDate.HasValue || stats.LastLoginDate.Value.Date < today)
        {
            stats.TotalLoginDays += 1;

            if (stats.LastLoginDate.HasValue && stats.LastLoginDate.Value.Date == today.AddDays(-1))
                stats.ConsecutiveLoginDays += 1;
            else
                stats.ConsecutiveLoginDays = 1;

            stats.LastLoginDate = today;
            await _context.SaveChangesAsync();
        }
    }

    public async Task IncrementLikesGivenAsync(int userId)
    {
        var stats = await GetOrCreateStatsAsync(userId);
        stats.LikesGivenCount += 1;
        await _context.SaveChangesAsync();
    }
    public async Task<int> CountMutualLikes(int userId)
    {


        var query = from myLike in _likeContext.Islike
                    join theirLike in _likeContext.Islike
                    on myLike.ToUserid equals theirLike.FromUserid 
                    where myLike.FromUserid == userId
                          && myLike.IsLike == true
                          && theirLike.ToUserid == userId 
                          && theirLike.IsLike == true
                    select 1;

        return await query.CountAsync();
    }
}