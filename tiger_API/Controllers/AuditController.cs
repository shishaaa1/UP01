using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using tiger_API.Context;
using tiger_API.Itreface;
using tiger_API.Modell;

namespace tiger_API.Controllers
{
    [Route("api/Audit")]
    [ApiController]
    public class AuditController : ControllerBase
    {
        private readonly IAuditService _auditServis;

        public AuditController(IAuditService auditServis)
        {
            _auditServis = auditServis;
        }

        /// <summary>
        /// Получить все логи активности
        /// </summary>
        /// <returns>Список всех записей лога</returns>
        [Route("GetAllLogs")]
        [HttpGet]
        public async Task<ActionResult<List<UserActivityLog>>> GetAllLogs()
        {
            try
            {
                var logs= await _auditServis.GetAllUsersLogsAsync();
                return Ok(logs);
            }
            catch (Exception ex)
            {
                return StatusCode(500, new { success = false, error = ex.Message });
            }
        }

        /// <summary>
        /// Получить все логи активности
        /// </summary>
        /// <returns>Список всех записей лога</returns>
        [Route("GetlogsByUserId")]
        [HttpGet]
        public async Task<IActionResult> GetUserslogsId(int id)
        {
            var res = await _auditServis.GetLogsByIdUser(id);
            return Ok(res);
            
        }
        [HttpDelete("ClearAll")]
        public async Task<IActionResult> ClearAll()
        {
            try
            {
                await _auditServis.ClearAllLogsAsync();
                return Ok(new { success = true, message = "Журнал аудита полностью очищен." });
            }
            catch (Exception ex)
            {
                return StatusCode(500, new { success = false, error = ex.Message });
            }
        }

        [HttpDelete("Trim")]
        public async Task<IActionResult> Trim()
        {
            try
            {
                
                await _auditServis.TrimLogsAsync(100);
                return Ok(new { success = true, message = "Старые логи удалены. Оставлено 100 последних записей." });
            }
            catch (Exception ex)
            {
                return StatusCode(500, new { success = false, error = ex.Message });
            }
        }
    }
}
