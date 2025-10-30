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
    }
}
