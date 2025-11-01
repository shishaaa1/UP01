using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace TaigerDesktop.Models
{
    public class Users
    {
        public int Id { get; set; }
        public string FirstName { get; set; }
        public string LastName { get; set; }
        public DateTime Birthday { get; set; }
        public string BIO { get; set; }
        public DateTime CreatedAt { get; set; }
        public string Login { get; set; }
        public string Password { get; set; }
        public DateTime BirthDay => Birthday;
        public bool Sex { get; set; }
        public string AvatarPath { get; set; }
    }
}
