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

## 🧩 Архитектура проекта
BoobleProject
│
├── app/
│   ├── src/
│   │   ├── androidTest/java/com/example/boobleproject/
│   │   ├── main/
│   │   │   ├── java/com/example/boobleproject/
│   │   │   │   ├── Account/
│   │   │   │   ├── Api/
│   │   │   │   ├── LikesOrNot/
│   │   │   │   ├── Logins/
│   │   │   │   ├── MainPage/
│   │   │   │   ├── MessageMatch/
│   │   │   │   ├── Registration/
│   │   │   │   ├── LikeManager.java
│   │   │   │   ├── Mainpage.java
│   │   │   │   ├── Profile.java
│   │   │   │   └── ProfileAdapter.java
│   │   │   ├── res/
│   │   │   │   ├── anim/
│   │   │   │   ├── drawable/
│   │   │   │   ├── layout/
│   │   │   │   ├── mipmap-*/ 
│   │   │   │   ├── values/
│   │   │   │   └── xml/
│   │   │   ├── AndroidManifest.xml
│   │   ├── test/java/com/example/boobleproject/
│   │
│   ├── build.gradle
│   ├── proguard-rules.pro
│
├── gradle/
│   ├── wrapper/
│   │   ├── gradle-wrapper.jar
│   │   └── gradle-wrapper.properties
│   └── libs.versions.toml
│
└── .idea (служебные файлы Android Studio)


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

## 🧪 Тестирование

Тесты находятся по пути:

    src/test/java/com/example/boobleproject/

## 📦 Используемые технологии

-   Java\
-   AndroidX\
-   RecyclerView\
-   Glide / Picasso (если используется)\
-   REST API\
-   MVC / MVP (в зависимости от реализации)
