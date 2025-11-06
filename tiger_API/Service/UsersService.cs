using Microsoft.AspNetCore.Http.HttpResults;
using Microsoft.AspNetCore.Mvc;
using tiger_API.Context;
using tiger_API.Itreface;
using tiger_API.Modell;
using System.Linq;
using Microsoft.EntityFrameworkCore;

namespace tiger_API.Service
{
    public class UsersService : IUsers
    {
        private readonly UsersContext _Userscontext;
        private readonly IPhotosUsers _photosUsers;

        public UsersService(UsersContext Userscontext, IPhotosUsers photosUsers)
        {
            _Userscontext = Userscontext;
            _photosUsers = photosUsers;
        }

        public async Task ReginU(Users users)
        {
            _Userscontext.Users.Add(users);
            await _Userscontext.SaveChangesAsync();
            
        }

        public async Task<int> LoginUsers(string login, string password)
        {
            Users User = _Userscontext.Users.Where(x => x.Login == login && x.Password == password).First();
            return User.Id;
        }

        public async Task<Users> GetUserById(int id)
        {
            return await _Userscontext.Users.FindAsync(id);
        }
        public async Task<List<Users>> GetListUser()
        {
            return await _Userscontext.Users.ToListAsync();
        }

        public async Task DeleteUser(int id)
        {
            var user = await _Userscontext.Users.FindAsync(id);
            if (user != null)
            {
                _Userscontext.Users.Remove(user);
                await _Userscontext.SaveChangesAsync(); 
            }
        }

        public async Task<List<Users>> GetAllUsersAsync()
        {
            return await _Userscontext.Users.ToListAsync();
        }
        public async Task<bool> UpdateUserAsync(int userId, UpdateUserDto dto)
        {
            var user = await _Userscontext.Users.FindAsync(userId);
            if (user == null)
                return false;

            if (!string.IsNullOrEmpty(dto.FirstName))
                user.FirstName = dto.FirstName;

            if (!string.IsNullOrEmpty(dto.LastName))
                user.LastName = dto.LastName;

            if (!string.IsNullOrEmpty(dto.BIO))
                user.BIO = dto.BIO;

            _Userscontext.Users.Update(user);
            await _Userscontext.SaveChangesAsync();
            return true;
        }
        public async Task<List<DailyStat>> GetRegistrationsCountToday()
        {
            var today = DateTime.UtcNow.Date;
            var startDate = today.AddDays(-29);

            var stats = new List<DailyStat>();

            // Получаем всех пользователей за последние 30 дней за один запрос
            var usersInPeriod = await _Userscontext.Users
                .Where(u => u.CreatedAt >= startDate && u.CreatedAt < today.AddDays(1))
                .ToListAsync();

            for (int i = 0; i < 30; i++)
            {
                var date = startDate.AddDays(i);
                var count = usersInPeriod.Count(u => u.CreatedAt.Date == date);

                stats.Add(new DailyStat
                {
                    Date = date,
                    NewUsers = count
                });
            }

            return stats;
        }

        public async Task<List<Users>> GetUsersOfOppositeSexAsync(int userId)
        {
            var currentUser = await _Userscontext.Users.FindAsync(userId);
            if (currentUser == null)
                return new List<Users>();
            return await _Userscontext.Users
                .Where(u => u.Id != userId && u.Sex != currentUser.Sex)
                .ToListAsync();
        }

    }
}
