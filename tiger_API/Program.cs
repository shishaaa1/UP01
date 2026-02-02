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
builder.Services.AddScoped<IUsers, UsersService>();
builder.Services.AddScoped<AiInterface, AiService>();
builder.Services.AddScoped<IPhotosUsers, PhotosUsersService>();
builder.Services.AddHttpClient("HuggingFace", (serviceProvider, client) =>
{
    var configuration = serviceProvider.GetRequiredService<IConfiguration>();
    var apiKey = configuration["HuggingFace:ApiKey"] ?? Environment.GetEnvironmentVariable("HF_TOKEN");

    if (string.IsNullOrEmpty(apiKey))
    {
        throw new InvalidOperationException("HuggingFace API key is not configured. Set it in appsettings.json or as HF_TOKEN environment variable.");
    }

    client.BaseAddress = new Uri("https://router.huggingface.co/v1/"); // Убраны лишние пробелы!
    client.DefaultRequestHeaders.Add("Accept", "application/json");
    client.DefaultRequestHeaders.Add("Authorization", $"Bearer {apiKey}");

    // Для отладки (опционально, не в продакшене!)
    // Console.WriteLine($"API Key loaded: {apiKey.Substring(0, 5)}...{apiKey.Substring(apiKey.Length - 5)}");
}); 

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