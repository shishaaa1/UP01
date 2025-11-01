namespace tiger_API.Modell
{
    public class UploadPhotoRequest
    {
        public int UserId { get; set; }
        public IFormFile PhotoFile { get; set; }
    }
}
