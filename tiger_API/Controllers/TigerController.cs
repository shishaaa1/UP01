using Microsoft.AspNetCore.Mvc;
using tiger_API.Itreface;
using tiger_API.Modell;
using tiger_API.Service;

namespace tiger_API.Controllers
{
    [Route("api/TigerController")]
    public class TigerController : Controller
    {
        private readonly IUsers _tigger;

        public TigerController(IUsers tigger)
        {
            _tigger = tigger;
        }
        /// <summary>
        /// Регистрация пользовтаеля
        /// </summary>
        /// <remarks>Данный метод для проверки</remarks>
        /// <returns></returns>
        [Route("AddUsers")]
        [HttpPost]
        public Task AddUsers([FromForm] Users users)
        {
            var res= _tigger.ReginU(users);
            return res;
        }
        /// <summary>
        /// Авторизация пользователя
        /// </summary>
        /// <remarks>бла бла бла</remarks>
        /// <returns></returns>
        [Route("LoginUsers")]
        [HttpPost]
        public Task<int> Login([FromForm] string login, [FromForm] string password)
        {
            var res = _tigger.LoginUsers(login, password);
            return res;
        }

    }
}