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
        private readonly UsersContext _Userscontext;

        public UsersService(UsersContext Userscontext)
        {
            _Userscontext = Userscontext;
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

        public async Task DeleteUser(int id)
        {
            var user = _Userscontext.Users.Find(id);
            if(user != null)
            {
                _Userscontext.Users.Remove(user);
                await _Userscontext.SaveChangesAsync();
            }
        }
    }
}
