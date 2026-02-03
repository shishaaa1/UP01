using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace TaigerDesktop.Models
{
    public class UserActivityLog
    {
        public int Id { get; set; }
        public int UserId { get; set; }
        public string Action { get; set; } = string.Empty;
        public string? Entity { get; set; }
        public string? Details { get; set; }
        public DateTime CreatedAt { get; set; }
        public string? IpAddress { get; set; }

        public string FormattedTime => CreatedAt.ToString("dd.MM.yyyy HH:mm:ss");
        public string ActionDisplay => Action switch
        {
            "LOGIN" => "Вход в систему",
            "LOGOUT" => "Выход из системы",
            "SEND_MESSAGE" => "Отправка сообщения",
            "LIKE" => "Поставил(а) лайк",
            "PHOTO_UPLOAD" => "Загрузка фото",
            "PROFILE_EDIT" => "Редактирование профиля",
            _ => Action
        };
    }
}
