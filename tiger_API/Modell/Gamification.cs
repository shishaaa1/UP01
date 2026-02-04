using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace tiger_API.Modell
{
    public class Gamification
    {
        [Key]
        [DatabaseGenerated(DatabaseGeneratedOption.None)] // UserId будет ключом, без автоинкремента
        public int UserId { get; set; }

        public int TotalLoginDays { get; set; } = 0;          // Общее количество уникальных дней входов
        public int ConsecutiveLoginDays { get; set; } = 0;    // Стрик (дней подряд)
        public DateTime? LastLoginDate { get; set; }          // Дата последнего входа
        public int LikesGivenCount { get; set; } = 0;         // Количество лайков, которые дал пользователь

        [ForeignKey("UserId")]
        public virtual Users User { get; set; }               // Связь с Users (опционально, для навигации)
    }
}