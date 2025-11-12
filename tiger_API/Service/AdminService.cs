using Microsoft.EntityFrameworkCore;
using tiger_API.Context;
using tiger_API.Itreface;
using tiger_API.Modell;

namespace tiger_API.Service
{
    public class AdminService : IAdmin
    {
        private readonly AdminContext _Adnmincontext;

        public AdminService(AdminContext Adnmincontext)
        {
            _Adnmincontext = Adnmincontext;
        }

        public async Task ReginA(Admin admin)
        {
            _Adnmincontext.Admin.Add(admin);
            await _Adnmincontext.SaveChangesAsync();
        }

        public async Task<int> LoginAdmin(string login, string password)
        {
            Admin admin = _Adnmincontext.Admin.Where(x => x.Login == login && x.Password == password).First();
            return admin.Id;
        }
        public async Task<Admin> LoginAdminFull(string login, string password)
        {
            var admin = await _Adnmincontext.Admin
                .FirstOrDefaultAsync(x => x.Login == login && x.Password == password);

            return admin; // Вернёт null, если не найден
        }

        public async Task<List<Admin>> GetAllAdmins()
        {
            return await _Adnmincontext.Admin
                .Select(a => new Admin
                {
                    Id = a.Id,
                    Login = a.Login,
                    Nickname = a.Nickname,
                    // Пароль НЕ возвращаем!
                    Password = "" // или null, если допустимо
                })
                .ToListAsync();
        }
        public async Task<Admin?> GetAdminById(int id)
        {
            var admin = await _Adnmincontext.Admin
                .Where(a => a.Id == id)
                .Select(a => new Admin
                {
                    Id = a.Id,
                    Login = a.Login,
                    Nickname = a.Nickname,
                    Password = "" 
                })
                .FirstOrDefaultAsync();

            return admin;
        }
        public async Task<bool> UpdateAdmin(Admin admin)
        {
            var existing = await _Adnmincontext.Admin.FirstOrDefaultAsync(a => a.Login == admin.Login);
            if (existing == null) return false;

            // Обновляем Nickname
            existing.Nickname = admin.Nickname;

            // Обновляем пароль, только если он передан
            if (!string.IsNullOrWhiteSpace(admin.Password))
            {
                // ⚠️ Не забудьте захэшировать пароль в продакшене!
                existing.Password = admin.Password;
            }

            try
            {
                _Adnmincontext.Admin.Update(existing);
                await _Adnmincontext.SaveChangesAsync();
                return true;
            }
            catch
            {
                return false;
            }
        }
        public async Task<bool> DeleteAdmin(int id)
        {
            var admin = await _Adnmincontext.Admin.FindAsync(id);
            if (admin == null) return false;

            _Adnmincontext.Admin.Remove(admin);
            try
            {
                await _Adnmincontext.SaveChangesAsync();
                return true;
            }
            catch
            {
                return false;
            }
        }
    }
}
