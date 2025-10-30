using Microsoft.AspNetCore.Http.HttpResults;
using Microsoft.AspNetCore.Mvc;
using tiger_API.Context;
using tiger_API.Itreface;
using tiger_API.Modell;
using System.Linq;

namespace tiger_API.Service
{
    public class UsersService : IUsers
    {
        private readonly UsersContext _context;

        public UsersService(UsersContext context)
        {
            _context = context;
        }

        /// <summary>
        /// Registers a user.
        /// </summary>
        /// <param name="users">The user to register.</param>
        /// <returns>A task representing the asynchronous operation.</returns>
        public async Task ReginU(Users users)
        {
            _context.Users.Add(users);
            await _context.SaveChangesAsync();
        }

        public async Task<int> LoginUsers(string login, string password)
        {
            Users User = new UsersContext().Users.Where(x => x.Login == login && x.Password == password).First();
            return User.Id;
        }
    }
}
