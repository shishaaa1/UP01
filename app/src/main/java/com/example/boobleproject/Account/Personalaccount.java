package com.example.boobleproject.Account;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.boobleproject.Api.ApiClient;
import com.example.boobleproject.Api.ApiService;
import com.example.boobleproject.Profile;
import com.example.boobleproject.R;
import com.google.android.material.button.MaterialButton;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class Personalaccount extends AppCompatActivity {

    private TextView firstNameTextView;
    private TextView lastNameTextView;
    private TextView bioTextView;
    private int currentUserId;
    private int currentPhotoId = -1;
    private boolean photoChanged = false;
    private Uri selectedImageUri;
    private CircleImageView profilePhoto;
    private ImageButton bthAddPhoto;
    private MaterialButton btnEditProfile;
    private SharedPreferences prefs;
    private static final String PREFS_NAME = "ProfilePrefs";
    private static final String PHOTO_PATH_KEY = "profile_photo_path";
    private final ActivityResultLauncher<String> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    setProfilePhoto(uri);
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_personalaccount);


        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        profilePhoto = findViewById(R.id.iv_profile_photo);
        bthAddPhoto = findViewById(R.id.btn_add_photo);
        btnEditProfile = findViewById(R.id.btn_edit_profile);
        firstNameTextView = findViewById(R.id.et_first_name);
        lastNameTextView = findViewById(R.id.et_last_name);
        bioTextView = findViewById(R.id.et_bio);

        bthAddPhoto.setOnClickListener(v -> openGallery());
        btnEditProfile.setOnClickListener(v -> EditProfile());

        SharedPreferences userPrefs = getSharedPreferences("userPrefs", MODE_PRIVATE);
        currentUserId = userPrefs.getInt("userId", -1); // Сохраняем в поле класса

        Log.d("PersonalAccount", "Загружен userId: " + currentUserId); // Добавьте логирование

        if (currentUserId != -1) {
            loadUserProfile(currentUserId);
        } else {
            Toast.makeText(this, "Ошибка: пользователь не найден", Toast.LENGTH_SHORT).show();
        }
    }

    private void openGallery() {
        galleryLauncher.launch("image/*");
    }

    private void setProfilePhoto(Uri uri) {
        try {
            selectedImageUri = uri;
            photoChanged = true;

            InputStream inputStream = getContentResolver().openInputStream(uri);
            if (inputStream != null) {
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                profilePhoto.setImageBitmap(bitmap);
                inputStream.close();
            }

            Log.d("PersonalAccount", "Фото выбрано, photoChanged = " + photoChanged);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadUserProfile(int userId) {
        ApiService apiService = ApiClient.getApiService();
        apiService.getUserWithPhoto(userId).enqueue(new Callback<Profile>() {
            @Override
            public void onResponse(Call<Profile> call, Response<Profile> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Profile user = response.body();

                    firstNameTextView.setText(user.getFirstName());
                    lastNameTextView.setText(user.getLastName());
                    bioTextView.setText(user.getBio());
                    if (user.getPhotoBytes() != null && !user.getPhotoBytes().isEmpty()) {
                        byte[] bytes = android.util.Base64.decode(user.getPhotoBytes(), android.util.Base64.DEFAULT);
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        profilePhoto.setImageBitmap(bitmap);
                        loadUserPhotoId(userId);
                    } else {
                        profilePhoto.setImageResource(R.drawable.alt1);
                    }

                    currentUserId = userId;
                    Log.d("PersonalAccount", "currentUserId установлен: " + currentUserId);
                }
            }

            @Override
            public void onFailure(Call<Profile> call, Throwable t) {
                Log.e("PersonalAccount", "Ошибка при загрузке профиля", t);
                Toast.makeText(Personalaccount.this, "Ошибка сети: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }



    private void loadUserPhotoId(int userId) {
        ApiService apiService = ApiClient.getApiService();
        apiService.getUserPhotoId(userId).enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                if (response.isSuccessful() && response.body() != null) {
                    currentPhotoId = response.body();
                    Log.d("PersonalAccount", "Получен photoId: " + currentPhotoId);
                } else {
                    currentPhotoId = -1;
                    Log.d("PersonalAccount", "Фото не найдено, photoId: " + currentPhotoId);
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                Log.e("PersonalAccount", "Ошибка при загрузке ID фото", t);
                currentPhotoId = -1;
            }
        });
    }

    private void updatePhoto() {
        Log.d("PersonalAccount", "updatePhoto: currentPhotoId = " + currentPhotoId);
        if (currentPhotoId != -1) {
            deleteOldPhoto();
        } else {
            uploadNewPhoto();
        }
    }


    private void deleteOldPhoto() {
        ApiService apiService = ApiClient.getApiService();
        apiService.deletePhoto(currentPhotoId).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Log.d("PersonalAccount", "Старое фото удалено, ID: " + currentPhotoId);
                    uploadNewPhoto();
                } else {
                    Log.e("PersonalAccount", "Ошибка при удалении старого фото: " + response.code());
                    Toast.makeText(Personalaccount.this, "Ошибка при удалении старого фото", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("PersonalAccount", "Ошибка при удалении фото", t);
                Toast.makeText(Personalaccount.this, "Ошибка при удалении фото: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
    public void Main(View view) {
        onBackPressed();
    }

    public void EditProfile(){
        if (currentUserId <= 0) {
            Toast.makeText(this, "Ошибка: ID пользователя не найден", Toast.LENGTH_SHORT).show();
            return;
        }

        String firstName = firstNameTextView.getText().toString().trim();
        String lastName = lastNameTextView.getText().toString().trim();
        String bio = bioTextView.getText().toString().trim();

        if (firstName.isEmpty() || lastName.isEmpty()) {
            Toast.makeText(this, "Имя и фамилия обязательны для заполнения", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d("PersonalAccount", "Обновление профиля для userId: " + currentUserId);
        updateUserData(firstName, lastName, bio);
    }

    private void updateUserData(String firstName, String lastName, String bio) {
        ApiService apiService = ApiClient.getApiService();

        Log.d("PersonalAccount", "Обновление пользователя: userId=" + currentUserId +
                ", firstName=" + firstName + ", lastName=" + lastName + ", bio=" + bio);

        RequestBody firstNameBody = RequestBody.create(MediaType.parse("text/plain"), firstName);
        RequestBody lastNameBody = RequestBody.create(MediaType.parse("text/plain"), lastName);
        RequestBody bioBody = RequestBody.create(MediaType.parse("text/plain"), bio);

        apiService.updateUser(currentUserId, firstNameBody, lastNameBody, bioBody)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        Log.d("PersonalAccount", "UpdateUser Response code: " + response.code());

                        if (response.isSuccessful()) {
                            Log.d("PersonalAccount", "Данные пользователя обновлены, проверяем фото: photoChanged=" + photoChanged);
                            if (photoChanged && selectedImageUri != null) {
                                Log.d("PersonalAccount", "Начинаем процесс обновления фото");
                                updatePhoto();
                            } else {
                                Toast.makeText(Personalaccount.this, "Профиль успешно обновлен", Toast.LENGTH_SHORT).show();
                                loadUserProfile(currentUserId);
                            }
                        } else {
                            Log.e("PersonalAccount", "Ошибка при обновлении данных. Код: " + response.code());
                            Toast.makeText(Personalaccount.this, "Ошибка при обновлении данных: " + response.code(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Log.e("PersonalAccount", "Ошибка сети при обновлении данных", t);
                        Toast.makeText(Personalaccount.this, "Ошибка сети: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void uploadNewPhoto() {
        Log.d("PersonalAccount", "uploadNewPhoto вызван, selectedImageUri: " + selectedImageUri);

        if (selectedImageUri == null) {
            Log.e("PersonalAccount", "selectedImageUri is null");
            Toast.makeText(this, "Фото не выбрано", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            InputStream inputStream = getContentResolver().openInputStream(selectedImageUri);
            if (inputStream == null) {
                Log.e("PersonalAccount", "Не удалось открыть InputStream");
                Toast.makeText(this, "Ошибка чтения фото", Toast.LENGTH_SHORT).show();
                return;
            }

            Log.d("PersonalAccount", "InputStream получен, создаем временный файл");

            File file = new File(getCacheDir(), "temp_photo_" + System.currentTimeMillis() + ".jpg");
            FileOutputStream outputStream = new FileOutputStream(file);

            byte[] buffer = new byte[1024];
            int length;
            int totalBytes = 0;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
                totalBytes += length;
            }

            inputStream.close();
            outputStream.close();

            Log.d("PersonalAccount", "Временный файл создан, размер: " + totalBytes + " байт");

            RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"), file);
            MultipartBody.Part photoPart = MultipartBody.Part.createFormData("PhotoFile", "profile.jpg", requestFile);
            RequestBody userIdBody = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(currentUserId));

            Log.d("PersonalAccount", "Отправляем запрос UploadPhoto для userId: " + currentUserId);

            ApiService apiService = ApiClient.getApiService();
            apiService.uploadPhoto(userIdBody, photoPart).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    Log.d("PersonalAccount", "UploadPhoto Response code: " + response.code());

                    if (file.exists()) {
                        file.delete();
                    }

                    if (response.isSuccessful()) {
                        Log.d("PersonalAccount", "Фото успешно загружено");
                        Toast.makeText(Personalaccount.this, "Профиль и фото успешно обновлены", Toast.LENGTH_SHORT).show();
                        photoChanged = false;
                        // УБИРАЕМ savePhotoLocally()
                        loadUserProfile(currentUserId); // Обновляем данные из API
                    } else {
                        Log.e("PersonalAccount", "Ошибка загрузки фото: " + response.code());
                        Toast.makeText(Personalaccount.this, "Ошибка при загрузке фото: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Log.e("PersonalAccount", "Ошибка сети при загрузке фото", t);

                    if (file.exists()) {
                        file.delete();
                    }

                    Toast.makeText(Personalaccount.this, "Ошибка сети при загрузке фото: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        } catch (Exception e) {
            Log.e("PersonalAccount", "Ошибка в uploadNewPhoto", e);
            Toast.makeText(this, "Ошибка при обработке фото", Toast.LENGTH_SHORT).show();
        }
    }
}