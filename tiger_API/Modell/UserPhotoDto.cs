namespace tiger_API.Modell
{
    // Modell/UserPhotoDto.cs
    public class UserPhotoDto
    {
        public int PhotoId { get; set; }
        public int UserId { get; set; }
        public string FirstName { get; set; }
        public string LastName { get; set; }
        public string Login { get; set; }
        public byte[] PhotoData { get; set; }
    }
}
