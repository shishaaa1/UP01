using Microsoft.OpenApi.Models;
using System.Reflection;
using Microsoft.AspNetCore;
using tiger_API.Itreface; 
using tiger_API.Service;
using tiger_API.Context;
using System.Data.Common;
using tiger_API;
using Microsoft.AspNetCore.SignalR;

var builder = WebApplication.CreateBuilder(args);

builder.Services.AddControllers()
    .AddJsonOptions(options =>
    {
        options.JsonSerializerOptions.Encoder = System.Text.Encodings.Web.JavaScriptEncoder.Create(
            System.Text.Unicode.UnicodeRanges.BasicLatin,
            System.Text.Unicode.UnicodeRanges.Cyrillic); 
    });

builder.Services.AddScoped<IUsers, UsersService>(); // реализация интерфейса и сервиса
builder.Services.AddScoped<IAdmin, AdminService>(); // реализация интерфейса и сервиса
builder.Services.AddScoped<IPhotosUsers, PhotosUsersService>(); // реализация интерфейса и сервиса
builder.Services.AddScoped<IMessageService, MessageService>();
builder.Services.AddScoped<IIsLike, iSLikeService>();
builder.Services.AddScoped<UsersContext>(); // реализация интерфейса и сервиса
builder.Services.AddScoped<AdminContext>(); // реализация интерфейса и сервиса
builder.Services.AddScoped<PhotosUserContext>(); // реализация интерфейса и сервиса
builder.Services.AddScoped<MessegeContext>(); // реализация интерфейса и сервиса
builder.Services.AddScoped<iSLikeContext>(); // реализация интерфейса и сервиса


builder.Services.AddSignalR();
builder.Services.AddEndpointsApiExplorer();
builder.Services.AddSwaggerGen(c =>
{
    c.SwaggerDoc("v1", new OpenApiInfo
    {
        Title = "tiger_API",
        Version = "v1",
        Description = "Методы в контроллере"
    });
    c.OperationFilter<SwaggerFileOperationFilter>();

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
    });
}

app.UseHttpsRedirection();
app.UseAuthorization();
app.MapControllers();

app.Run();