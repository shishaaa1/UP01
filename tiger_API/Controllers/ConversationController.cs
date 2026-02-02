using Microsoft.AspNetCore.Mvc;
using tiger_API.Itreface;

namespace tiger_API.Controllers
{
    [ApiController]
    [Route("api/[controller]")]
    public class ConversationController : ControllerBase
    {
        private readonly IConversationAIService _aiService;
        private readonly IAuditService _audit;

        public ConversationController(IConversationAIService aiService, IAuditService audit)
        {
            _aiService = aiService;
            _audit = audit;
        }

        /// <summary>
        /// Получить 3 варианта продолжения диалога
        /// </summary>
        [HttpGet("continuation")]
        public async Task<IActionResult> GetContinuation(int user1Id, int user2Id)
        {
            if (user1Id <= 0 || user2Id <= 0)
                return BadRequest("Invalid user ids");

            var result = await _aiService.GenerateConversationContinuationAsync(user1Id, user2Id);

            // логируем использование AI
            await _audit.LogAsync(user1Id, "AI_CONVERSATION_CONTINUATION", "Conversation",
                $"WithUser:{user2Id}");

            return Ok(new
            {
                Success = true,
                Suggestions = result
            });
        }
    }
}
