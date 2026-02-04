public interface IGamification
{
    Task<int> CountDay(int userId);        // Активные дни (из Gamification)
    Task<int> CountOfDay(int userId);      // Текущий стрик
    Task<int> NumberOfUsersLiked(int userId);
    Task<int> GetAccountAgeDays(int userId); // Дни с момента регистрации (из Users)

    Task UpdateLoginStreakAsync(int userId);
    Task IncrementLikesGivenAsync(int userId);
}