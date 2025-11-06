using tiger_API.Modell;

namespace tiger_API.Itreface
{
    public interface IUsers
    {
        Task ReginU(Users users);
        Task<int> LoginUsers(string login, string password);
        Task DeleteUser(int id);
        Task<List<DailyStat>> GetRegistrationsCountToday();
        Task<Users> GetUserById(int id);
        Task<List<Users>> GetListUser();
        Task<List<Users>> GetAllUsersAsync();
        Task<bool> UpdateUserAsync(int userId, UpdateUserDto dto);
        Task<List<Users>> GetUsersOfOppositeSexAsync(int userId);
    }
}
