using tiger_API.Modell;

namespace tiger_API.Itreface
{
    public interface IIsLike
    {
        Task<bool> SendLikeAsync(int fromUserId, int toUserId, bool isLike);
        Task<List<Islike>> GetUserLikesAsync(int userId);
        Task<bool> CheckMutualLikeAsync(int user1Id, int user2Id);
        Task<List<Islike>> GetLikesSentByUserAsync(int userId);
        Task<bool> RevokeLikeAsync(int fromUserId, int toUserId);
    }
}
