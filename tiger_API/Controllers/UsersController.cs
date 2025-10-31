using System;
using Microsoft.AspNetCore.Mvc;
using tiger_API.Itreface;
using tiger_API.Modell;
using tiger_API.Service;

namespace tiger_API.Controllers
{
    [Route("api/UserController")]
    [ApiController]
    public class UsersController : Controller
    {
        private readonly IUsers _tigger;
        private readonly IPhotosUsers _photosUsers;

        public UsersController(IUsers tigger, IPhotosUsers photosUsers)
        {
            _tigger = tigger;
            _photosUsers = photosUsers; 
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

        /// <summary>
        /// Удаление пользователя
        /// </summary>
        /// <remarks>бла бла бла</remarks>
        /// <returns></returns>
        [Route("DeleteUser")]
        [HttpDelete]
        public ActionResult DeleteUser([FromForm] int id)
        {
            _tigger.DeleteUser(id);
            return Ok();
        }

        /// <summary>
        /// Считываение новых пользователей за сегодня
        /// </summary>
        /// <remarks>Ура</remarks>
        /// <returns></returns>
        [Route("CountUsersToday")]
        [HttpGet]
        public async Task<ActionResult<List<DailyStat>>> GetUsersToDay()
        {
            var res = await _tigger.GetRegistrationsCountToday();
            return Ok(res);
        }
    }
}