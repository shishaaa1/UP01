using Microsoft.AspNetCore.Mvc;
using tiger_API.Itreface;

namespace tiger_API.Controllers
{
    [Route("api/[controller]")]
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
        /// <param name="userId">ID пользователя</param>
        /// <param name="photoFile">Файл изображения</param>
        /// <returns>Идентификатор загруженного фото</returns>
        [HttpPost("UploadPhoto")]
        [Consumes("multipart/form-data")]
        public async Task<IActionResult> UploadPhoto(
            [FromForm] int userId,
            [FromForm] IFormFile photoFile)
        {
            // Валидация входных данных
            if (userId <= 0)
                return BadRequest("Неверный ID пользователя.");

            if (photoFile == null || photoFile.Length == 0)
                return BadRequest("Файл не выбран или пуст.");
            var allowedTypes = new[] { "image/jpeg", "image/png", "image/jpg", "image/gif" };
            if (!allowedTypes.Contains(photoFile.ContentType))
                return BadRequest($"Недопустимый формат файла. Разрешены: {string.Join(", ", allowedTypes)}");

            try
            {
                using var ms = new MemoryStream();
                await photoFile.CopyToAsync(ms);
                var photoBytes = ms.ToArray();

                var id = await _photosUsers.UploadPhotoAsync(userId, photoBytes);

                return Ok(new { PhotoId = id });
            }
            catch (Exception ex)
            {
                Console.WriteLine($"Ошибка загрузки: {ex.Message}");
                return Ok();
            }
        }
    }
}
