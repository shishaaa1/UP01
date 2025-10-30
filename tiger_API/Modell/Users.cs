namespace tiger_API.Modell
{
    public class Users
    {
        public int Id { get; set; }
        public string FirstName { get; set; }
        public string LastName { get; set; }
        public DateTime Birthday {  get; set; }
        public string BIO { get; set; }
        public DateTime CreatedAt { get; set; }
        public bool Sex { get; set; }
        public string Login {  get; set; }
        public string Password {  get; set; }
    }
}
