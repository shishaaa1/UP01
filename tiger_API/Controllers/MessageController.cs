using Microsoft.AspNetCore.Mvc;
using tiger_API.Itreface;
using tiger_API.Modell;

namespace tiger_API.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class MessageController : ControllerBase
    {
        private readonly IMessageService _messageService;

        public MessageController(IMessageService messageService)
        {
            _messageService = messageService;
        }

        /// <summary>
        /// Отправить сообщение от одного пользователя другому
        /// </summary>
        /// <param name="senderId">ID отправителя</param>
        /// <param name="recipientId">ID получателя</param>
        /// <param name="text">Текст сообщения</param>
        /// <returns></returns>
        [HttpPost("WriteMessage")]
        public async Task<IActionResult> WriteMessage(
            [FromQuery] int senderId,
            [FromQuery] int recipientId,
            [FromBody] string text)
        {
            try
            {
                await _messageService.SendMessageAsync(senderId, recipientId, text);
                return Ok("Сообщение успешно отправлено.");
            }
            catch (ArgumentException ex)
            {
                return BadRequest(ex.Message);
            }
            catch (InvalidOperationException ex)
            {
                return BadRequest(ex.Message);
            }
            catch (Exception ex)
            {
                // Логирование ошибки (в продакшене используйте ILogger)
                return StatusCode(500, "Произошла внутренняя ошибка сервера.");
            }
        }

        /// <summary>
        /// Получить переписку между двумя пользователями
        /// </summary>
        /// <param name="u1">ID первого пользователя</param>
        /// <param name="u2">ID второго пользователя</param>
        /// <returns>Список сообщений</returns>
        [HttpGet("Conversation")]
        public async Task<IActionResult> GetConversation(
            [FromQuery] int u1,
            [FromQuery] int u2)
        {
            try
            {
                var messages = await _messageService.GetConversationAsync(u1, u2);
                return Ok(messages);
            }
            catch (Exception ex)
            {
                return StatusCode(500, "Не удалось загрузить переписку.");
            }
        }
    }
}