using Microsoft.EntityFrameworkCore;
using tiger_API.Context;
using tiger_API.Itreface;
using tiger_API.Modell;

namespace tiger_API.Service
{
    public class PhotosUsersService :IPhotosUsers
    {
        private readonly PhotosUserContext _photosUserContext;
        private readonly UsersContext _usersContext;
        public PhotosUsersService(PhotosUserContext photosUserContext, UsersContext usersContext)
        {
            _photosUserContext = photosUserContext;
            _usersContext = usersContext;
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
        public async Task DeletePhotosByUserIdAsync(int userId)
        {
            var photos = await _photosUserContext.Photos
                .Where(p => p.UserId == userId)
                .ToListAsync();

            
                _photosUserContext.Photos.RemoveRange(photos);
                await _photosUserContext.SaveChangesAsync();
            
        }

        public async Task<int> GetUserPhotoIdAsync(int userId)
        {
            var photo= await _photosUserContext.Photos.FirstOrDefaultAsync(x=>x.UserId == userId);
            return photo.Id;
        }

        public async Task<List<PhotosUsers>> GetAllPhotosAsync()
        {
            return await _photosUserContext.Photos.ToListAsync();
        }
        

        public async Task<List<UserPhotoDto>> GetAllPhotosWithUserDataAsync()
        {
            return await _photosUserContext.Photos
                .Join(
                    _photosUserContext.Users,                   
                    photo => photo.UserId,                       
                    user => user.Id,                             
                    (photo, user) => new UserPhotoDto           
                    {
                        PhotoId = photo.Id,
                        UserId = user.Id,
                        FirstName = user.FirstName,
                        LastName = user.LastName,
                        Login = user.Login,
                        PhotoData = photo.Photobill
                    })
                .ToListAsync();
        }
    }
}

