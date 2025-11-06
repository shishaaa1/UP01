using Microsoft.AspNetCore.Mvc;
using tiger_API.Itreface;
using tiger_API.Modell;

namespace tiger_API.Controllers
{
    [Route("api/AdminController")]
    [ApiController]
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
        [HttpPost("LoginAdmin")]
        public async Task<IActionResult> LoginAdmin([FromForm] string login, [FromForm] string password)
        {
            if (string.IsNullOrWhiteSpace(login) || string.IsNullOrWhiteSpace(password))
                return BadRequest("Логин и пароль обязательны");

            var result = await _admin.LoginAdmin(login, password);

            if (result <= 0)
                return Unauthorized();

            return Ok(new { AdminId = result });
        }

        /// <summary>
        /// Метод что бы узнать ник админа
        /// </summary>
        /// <remarks></remarks>
        /// <returns></returns>
        [HttpPost("LoginAdminName")]
        public async Task<IActionResult> LoginAdminName([FromForm] string login, [FromForm] string password)
        {
            if (string.IsNullOrWhiteSpace(login) || string.IsNullOrWhiteSpace(password))
                return BadRequest("Логин и пароль обязательны");

            var result = await _admin.LoginAdminName(login, password);

            return Ok(result);
        }
        /// <summary>
        /// Метод что бы узнать Login админа
        /// </summary>
        /// <remarks></remarks>
        /// <returns></returns>
        [HttpPost("LoginAdminLogin")]
        public async Task<IActionResult> LoginAdminLogin([FromForm] string login, [FromForm] string password)
        {
            if (string.IsNullOrWhiteSpace(login) || string.IsNullOrWhiteSpace(password))
                return BadRequest("Логин и пароль обязательны");

            var result = await _admin.LoginAdminLogin(login, password);

            return Ok(result);
        }
    }
}
