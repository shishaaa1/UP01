# 🐯 tiger_API --- ASP.NET Core Web API

`tiger_API` --- серверное Web API, созданное на **ASP.NET Core**,
предназначенное для обработки пользователей, сообщений, лайков и
фотографий.

Проект реализован по многослойной архитектуре (**Controllers → Services
→ Interfaces → Context → Models**) и поддерживает все базовые функции
социальной платформы: взаимные лайки, переписку, фотографии и аналитику.

## 🚀 Основные возможности

### 👤 Пользователи

-   Регистрация, авторизация, обновление данных
-   Получение списка пользователей
-   Получение информации о конкретном пользователе

### ❤️ Лайки

-   Добавление лайка
-   Проверка взаимного лайка
-   Получение статистики по лайкам

### 💬 Сообщения

-   Отправка сообщения
-   Получение чата между пользователями
-   Очистка/удаление сообщений

### 📸 Фотографии

-   Загрузка фотографий
-   Получение фотографий пользователя
-   Привязка фотографий к профилю

### 👨‍💼 Администрирование

-   Статистика
-   Управление пользователями

## 🏗 Архитектура проекта

    tiger_API
    │
    ├── Context/
    │   ├── AdminContext.cs
    │   ├── DbConnection.cs
    │   ├── IsLikeContext.cs
    │   ├── MessageContext.cs
    │   ├── PhotosUserContext.cs
    │   └── UsersContext.cs
    │
    ├── Controllers/
    │   ├── AdminController.cs
    │   ├── IsLikeController.cs
    │   ├── MessageController.cs
    │   ├── PhotoController.cs
    │   └── UsersController.cs
    │
    ├── Interface/
    │   ├── IAdmin.cs
    │   ├── IIsLike.cs
    │   ├── IMessageService.cs
    │   ├── IPhotosUsers.cs
    │   └── IUsers.cs
    │
    ├── Modell/
    │   ├── Admin.cs
    │   ├── DailyStat.cs
    │   ├── IsLike.cs
    │   ├── Message.cs
    │   ├── PhotosUsers.cs
    │   ├── rating.cs
    │   ├── UpdatedUserDto.cs
    │   ├── UploadPhotoRequest.cs
    │   ├── UserPhotoDto.cs
    │   └── Users.cs
    │
    ├── Service/
    │   ├── AdminService.cs
    │   ├── IsLikeService.cs
    │   ├── MessageService.cs
    │   ├── PhotosUsersService.cs
    │   └── UsersService.cs
    │
    ├── appsettings.json
    ├── Program.cs
    └── tiger_API.csproj

## ⚙️ Используемые технологии

-   .NET 6 / .NET 7
-   ASP.NET Core Web API
-   Entity Framework Core
-   Dependency Injection
-   REST API
-   Работа с файлами (загрузка изображений)

## 🔧 Установка и запуск

### 1. Установите .NET SDK

Проверка версии:

``` bash
dotnet --version
```

### 2. Перейдите в каталог проекта

``` bash
cd tiger_API
```

### 3. Восстановите зависимости

``` bash
dotnet restore
```

### 4. Укажите строку подключения к базе данных

Открой файл:

    appsettings.json

И замените строку подключения:

``` json
"DefaultConnection": "Server=host;Database=name;User Id=user;Password=pass;"
```

### 5. Запустите API

``` bash
dotnet run
```

API будет доступно по адресам:

    http://localhost:7252

## 📡 Основные эндпоинты

  Контроллер          Маршруты
  ------------------- ----------------------------------------
  UsersController     /users/AddUsers, /users/LoginUsers, /users/"DeleteUser/{id}", /users/CountUsersToday, /users/GetUserById, /users/GetUsers, /users/GetUsersAndPhoto, /users/GetAllUsersWithPhoto  /users/UpdateUsers, /users/GetOppositeSexUser.
  MessageController   /message/send, /message/history,  /message/WriteMessage, /message/Conversation, /message/DeleteMessage,/message/DeleteConversation.
  IsLikeController    /iSLikeController/add, /iSLikeController/check, /iSLikeController/send, /iSLikeController/received/{userId}, /iSLikeController/sent/{userId, /iSLikeController/mutual/{user1Id}/{user2Id}, /iSLikeController/user/{userId}/matches, /iSLikeController/revokeLike
  PhotoController     /PhotoController/upload, /PhotoController/get
  AdminController     /admin/stats


## 🔐 Конфигурации

Файл `appsettings.json` содержит: - строку подключения к БД - настройки
логирования - пути хранения файлов

Рекомендуется создать файл:

    appsettings.Development.json

## 📌 Примечания

-   Контроллеры используют сервисы --- хорошая архитектура.
-   DTO разделяют API и базу данных.
-   Рекомендуется добавить Swagger:

``` csharp
builder.Services.AddSwaggerGen();
```
