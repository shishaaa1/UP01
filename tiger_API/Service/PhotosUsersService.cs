using Microsoft.EntityFrameworkCore;
using tiger_API.Context;
using tiger_API.Itreface;
using tiger_API.Modell;

namespace tiger_API.Service
{
    public class PhotosUsersService :IPhotosUsers
    {
        private readonly PhotosUserContext _photosUserContext;
        public PhotosUsersService(PhotosUserContext photosUserContext)
        {
            _photosUserContext = photosUserContext;
        }

        public async Task<int> UploadPhotoAsync(int userId, byte[] photoData)
        {
            if (photoData == null || photoData.Length == 0)
                throw new ArgumentException("Фото не может быть пустым");

            var photo = new PhotosUsers
            {
                UserId = userId,
                Photobill = photoData
            };

            _photosUserContext.Photos.Add(photo);
            await _photosUserContext.SaveChangesAsync();
            return photo.Id;
        }

        public async Task<byte[]> GetPhotoByUserIdAsync(int userId)
        {
            var photo = await _photosUserContext.Photos
                .FirstOrDefaultAsync(p => p.UserId == userId);

            if (photo?.Photobill == null)
                throw new FileNotFoundException("Фото для пользователя не найдено");

            return photo.Photobill;
        }

        public async Task<bool> DeletePhotoAsync(int photoId)
        {
            var photo = await _photosUserContext.Photos.FindAsync(photoId);
            if (photo == null)
                return false; 

            _photosUserContext.Photos.Remove(photo);
            await _photosUserContext.SaveChangesAsync();
            return true;
        }
    }
}

