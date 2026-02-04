using Microsoft.EntityFrameworkCore;
using tiger_API.Context;
using tiger_API.Itreface;
using tiger_API.Modell;

namespace tiger_API.Service
{
    public class iSLikeService:IIsLike
    {
        private readonly iSLikeContext _context;
        private readonly IAuditService _audit;
        private readonly IGamification _gamification;     // ← добавляем зависимость

        public iSLikeService(
            iSLikeContext context,
            IAuditService audit,
            IGamification gamification)                   // ← добавляем в конструктор
        {
            _context = context;
            _audit = audit;
            _gamification = gamification;
        }

        public async Task<bool> SendLikeAsync(int fromUserId, int toUserId, bool isLike)
        {
            var fromUser = await _context.Users.FindAsync(fromUserId);
            var toUser = await _context.Users.FindAsync(toUserId);

            if (fromUser == null || toUser == null)
                throw new ArgumentException("Пользователь не найден");

            if (fromUser.Sex == toUser.Sex)
                throw new InvalidOperationException("Лайки могут отправляться только между пользователями разного пола");

            var existingLike = await _context.Islike
                .FirstOrDefaultAsync(l => l.FromUserid == fromUserId && l.ToUserid == toUserId);

            bool isNewLike = existingLike == null;

            if (existingLike != null)
            {
                // было ли раньше лайком, а теперь нет
                bool wasLikeBefore = existingLike.IsLike;
                existingLike.IsLike = isLike;
                existingLike.CreatedAt = DateTime.UtcNow;

                // Если раньше не было лайка, а теперь есть → увеличиваем
                if (!wasLikeBefore && isLike)
                {
                    await _gamification.IncrementLikesGivenAsync(fromUserId);
                }
                // Если раньше был лайк, а теперь дизлайк → можно уменьшить (опционально)
                // else if (wasLikeBefore && !isLike) { ... decrement ... }
            }
            else
            {
                var newLike = new Islike
                {
                    FromUserid = fromUserId,
                    ToUserid = toUserId,
                    IsLike = isLike,
                    CreatedAt = DateTime.UtcNow
                };
                _context.Islike.Add(newLike);

                if (isLike)
                {
                    await _gamification.IncrementLikesGivenAsync(fromUserId);
                }
            }

            await _context.SaveChangesAsync();

            string action = isLike ? "LIKE" : "DISLIKE";
            await _audit.LogAsync(fromUserId, action, "User", $"Target:{toUserId}");

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
        public async Task<bool> RevokeLikeAsync(int fromUserId, int toUserId)
        {
            var fromUser = await _context.Users.FindAsync(fromUserId);
            var toUser = await _context.Users.FindAsync(toUserId);

            if (fromUser == null || toUser == null)
            {
                throw new ArgumentException("Пользователь не найден");
            }

            if (fromUser.Sex == toUser.Sex)
            {
                throw new InvalidOperationException("Лайки могут отправляться только между пользователями разного пола");
            }

            var existingLike = await _context.Islike
                .FirstOrDefaultAsync(l => l.FromUserid == fromUserId && l.ToUserid == toUserId);

            if (existingLike != null)
            {
                if (existingLike.IsLike) // Если лайк был true
                {
                    existingLike.IsLike = false; // Меняем на false
                    existingLike.CreatedAt = DateTime.Now;
                    await _context.SaveChangesAsync();
                }
                return true;
            }

            // Если лайка не было, просто возвращаем true (ничего не делаем)
            return true;
        }
    }
}
