using Microsoft.AspNetCore.Mvc;
using tiger_API.Modell;
using tiger_API.Service;
using System.Threading.Tasks;
using tiger_API.Itreface;

namespace tiger_API.Controllers
{
    [ApiController]
    [Route("api/[controller]")]
    public class iSLikeController : ControllerBase
    {
        private readonly IIsLike _likeService;
        private readonly IUsers _userService;

        public iSLikeController(IIsLike likeService, IUsers userService)
        {
            _likeService = likeService;
            _userService = userService;
        }

        [HttpPost("send")]
        public async Task<IActionResult> SendLike([FromForm] SendLikeRequest request)
        {
            try
            {
                var result = await _likeService.SendLikeAsync(request.FromUserId, request.ToUserId, request.IsLike);
                return Ok(new { success = true, message = "Лайк успешно отправлен" });
            }
            catch (ArgumentException ex)
            {
                return BadRequest(new { success = false, message = ex.Message });
            }
            catch (InvalidOperationException ex)
            {
                return BadRequest(new { success = false, message = ex.Message });
            }
            catch (Exception ex)
            {
                return StatusCode(500, new { success = false, message = "Ошибка сервера" });
            }
        }

        [HttpGet("received/{userId}")]
        public async Task<IActionResult> GetUserLikes(int userId)
        {
            try
            {
                var likes = await _likeService.GetUserLikesAsync(userId);

                // Создаем расширенный ответ с информацией о пользователях
                var likesWithUserInfo = new List<object>();
                foreach (var like in likes)
                {
                    var fromUser = await _userService.GetUserById(like.FromUserid);
                    likesWithUserInfo.Add(new
                    {
                        like.Id,
                        like.FromUserid,
                        like.ToUserid,
                        like.IsLike,
                        like.CreatedAt,
                        FromUserName = fromUser != null ? $"{fromUser.FirstName} {fromUser.LastName}" : "Неизвестный пользователь",
                        FromUserSex = fromUser?.Sex
                    });
                }

                return Ok(new { success = true, likes = likesWithUserInfo });
            }
            catch (Exception ex)
            {
                return StatusCode(500, new { success = false, message = "Ошибка сервера" });
            }
        }

        [HttpGet("sent/{userId}")]
        public async Task<IActionResult> GetLikesSentByUser(int userId)
        {
            try
            {
                var likes = await _likeService.GetLikesSentByUserAsync(userId);

                var likesWithUserInfo = new List<object>();
                foreach (var like in likes)
                {
                    var toUser = await _userService.GetUserById(like.ToUserid);
                    likesWithUserInfo.Add(new
                    {
                        like.Id,
                        like.FromUserid,
                        like.ToUserid,
                        like.IsLike,
                        like.CreatedAt,
                        ToUserName = toUser != null ? $"{toUser.FirstName} {toUser.LastName}" : "Неизвестный пользователь",
                        ToUserSex = toUser?.Sex
                    });
                }

                return Ok(new { success = true, likes = likesWithUserInfo });
            }
            catch (Exception ex)
            {
                return StatusCode(500, new { success = false, message = "Ошибка сервера" });
            }
        }

        [HttpGet("mutual/{user1Id}/{user2Id}")]
        public async Task<IActionResult> CheckMutualLike(int user1Id, int user2Id)
        {
            try
            {
                var isMutual = await _likeService.CheckMutualLikeAsync(user1Id, user2Id);
                return Ok(new { success = true, isMutual = isMutual });
            }
            catch (Exception ex)
            {
                return StatusCode(500, new { success = false, message = "Ошибка сервера" });
            }
        }

        [HttpGet("user/{userId}/matches")]
        public async Task<IActionResult> GetUserMatches(int userId)
        {
            try
            {
                var sentLikes = await _likeService.GetLikesSentByUserAsync(userId);
                var matches = new List<object>();

                foreach (var sentLike in sentLikes.Where(l => l.IsLike))
                {
                    var isMutual = await _likeService.CheckMutualLikeAsync(userId, sentLike.ToUserid);
                    if (isMutual)
                    {
                        var user = await _userService.GetUserById(sentLike.ToUserid);
                        matches.Add(new
                        {
                            UserId = sentLike.ToUserid,
                            Name = user != null ? $"{user.FirstName} {user.LastName}" : "Неизвестный пользователь",
                            Bio = user?.BIO,
                            MutualLikeDate = sentLike.CreatedAt
                        });
                    }
                }

                return Ok(new { success = true, matches = matches });
            }
            catch (Exception ex)
            {
                return StatusCode(500, new { success = false, message = "Ошибка сервера" });
            }
        }
    }

    public class SendLikeRequest
    {
        public int FromUserId { get; set; }
        public int ToUserId { get; set; }
        public bool IsLike { get; set; }
    }
}