namespace tiger_API.Modell
{
    public class UserActivityLog
    {
        public int Id { get; set; }

        public int UserId { get; set; }              // Кто совершил действие
        public string Action { get; set; } = null!;  // Тип действия (LOGIN, SEND_MESSAGE, LIKE и т.д.)
        public string? Entity { get; set; }          // Над чем действие (Message, Photo, Profile)
        public string? Details { get; set; }         // Доп. инфа (id сообщения, текст ошибки и т.п.)

        public DateTime CreatedAt { get; set; } = DateTime.UtcNow;

        public string? IpAddress { get; set; }
    }
}
