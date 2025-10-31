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
    }
}

