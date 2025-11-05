package com.example.boobleproject;

import android.app.ProgressDialog;
import android.content.Intent;
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
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.squareup.picasso.Picasso;

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
import java.io.IOException;
import java.io.InputStream;

import de.hdodenhof.circleimageview.CircleImageView;

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
        loadSavedPhoto();

        // Загружаем userId из SharedPreferences
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
            // Копируем файл во внутреннее хранилище приложения
            String savedPath = copyImageToInternalStorage(uri);
            if (savedPath != null) {
                // Загружаем из внутреннего хранилища
                loadImageFromInternalStorage(savedPath);
                // Сохраняем путь
                savePhotoPath(savedPath);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String copyImageToInternalStorage(Uri uri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            if (inputStream == null) return null;

            // Создаем файл во внутреннем хранилище
            File internalFile = new File(getFilesDir(), "profile_photo.jpg");
            FileOutputStream outputStream = new FileOutputStream(internalFile);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }

            inputStream.close();
            outputStream.close();

            return internalFile.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void loadImageFromInternalStorage(String filePath) {
        try {
            File file = new File(filePath);
            if (file.exists()) {
                Bitmap bitmap = BitmapFactory.decodeFile(filePath);
                profilePhoto.setImageBitmap(bitmap);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void savePhotoPath(String filePath) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PHOTO_PATH_KEY, filePath);
        editor.apply();
    }

    private void loadSavedPhoto() {
        String savedPath = prefs.getString(PHOTO_PATH_KEY, null);
        if (savedPath != null) {
            loadImageFromInternalStorage(savedPath);
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
                        // Получаем ID фото пользователя
                        loadUserPhotoId(userId);
                    } else {
                        loadSavedPhoto();
                    }

                    // Убедитесь, что currentUserId установлен
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
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                Log.e("PersonalAccount", "Ошибка при загрузке ID фото", t);
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
            Log.e("PersonalAccount", "currentUserId = " + currentUserId);
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

    private void updatePhoto() {
        if (currentPhotoId != -1) {
            deleteOldPhoto();
        } else {
            uploadNewPhoto();
        }
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
                        Log.d("PersonalAccount", "Response code: " + response.code());

                        if (response.isSuccessful()) {
                            if (photoChanged && selectedImageUri != null) {
                                updatePhoto();
                            } else {
                                // УБИРАЕМ hideProgressDialog
                                Toast.makeText(Personalaccount.this, "Профиль успешно обновлен", Toast.LENGTH_SHORT).show();
                                loadUserProfile(currentUserId); // Обновляем данные на экране
                            }
                        } else {
                            // УБИРАЕМ hideProgressDialog
                            Log.e("PersonalAccount", "Ошибка при обновлении данных. Код: " + response.code());
                            Toast.makeText(Personalaccount.this, "Ошибка при обновлении данных: " + response.code(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        // УБИРАЕМ hideProgressDialog
                        Log.e("PersonalAccount", "Ошибка сети: " + t.getMessage(), t);
                        Toast.makeText(Personalaccount.this, "Ошибка сети: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void deleteOldPhoto() {
        ApiService apiService = ApiClient.getApiService();
        apiService.deletePhoto(currentPhotoId).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    uploadNewPhoto();
                } else {

                    Toast.makeText(Personalaccount.this, "Ошибка при удалении старого фото", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

                Toast.makeText(Personalaccount.this, "Ошибка при удалении фото: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void uploadNewPhoto() {
        if (selectedImageUri == null) {
            Toast.makeText(this, "Фото не выбрано", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            // Получаем InputStream из Uri
            InputStream inputStream = getContentResolver().openInputStream(selectedImageUri);
            if (inputStream == null) {
                Toast.makeText(this, "Ошибка чтения фото", Toast.LENGTH_SHORT).show();
                return;
            }

            // Читаем байты из потока
            byte[] photoBytes;
            try {
                // Создаем ByteArrayOutputStream для чтения байтов
                java.io.ByteArrayOutputStream byteArrayOutputStream = new java.io.ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int length;
                while ((length = inputStream.read(buffer)) != -1) {
                    byteArrayOutputStream.write(buffer, 0, length);
                }
                photoBytes = byteArrayOutputStream.toByteArray();

                // Закрываем потоки
                inputStream.close();
                byteArrayOutputStream.close();

            } catch (IOException e) {
                Toast.makeText(this, "Ошибка обработки фото", Toast.LENGTH_SHORT).show();
                return;
            }

            // Создаем временный файл из байтов (для Retrofit)
            File tempFile = new File(getCacheDir(), "temp_photo.jpg");
            try (FileOutputStream fos = new FileOutputStream(tempFile)) {
                fos.write(photoBytes);
            }

            // Подготавливаем запрос
            RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"), tempFile);
            MultipartBody.Part photoPart = MultipartBody.Part.createFormData("PhotoFile", "profile.jpg", requestFile);
            RequestBody userIdBody = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(currentUserId));

            // Отправляем запрос
            ApiService apiService = ApiClient.getApiService();
            apiService.uploadPhoto(userIdBody, photoPart).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    // Удаляем временный файл
                    if (tempFile.exists()) {
                        tempFile.delete();
                    }

                    if (response.isSuccessful()) {
                        Toast.makeText(Personalaccount.this, "Профиль и фото успешно обновлены", Toast.LENGTH_SHORT).show();
                        photoChanged = false;
                        savePhotoLocally();
                        // Обновляем профиль чтобы показать новое фото
                        loadUserProfile(currentUserId);
                    } else {
                        Log.e("PersonalAccount", "Ошибка загрузки фото: " + response.code());
                        Toast.makeText(Personalaccount.this, "Ошибка при загрузке фото: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    // Удаляем временный файл
                    if (tempFile.exists()) {
                        tempFile.delete();
                    }

                    Log.e("PersonalAccount", "Ошибка сети при загрузке фото", t);
                    Toast.makeText(Personalaccount.this, "Ошибка сети при загрузке фото: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        } catch (Exception e) {
            Log.e("PersonalAccount", "Ошибка в uploadNewPhoto", e);
            Toast.makeText(this, "Ошибка при обработке фото", Toast.LENGTH_SHORT).show();
        }
    }

    private void savePhotoLocally() {
        if (selectedImageUri != null) {
            String savedPath = copyImageToInternalStorage(selectedImageUri);
            if (savedPath != null) {
                savePhotoPath(savedPath);
            }
        }
    }



}