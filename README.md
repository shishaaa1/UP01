tiger_API --- ASP.NET Core Web API

`tiger_API` --- серверное Web API, созданное на **ASP.NET Core**,
предназначенное для обработки пользователей, сообщений, лайков и
фотографий.

Проект реализован по многослойной архитектуре (**Controllers → Services
→ Interfaces → Context → Models**) и поддерживает все базовые функции
социальной платформы: взаимные лайки, переписку, фотографии и аналитику.


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
"DefaultConnection": "Server=localhost;Database=...;User=..;Password=..;"
```
Используйте MSSQL
### 5. Удалите строчку

```
app.UseHttpsRedirection();
```


``` bash
dotnet run
```

### 6. Запустите API
Запуск
Через Visual Studio --- F5

или:

dotnet run


API будет доступно по адресам:

    http://localhost:7252


## 🔐 Конфигурации

Файл `DbContext` содержит: - строку подключения к БД - настройки
логирования - пути хранения файлов



## 📌 Примечания

-   Контроллеры используют сервисы --- хорошая архитектура.
-   DTO разделяют API и базу данных.
-   Рекомендуется добавить Swagger:

``` csharp
builder.Services.AddSwaggerGen();
```









🖥️ TaigerDesktop --- Desktop-приложение (WPF) для работы с tiger_API
TaigerDesktop --- это WPF‑приложение, являющееся административной панелью для работы с серверным API (tiger_API).
Проект позволяет управлять пользователями, администраторами, фотографиями и статистикой, предоставляя удобный графический интерфейс поверх REST API.

🔗 ApiContext --- Основной класс для работы с API
ApiContext --- главный сетевой слой приложения.
Он отвечает за авторизацию, CRUD‑операции, загрузку данных и удаление записей на сервере.

📌 Функционал ApiContext
### 🔐 Авторизация администратора

Методы: - `LoginAdminAsync` --- простая авторизация (true/false) -
`LoginAdminAAsync` --- авторизация + получение Login и Nickname
(обновлённая версия)

После успешного входа: - `IsAuthenticated = true` -
`CurrentLogin = ...` - Передача информации в `App.SetAdminData()`

### 👤 Работа с администраторами

-   `AddAdminAsync(Admin admin)` --- добавление администратора\
-   `EditAdminAsync(Admin admin)` --- редактирование\
-   `DeleteAdminAsync(int id)` --- удаление\
-   `GetAllAdminsAsync()` --- получение списка администраторов

### 🧑 Пользователи

-   `GetAllUsersAsync()` --- получение всех пользователей\
-   `DeleteUserAsync(int id)` --- удаление пользователя

### 🖼 Фото пользователей

-   `GetPhotosByUsersIdAsync()` --- получение всех фото\
    Преобразует DTO → модель `PhotosUsers`\
-   `DeletePhotoAsync(int id)` --- удаление фото

DTO:

    UserPhotoDto
      - PhotoId
      - UserId
      - FirstName
      - LastName
      - Login
      - PhotoData (byte[])

### 📊 Статистика

-   `GetStatsLast30DaysAsync()` --- статистика регистраций\
    (данные из: UserController/CountUsersToday)

------------------------------------------------------------------------

# 🏗 Структура проекта

    TaigerDesktop
    │
    ├── Connect/
    │   └── ApiContext.cs
    │
    ├── Images/
    │   ├── backgroundHomePage.jpg
    │   └── icon.ico
    │
    ├── Models/
    │   ├── Admin.cs
    │   ├── DailyStat.cs
    │   ├── KpiCard.cs
    │   ├── PhotosUser.cs
    │   └── Users.cs
    │
    ├── Pages/
    │   ├── AddAdministrator.xaml
    │   ├── CheckAdministrator.xaml
    │   ├── CheckPhotos.xaml
    │   ├── CheckStat.xaml
    │   ├── CheckUsers.xaml
    │   ├── HomePage.xaml
    │   └── PhotoViewerWindow.xaml
    │
    ├── View/
    │   ├── AdministratorCard.xaml
    │   ├── UserCard.xaml
    │   └── UserPhotoCard.xaml
    │
    ├── App.xaml
    ├── MainWindow.xaml
    └── Styles.xaml

------------------------------------------------------------------------

⚙️ Используемые технологии
WPF (.NET 8)
MVVM‑структура
REST API + HttpClient
JSON (System.Text.Json)
XAML‑интерфейс
🔧 Установка и запуск
1. Открыть проект в Visual Studio 2022+
Необходим workload:
.NET Desktop Development

2. Настроить API‑адрес
Файл:

Connect/ApiContext.cs
Заменить:

BaseAddress = new Uri("https://localhost:7252/api/")
На адрес вашего сервера API.

3. Запуск
Через Visual Studio --- F5

или:

dotnet run

















# BoobleProject --- Android-приложение

## 📌 Описание

BoobleProject --- Android-приложение на Java с функционалом регистрации,
авторизации, лайков, сообщений и просмотра профилей.

## 📱 Функционал

-   Регистрация и авторизация\
-   Лайк/дизлайк пользователей\
-   Match-система\
-   Обмен сообщениями\
-   Главная страница с карточками\
-   Просмотр и редактирование профиля

## 🔗 API

Используется собственный REST API для: - Авторизации\
- Регистрации\
- Лайков\
- Получения анкет\
- Сообщений

## ⚙️ Сборка проекта

1.  Открыть проект в **Android Studio**\
2.  Убедиться, что установлена **JDK 11+**\
3.  Выполнить синхронизацию Gradle\
4.  Запустить приложение через эмулятор или устройство



## 📦 Используемые технологии

-   Java\
-   AndroidX\
-   RecyclerView\
-   Glide / Picasso (если используется)\
-   REST API\
-   MVC / MVP (в зависимости от реализации)
