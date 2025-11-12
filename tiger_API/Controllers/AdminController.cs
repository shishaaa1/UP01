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
        /// Авторизация Администратора и получение его данных (Login и Nickname)
        /// </summary>
        /// <remarks>Возвращает Id, Login и Nickname при успешной авторизации</remarks>
        /// <returns>JSON с AdminId, Login, Nickname или ошибку</returns>
        [HttpPost("GetLoginAndNick")]
        public async Task<IActionResult> GetLoginAndNick([FromForm] string login, [FromForm] string password)
        {
            if (string.IsNullOrWhiteSpace(login) || string.IsNullOrWhiteSpace(password))
                return BadRequest("Логин и пароль обязательны");

            var result = await _admin.LoginAdminFull(login, password);

            if (result == null)
                return Unauthorized(new { message = "Неверный логин или пароль" });

            return Ok(new
            {
                AdminId = result.Id,
                Login = result.Login,
                Nickname = result.Nickname
            });
        }

        /// <summary>
        /// Получить всех администраторов (без паролей)
        /// </summary>
        [Route("GetAllAdmins")]
        [HttpGet]
        public async Task<ActionResult<List<Admin>>> GetAllAdmins()
        {
            var admins = await _admin.GetAllAdmins();
            return Ok(admins);
        }

        /// <summary>
        /// Получить администратора по ID (без пароля)
        /// </summary>
        [Route("GetAdminById")]
        [HttpGet]
        public async Task<ActionResult<Admin>> GetAdminById(int id)
        {
            var admin = await _admin.GetAdminById(id);
            if (admin == null) return NotFound(new { message = $"Админ с ID {id} не найден." });
            return Ok(admin);
        }

        /// <summary>
        /// Обновить данные администратора
        /// </summary>
        /// <remarks>
        /// Передавайте Login, Nickname. Пароль — только если нужно сменить (иначе оставьте пустым/не передавайте).
        /// </remarks>
        [Route("UpdateAdmin")]
        [HttpPut]
        public async Task<IActionResult> UpdateAdmins([FromForm] Admin admin)
        {
            if (string.IsNullOrWhiteSpace(admin.Login))
                return BadRequest("Логин обязателен для обновления администратора.");

            var success = await _admin.UpdateAdmin(admin);
            if (!success)
                return NotFound(new { message = $"Админ с логином {admin.Login} не найден или ошибка обновления." });

            return Ok(new { message = "Администратор успешно обновлён." });
        }

        /// <summary>
        /// Удалить администратора по ID
        /// </summary>
        [Route("DeleteAdmin")]
        [HttpDelete]
        public async Task<IActionResult> DeleteAdminByID(int id)
        {
            if (id <= 0)
                return BadRequest("Некорректный ID.");

            var success = await _admin.DeleteAdmin(id);
            if (!success)
                return NotFound(new { message = $"Админ с ID {id} не найден." });

            return Ok(new { message = $"Администратор с ID {id} удалён." });
        }
    }
}
