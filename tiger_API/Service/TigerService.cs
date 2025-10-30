using tiger_API.Itreface;
using tiger_API.Modell;

namespace tiger_API.Service
{
    public class TigerService : ITigger
    {
        /// <summary>
        /// Registers a user.
        /// </summary>
        /// <param name="users">The user to register.</param>
        /// <returns>A task representing the asynchronous operation.</returns>
        public Task ReginU(Users users)
        {

            return Task.CompletedTask;
        }
    }
}
