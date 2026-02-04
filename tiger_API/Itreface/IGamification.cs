public interface IGamification
{
    Task<int> CountDay(int userId);
    Task<int> CountOfDay(int userId);
    Task<int> NumberOfUsersLiked(int userId);
    Task<int> GetAccountAgeDays(int userId);

    Task<int> CountMutualLikes(int userId);

    Task UpdateLoginStreakAsync(int userId);
    Task IncrementLikesGivenAsync(int userId);
}