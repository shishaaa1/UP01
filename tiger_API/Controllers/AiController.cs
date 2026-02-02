using Microsoft.AspNetCore.Mvc;
using tiger_API.Itreface;

namespace tiger_API.Controllers
{
    [ApiController]
    [Route("api/[controller]")]
    public class AiController : ControllerBase
    {
        private readonly AiInterface _aiService;
        private readonly ILogger<AiController> _logger;

        public AiController(AiInterface aiService, ILogger<AiController> logger)
        {
            _aiService = aiService;
            _logger = logger;
        }

        [HttpPost("chat")]
        public async Task<IActionResult> Chat([FromForm] ChatRequest request)
        {
            try
            {
                if (string.IsNullOrWhiteSpace(request.Message))
                {
                    return BadRequest(new { error = "Message is required" });
                }
                bool work=false;
                _logger.LogInformation("Processing chat request: {Message}", request.Message);
                var response = await _aiService.GetChatCompletionAsync(request.Message);
                return Ok(new { response });
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Error processing chat request");
                return StatusCode(500, new { error = "Internal server error", details = ex.Message });
            }
        }
        [HttpGet("status")]
        public async Task<IActionResult> GetAssistantStatus()
        {
            try
            {
                _logger.LogInformation("Checking AI assistant status via API");
                var status = await _aiService.GetAssistantStatusAsync();

                return Ok(new { status });
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Error getting assistant status");
                return StatusCode(500, new { error = "Internal server error", details = ex.Message });
            }
        }
        [HttpGet("match-suggestions/{userId}")]
        public async Task<IActionResult> GetMatchSuggestions( int userId)
        {
            try
            {
                if (userId <= 0)
                {
                    return BadRequest(new { error = "Valid user ID is required" });
                }

                _logger.LogInformation("Getting match suggestions for user {UserId}", userId);
                var suggestions = await _aiService.GenerateMatchSuggestionAsync(userId);
                return Ok(new { suggestions });
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Error getting match suggestions for user {UserId}", userId);
                return StatusCode(500, new { error = "Internal server error", details = ex.Message });
            }
        }

        [HttpGet("icebreaker/{user1Id}/{user2Id}")]
        public async Task<IActionResult> GenerateIcebreaker( int user1Id,  int user2Id)
        {
            try
            {
                if (user1Id <= 0 || user2Id <= 0)
                {
                    return BadRequest(new { error = "Valid user IDs are required" });
                }

                _logger.LogInformation("Generating icebreaker for users {User1Id} and {User2Id}", user1Id, user2Id);
                var icebreakers = await _aiService.GenerateIcebreakerAsync(user1Id, user2Id);
                return Ok(new { icebreakers });
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Error generating icebreaker for users {User1Id} and {User2Id}", user1Id, user2Id);
                return StatusCode(500, new { error = "Internal server error", details = ex.Message });
            }
        }
    }

    public class ChatRequest
    {
        public string Message { get; set; } = string.Empty;
    }
}