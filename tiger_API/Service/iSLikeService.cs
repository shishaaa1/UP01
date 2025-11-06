using Microsoft.EntityFrameworkCore;
using tiger_API.Context;
using tiger_API.Itreface;
using tiger_API.Modell;

namespace tiger_API.Service
{
    public class iSLikeService:IIsLike
    {
        private readonly iSLikeContext _context;

        public iSLikeService(iSLikeContext context)
        {
            _context = context;
        }

        public async Task<bool> SendLikeAsync(int fromUserId, int toUserId, bool isLike)
        {
            // Получаем информацию о пользователях
            var fromUser = await _context.Users.FindAsync(fromUserId);
            var toUser = await _context.Users.FindAsync(toUserId);

            if (fromUser == null || toUser == null)
            {
                throw new ArgumentException("Пользователь не найден");
            }

            // Проверяем правила: мужчина → женщина или женщина → мужчина
            if (fromUser.Sex == toUser.Sex)
            {
                throw new InvalidOperationException("Лайки могут отправляться только между пользователями разнего пола");
            }

            // Проверяем, существует ли уже лайк
            var existingLike = await _context.Islike
                .FirstOrDefaultAsync(l => l.FromUserid == fromUserId && l.ToUserid == toUserId);

            if (existingLike != null)
            {
                // Обновляем существующий лайк
                existingLike.IsLike = isLike;
                existingLike.CreatedAt = DateTime.Now;
            }
            else
            {
                // Создаем новый лайк
                var newLike = new Islike
                {
                    FromUserid = fromUserId,
                    ToUserid = toUserId,
                    IsLike = isLike,
                    CreatedAt = DateTime.Now
                };
                _context.Islike.Add(newLike);
            }

            await _context.SaveChangesAsync();
            return true;
        }

        public async Task<List<Islike>> GetUserLikesAsync(int userId)
        {
            // Получаем лайки, полученные пользователем
            return await _context.Islike
                .Where(l => l.ToUserid == userId && l.IsLike)
                .ToListAsync();
        }

        public async Task<List<Islike>> GetLikesSentByUserAsync(int userId)
        {
            // Получаем лайки, отправленные пользователем
            return await _context.Islike
                .Where(l => l.FromUserid == userId)
                .ToListAsync();
        }

        public async Task<bool> CheckMutualLikeAsync(int user1Id, int user2Id)
        {
            var like1 = await _context.Islike
                .AnyAsync(l => l.FromUserid == user1Id && l.ToUserid == user2Id && l.IsLike);

            var like2 = await _context.Islike
                .AnyAsync(l => l.FromUserid == user2Id && l.ToUserid == user1Id && l.IsLike);

            return like1 && like2;
        }

    }
}
