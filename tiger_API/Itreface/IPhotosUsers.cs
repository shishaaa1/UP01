using tiger_API.Modell;

namespace tiger_API.Itreface
{
    public interface IPhotosUsers
    {
        Task<int> UploadPhotoAsync(int userId, byte[] photoData);
        Task<byte[]> GetPhotoByUserIdAsync(int userId);
        Task<bool> DeletePhotoAsync(int photoId);
        Task<List<PhotosUsers>> GetAllPhotosAsync();
        Task<List<UserPhotoDto>> GetAllPhotosWithUserDataAsync();
        Task DeletePhotosByUserIdAsync(int userId);
    }
}
