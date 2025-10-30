using Microsoft.AspNetCore.Mvc;
using tiger_API.Itreface;

namespace tiger_API.Controllers
{
    [Route("api/TigerController")]
    public class TigerController : Controller
    {
        private readonly ITigger _tigger;

        public TigerController(ITigger tigger)
        {
            _tigger = tigger;
        }
        /// <summary>
        /// Тестовая фигня
        /// </summary>
        /// <remarks>Данный метод для проверки</remarks>
        /// <returns></returns>
        [Route("ok")]
        [HttpGet]
        public IActionResult Get()
        {
            return Ok();
        }
    }
}
