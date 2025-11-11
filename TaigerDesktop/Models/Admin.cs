using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace TaigerDesktop.Models
{
    public class Admin
    {
        public int Id { get; set; }
        public string Login { get; set; }
        public string? Password { get; set; }
        public string Nickname { get; set; }

    }
}
