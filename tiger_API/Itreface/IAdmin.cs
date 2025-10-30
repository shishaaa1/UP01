using tiger_API.Modell;

namespace tiger_API.Itreface
{
    public interface IAdmin
    {
        Task ReginA(Admin admin);
        Task<int> LoginAdmin(string login, string password);
    }
}
