using tiger_API.Modell;

namespace tiger_API.Itreface
{
    public interface IUsers
    {
        Task ReginU(Users users);
        Task<int> LoginUsers(string login, string password);
        Task DeleteUser(int id);
    }
}
