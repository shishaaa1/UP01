using Microsoft.AspNetCore.Mvc;
using Moq;
using System.Text.Json;
using System.Threading.Tasks;
using tiger_API.Controllers;
using tiger_API.Itreface;
using tiger_API.Modell;
using Xunit;

namespace tiger_API.Tests.ControllerTest
{
    public class AdminControllerTests
    {
        private readonly Mock<IAdmin> _mockAdminService;
        private readonly AdminController _controller;

        public AdminControllerTests()
        {
            _mockAdminService = new Mock<IAdmin>();
            _controller = new AdminController(_mockAdminService.Object);
        }

        [Fact]
        public async Task UpdateAdmins_WithValidData_ReturnsOkResult()
        {
            // Arrange
            var admin = new Admin
            {
                Login = "testuser",
                Nickname = "Test User",
                Password = "newpassword"
            };

            _mockAdminService
                .Setup(service => service.UpdateAdmin(It.IsAny<Admin>()))
                .ReturnsAsync(true);

            // Act
            var result = await _controller.UpdateAdmins(admin);

            // Assert
            var okResult = Assert.IsType<OkObjectResult>(result);
            var anonymousObject = okResult.Value; // Это анонимный объект

            // Используем reflection, чтобы получить message
            var messageProperty = anonymousObject.GetType().GetProperty("message");
            var message = (string)messageProperty.GetValue(anonymousObject);

            Assert.Contains("успешно обновлён", message);
        }

        [Fact]
        public async Task UpdateAdmins_WithInvalidLogin_ReturnsBadRequest()
        {
            // Arrange
            var admin = new Admin
            {
                Login = "", // Некорректный логин
                Nickname = "Test User"
            };

            // Act
            var result = await _controller.UpdateAdmins(admin);

            // Assert
            var badRequestResult = Assert.IsType<BadRequestObjectResult>(result);
            Assert.Contains("Логин обязателен", (string)badRequestResult.Value);
        }

        [Fact]
        public async Task UpdateAdmins_WhenServiceReturnsFalse_ReturnsNotFound()
        {
            // Arrange
            var admin = new Admin
            {
                Login = "nonexistentuser",
                Nickname = "Test User"
            };

            _mockAdminService
                .Setup(service => service.UpdateAdmin(It.IsAny<Admin>()))
                .ReturnsAsync(false);

            // Act
            var result = await _controller.UpdateAdmins(admin);

            // Assert
            var notFoundResult = Assert.IsType<NotFoundObjectResult>(result);
            var anonymousObject = notFoundResult.Value; // Это анонимный объект

            // Используем reflection, чтобы получить message
            var messageProperty = anonymousObject.GetType().GetProperty("message");
            var message = (string)messageProperty.GetValue(anonymousObject);

            Assert.Contains("не найден", message);
        }

        [Fact]
        public async Task LoginAdmin_WithValidCredentials_ReturnsOk()
        {
            // Arrange
            const string login = "validuser";
            const string password = "validpass";
            const int adminId = 99;

            _mockAdminService
                .Setup(service => service.LoginAdmin(login, password))
                .ReturnsAsync(adminId);

            // Act
            var result = await _controller.LoginAdmin(login, password);

            // Assert
            var okResult = Assert.IsType<OkObjectResult>(result);

            // okResult.Value — это анонимный объект
            var anonymousObject = okResult.Value;

            // Используем reflection, чтобы получить AdminId
            var adminIdProperty = anonymousObject.GetType().GetProperty("AdminId");
            var actualId = (int)adminIdProperty.GetValue(anonymousObject);

            Assert.Equal(adminId, actualId);
        }

        [Fact]
        public async Task LoginAdmin_WithInvalidCredentials_ReturnsUnauthorized()
        {
            // Arrange
            const string login = "invalid";
            const string password = "wrong";

            _mockAdminService
                .Setup(service => service.LoginAdmin(login, password))
                .ReturnsAsync(0); // 0 означает не найден

            // Act
            var result = await _controller.LoginAdmin(login, password);

            // Assert
            Assert.IsType<UnauthorizedResult>(result);
        }
    }
}
