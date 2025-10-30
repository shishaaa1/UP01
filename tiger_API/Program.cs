using Microsoft.OpenApi.Models;
using System.Reflection;
using Microsoft.AspNetCore;
using tiger_API.Itreface; 
using tiger_API.Service;
using tiger_API.Context;
using System.Data.Common;

var builder = WebApplication.CreateBuilder(args);

builder.Services.AddControllers();

builder.Services.AddScoped<IUsers, UsersService>(); // реализация интерфейса и сервиса
builder.Services.AddScoped<UsersContext>(); // реализация интерфейса и сервиса

builder.Services.AddEndpointsApiExplorer();
builder.Services.AddSwaggerGen(c =>
{
    c.SwaggerDoc("v1", new OpenApiInfo
    {
        Title = "tiger_API",
        Version = "v1",
        Description = "Методы в контроллере"
    });

    var xmlFile = $"{Assembly.GetExecutingAssembly().GetName().Name}.xml";
    var xmlPath = Path.Combine(AppContext.BaseDirectory, xmlFile);
    c.IncludeXmlComments(xmlPath);
    
});

var app = builder.Build();

if (app.Environment.IsDevelopment())
{
    app.UseSwagger();
    app.UseSwaggerUI(c =>
    {
        c.SwaggerEndpoint("/swagger/v1/swagger.json", "tiger_API v1");
        c.RoutePrefix = string.Empty;
    });
}

app.UseHttpsRedirection();
app.UseAuthorization();
app.MapControllers();

app.Run();