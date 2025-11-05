package com.example.boobleproject;

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

import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
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

    private CircleImageView profilePhoto;
    private ImageButton bthAddPhoto;
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

        firstNameTextView = findViewById(R.id.et_first_name);
        lastNameTextView = findViewById(R.id.et_last_name);
        bioTextView = findViewById(R.id.et_bio);

        bthAddPhoto.setOnClickListener(v -> openGallery());
        loadSavedPhoto();

        // Загружаем userId из SharedPreferences
        SharedPreferences userPrefs = getSharedPreferences("userPrefs", MODE_PRIVATE);
        int userId = userPrefs.getInt("userId", -1);

        if (userId != -1) {
            loadUserProfile(userId);
            //loadUserPhoto(userId); // <-- вот эта строка
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

    private void loadUserPhoto(int userId) {
        ApiService apiService = ApiClient.getApiService();
        apiService.getPhotoByUser(userId).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Получаем байты из ответа
                    byte[] photoBytes;
                    try {
                        photoBytes = response.body().bytes();
                    } catch (IOException e) {
                        Toast.makeText(Personalaccount.this, "Ошибка чтения фото", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Преобразуем в Bitmap
                    Bitmap bitmap = BitmapFactory.decodeByteArray(photoBytes, 0, photoBytes.length);
                    if (bitmap != null) {
                        profilePhoto.setImageBitmap(bitmap);
                    } else {
                        Toast.makeText(Personalaccount.this, "Ошибка отображения фото", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Если фото нет — можешь поставить дефолтную картинку
                    profilePhoto.setImageResource(R.drawable.alt1);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(Personalaccount.this, "Ошибка загрузки фото: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }//

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
                    } else {
                        loadSavedPhoto();
                    }
                }
            }
            private void savePhotoToInternalStorage(Bitmap bitmap) {
                try {
                    File file = new File(getFilesDir(), "profile_photo.jpg");
                    FileOutputStream fos = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                    fos.close();

                    savePhotoPath(file.getAbsolutePath());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(Call<Profile> call, Throwable t) {
                Log.e("PersonalAccount", "Ошибка при загрузке профиля", t);
                Toast.makeText(Personalaccount.this, "Ошибка сети: " + t.getMessage(), Toast.LENGTH_SHORT).show();
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

    }


}