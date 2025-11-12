using Microsoft.EntityFrameworkCore;
using System;
using System.Threading.Tasks;
using tiger_API.Context;
using tiger_API.Modell;
using tiger_API.Service;
using Xunit;

namespace tiger_API.Tests.ServiceTest
{
    public class AdminServiceTests
    {
        private DbContextOptions<AdminContext> CreateNewContextOptions()
        {
            return new DbContextOptionsBuilder<AdminContext>()
                .UseInMemoryDatabase(databaseName: Guid.NewGuid().ToString()) // Уникальное имя для каждого теста
                .Options;
        }

        [Fact]
        public async Task UpdateAdmin_UpdatesNickname_WhenUserExists()
        {
            // Arrange
            using var context = new AdminContext();

            var uniqueLogin = $"testuser_{Guid.NewGuid()}"; // <= Уникальный логин
            var admin = new Admin { Login = uniqueLogin, Nickname = "Old Name", Password = "pass" };
            context.Admin.Add(admin);
            await context.SaveChangesAsync();

            var service = new AdminService(context);

            var updateModel = new Admin { Login = uniqueLogin, Nickname = "New Name" };

            // Act
            var result = await service.UpdateAdmin(updateModel);

            // Assert
            Assert.True(result);

            var updatedAdmin = await context.Admin.FirstOrDefaultAsync(a => a.Login == uniqueLogin);
            Assert.NotNull(updatedAdmin);
            Assert.Equal("New Name", updatedAdmin.Nickname);
        }

        [Fact]
        public async Task UpdateAdmin_ReturnsFalse_WhenUserNotFound()
        {
            // Arrange
            var options = CreateNewContextOptions();
            using var context = new AdminContext();
            var service = new AdminService(context);

            var updateModel = new Admin { Login = "nonexistent", Nickname = "New Name" };

            // Act
            var result = await service.UpdateAdmin(updateModel);

            // Assert
            Assert.False(result);
        }

        [Fact]
        public async Task LoginAdmin_ReturnsCorrectId_WhenCredentialsValid()
        {
            // Arrange
            using var context = new AdminContext();

            var uniqueLogin = $"validuser_{Guid.NewGuid()}"; // <= Уникальный логин каждый раз
            var admin = new Admin { Login = uniqueLogin, Password = "validpass", Nickname = "Test" };
            context.Admin.Add(admin);
            await context.SaveChangesAsync();

            var service = new AdminService(context);

            // Act
            var result = await service.LoginAdmin(uniqueLogin, "validpass");

            // Assert
            Assert.Equal(admin.Id, result);
        }

        [Fact]
        public async Task LoginAdmin_ReturnsZero_WhenCredentialsInvalid()
        {
            // Arrange
            using var context = new AdminContext();

            var uniqueLogin = $"validuser_{Guid.NewGuid()}";
            var admin = new Admin { Login = uniqueLogin, Password = "validpass", Nickname = "Test" };
            context.Admin.Add(admin);
            await context.SaveChangesAsync();

            var service = new AdminService(context);

            // Act & Assert
            await Assert.ThrowsAsync<InvalidOperationException>(
                () => service.LoginAdmin(uniqueLogin, "wrongpass")
            );
        }
    }
}
