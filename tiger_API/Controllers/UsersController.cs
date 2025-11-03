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
            _photosUsers.DeletePhotosByUserIdAsync(id); 
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

        [Route("GetUserById")]
        [HttpGet]
        public async Task<Users> GetUsersToDay(int id)
        {
            var res= await _tigger.GetUserById(id);
            return res;
        }

        [Route("GetUsers")]
        [HttpGet]
        public async Task<ActionResult<List<Users>>> GetUsers()
        {
            var users = await _tigger.GetListUser();
            return Ok(users);
        }

        /// <summary>
        /// Получение пользователя по ID вместе с фото 
        /// </summary>
        /// <param name="id">ID пользователя</param>
        /// <returns>Объект с данными пользователя и фото в виде байтов</returns>
        [Route("GetUsersAndPhoto")]
        [HttpGet]
        public async Task<ActionResult> GetUserWithPhoto(int id)
        {
            var user = await _tigger.GetUserById(id);
            if (user == null)
                return NotFound(new { Message = "Пользователь не найден" });

            // Получаем фото
            byte[] photoBytes = null;
            try
            {
                photoBytes = await _photosUsers.GetPhotoByUserIdAsync(id);
            }
            catch (FileNotFoundException)
            {
                
            }
            var result = new
            {
                user.Id,
                user.FirstName,
                user.LastName,
                user.Birthday,
                user.BIO,
                user.CreatedAt,
                user.Sex,
                user.Login,
                PhotoBytes = photoBytes 
            };

            return Ok(result);
        }

        /// <summary>
        /// Получение всех пользователей вместе с их фото
        /// </summary>
        /// <returns>Список объектов с данными пользователей и фото в виде байтов</returns>
        [Route("GetAllUsersWithPhoto")]
        [HttpGet] 
        public async Task<ActionResult<List<object>>> GetAllUsersAndPhoto()
        {
            var users = await _tigger.GetAllUsersAsync(); 
            if (users == null || !users.Any())
            {
                return Ok(new List<object>()); 
            }

            var result = new List<object>();

            foreach (var user in users)
            {
                byte[]? photoBytes = null;
                try
                {
                    photoBytes = await _photosUsers.GetPhotoByUserIdAsync(user.Id);
                }
                catch (FileNotFoundException)
                {
                    
                }

                result.Add(new
                {
                    user.Id,
                    user.FirstName,
                    user.LastName,
                    user.Birthday,
                    user.BIO,
                    user.CreatedAt,
                    user.Sex,
                    user.Login,
                    PhotoBytes = photoBytes
                });
            }

            return Ok(result);
        }


    }
}