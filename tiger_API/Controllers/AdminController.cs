using Microsoft.AspNetCore.Mvc;
using tiger_API.Itreface;
using tiger_API.Modell;

namespace tiger_API.Controllers
{
    public class AdminController : Controller
    {
        private readonly IAdmin _admin;

        public AdminController(IAdmin admin)
        {
            _admin = admin;
        }

        /// <summary>
        /// Регистрация Администратора
        /// </summary>
        /// <remarks>Данный метод для добавления администора</remarks>
        /// <returns></returns>
        [Route("AddAdmin")]
        [HttpPost]
        public Task AddAdmin([FromForm] Admin admin)
        {
            var res = _admin.ReginA(admin);
            return res;
        }
        /// <summary>
        /// Авторизация Администратора
        /// </summary>
        /// <remarks>Данный метод авторизирует администратора в системе</remarks>
        /// <returns></returns>
        [Route("LoginAdmin")]
        [HttpPost]
        public Task<int> Login([FromForm] string login, [FromForm] string password)
        {
            var res = _admin.LoginAdmin(login, password);
            return res;
        }
    }
}
