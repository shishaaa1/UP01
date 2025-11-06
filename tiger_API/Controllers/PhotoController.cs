using Microsoft.AspNetCore.Mvc;
using tiger_API.Itreface;
using tiger_API.Modell;

namespace tiger_API.Controllers
{
    [Route("api/PhotoController")]
    [ApiController]
    public class PhotoController : Controller
    {
        private readonly IPhotosUsers _photosUsers;

        public PhotoController(IPhotosUsers photosUsers)
        {
            _photosUsers = photosUsers;
        }

        /// <summary>
        /// Загрузка фото пользователя
        /// </summary>
        /// <returns>Идентификатор загруженного фото</returns>
        [Route("UploadPhoto")]
        [HttpPost]
        public async Task<IActionResult> UploadPhoto([FromForm] UploadPhotoRequest request)
        {
            using var ms = new MemoryStream();
            await request.PhotoFile.CopyToAsync(ms);
            var photoBytes = ms.ToArray();

            var id = await _photosUsers.UploadPhotoAsync(request.UserId, photoBytes);
            return Ok(new { PhotoId = id });
        }
        [Route("GetPhotoByUsersId")]
        [HttpGet]
        public async Task<IActionResult> GetPhotoByUser(int userId)
        {
            try
            {
                var photoBytes = await _photosUsers.GetPhotoByUserIdAsync(userId);
                return File(photoBytes, "image/jpeg"); // или другой тип
            }
            catch (FileNotFoundException)
            {
                return NotFound("Фото не найдено");
            }
        }

        /// <summary>
        /// Удаление фото по его ID
        /// </summary>
        /// <param name="id">ID фотографии (не пользователя!)</param>
        /// <returns>Результат операции</returns>
        [Route("DeletePhoto")]
        [HttpDelete]
        public async Task<IActionResult> DeletePhoto([FromQuery] int id)
        {
            if (id <= 0)
                return BadRequest("Некорректный ID фото");

            var isDeleted = await _photosUsers.DeletePhotoAsync(id);

            if (!isDeleted)
                return NotFound(new { Message = "Фото с таким ID не найдено" });

            return Ok(new {PhotoId = id });
        }

        [Route("GetUserPhotoId")]
        [HttpGet]
        public async Task<IActionResult> GetPhotoIdByUserId(int userId)
        {
            try
            {
                var photoId = await _photosUsers.GetUserPhotoIdAsync(userId);
                return Ok(photoId);
            }
            catch(Exception ex)
            {
                return NotFound(new {Message = "Фото не найдено"});
            }
        }

        /// <summary>
        /// Получить все сохранённые фотографии пользователей
        /// </summary>
        /// <returns>Список всех фото с привязкой к UserId</returns>
        [Route("GetAllPhotos")]
        [HttpGet]
        public async Task<ActionResult<List<PhotosUsers>>> GetAllPhotos()
        {
            var photos = await _photosUsers.GetAllPhotosWithUserDataAsync();

            if (photos == null || !photos.Any())
                return NotFound(new { Message = "Фотографии не найдены" });

            return Ok(photos);
        }
    }
}
