package com.example.boobleproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import de.hdodenhof.circleimageview.CircleImageView;

public class Personalaccount extends AppCompatActivity {

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

        loadSavedPhoto();

        bthAddPhoto.setOnClickListener(v -> openGallery());
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