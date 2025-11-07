package com.example.boobleproject;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;



public class Mainpage extends AppCompatActivity {

    private RecyclerView rvProfiles;
    private ProfileAdapter adapter;
    private List<Profile> profileQueue;
    private ImageView swipeIndicator;
    private ImageButton btnProfile;
    private ApiService apiService;
    private int currentUserId;
    private List<Profile> allFilteredProfiles = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_mainpage);

        apiService = ApiClient.getApiService();

        // Получаем ID текущего пользователя
        SharedPreferences userPrefs = getSharedPreferences("userPrefs", MODE_PRIVATE);
        currentUserId = userPrefs.getInt("userId", -1); // Сохраняем в поле класса

        if (currentUserId == -1) {
            Toast.makeText(this, "Ошибка авторизации", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Log.d("MAINPAGE_DEBUG", "Текущий пользователь ID: " + currentUserId);

        rvProfiles = findViewById(R.id.rv_profiles);
        swipeIndicator = findViewById(R.id.iv_swipe_indicator);

        setupRecyclerView();
        setupSwipeHelper();
        loadOppositeSexUsers();

        btnProfile = findViewById(R.id.btn_account);
        btnProfile.setOnClickListener(v -> {
            Intent intent = new Intent(Mainpage.this, Personalaccount.class);
            startActivity(intent);
        });
    }

    private void loadOppositeSexUsers() {
        Call<List<Profile>> call = apiService.getOppositeSexUsers(currentUserId);
        call.enqueue(new Callback<List<Profile>>() {
            @Override
            public void onResponse(Call<List<Profile>> call, Response<List<Profile>> response) {
                logResponseBody(response);
                Log.d("DEBUG_MAINPAGE", "Response code: " + response.code());
                Log.d("DEBUG_MAINPAGE", "Response isSuccessful: " + response.isSuccessful());

                if (response.isSuccessful() && response.body() != null) {
                    List<Profile> oppositeSexUsers = response.body();
                    Log.d("DEBUG_MAINPAGE", "Получено пользователей противоположного пола: " + oppositeSexUsers.size());
                    Log.d("GENDER_DEBUG", "=== ОТЛАДКА ПОЛА ===");
                    for (Profile user : oppositeSexUsers) {
                        Log.d("GENDER_DEBUG",
                                "ID: " + user.id +
                                        " | Имя: " + user.firstName +
                                        " | Sex: " + user.sex +
                                        " | getGenderAsString(): " + user.getGenderAsString());
                    }
                    // Логируем первых нескольких пользователей для проверки
                    for (int i = 0; i < Math.min(3, oppositeSexUsers.size()); i++) {
                        Profile user = oppositeSexUsers.get(i);

                    }

                    if (oppositeSexUsers.isEmpty()) {
                        Toast.makeText(Mainpage.this, "Нет подходящих пользователей", Toast.LENGTH_LONG).show();
                        return;
                    }

                    allFilteredProfiles.clear();
                    allFilteredProfiles.addAll(oppositeSexUsers);

                    // Загружаем фото для пользователей у которых их нет
                    loadMissingPhotos();

                } else {
                    Log.d("DEBUG_MAINPAGE", "Response error body: " + response.errorBody());
                    Toast.makeText(Mainpage.this, "Ошибка загрузки пользователей: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Profile>> call, Throwable t) {
                Log.e("DEBUG_MAINPAGE", "Network error: " + t.getMessage());
                Toast.makeText(Mainpage.this, "Ошибка сети: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadMissingPhotos() {
        for (Profile user : allFilteredProfiles) {
            if (user.photoBytes == null || user.photoBytes.isEmpty()) {
                loadUserPhoto(user);
            }
        }

        // После загрузки пользователей добавляем их в очередь
        Collections.shuffle(allFilteredProfiles);
        addNextProfilesToQueue(5);
    }
    private void logResponseBody(Response<List<Profile>> response) {
        try {
            // Получаем сырой JSON ответ
            String rawJson = response.raw().body().string();
            Log.d("RAW_JSON_DEBUG", "=== СЫРОЙ JSON ОТВЕТ ===");
            Log.d("RAW_JSON_DEBUG", rawJson);
            Log.d("RAW_JSON_DEBUG", "=== КОНЕЦ JSON ===");
        } catch (Exception e) {
            Log.e("RAW_JSON_DEBUG", "Ошибка чтения response body: " + e.getMessage());
        }
    }
    private void loadUserPhoto(Profile user) {
        Call<ResponseBody> call = apiService.getPhotoByUserId(user.id);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        // Конвертируем ResponseBody в byte array
                        byte[] photoBytes = response.body().bytes();

                        // Конвертируем byte array в base64 строку
                        String base64Photo = Base64.encodeToString(photoBytes, Base64.DEFAULT);
                        user.photoBytes = base64Photo;

                        Log.d("PHOTO_DEBUG", "Фото загружено для пользователя ID: " + user.id + ", размер: " + base64Photo.length());

                        // Обновляем адаптер если этот пользователь сейчас отображается
                        updateAdapterIfNeeded(user);

                    } catch (Exception e) {
                        Log.e("PHOTO_DEBUG", "Ошибка конвертации фото для пользователя ID: " + user.id + ": " + e.getMessage());
                    }
                } else {
                    Log.d("PHOTO_DEBUG", "Фото не найдено для пользователя ID: " + user.id + ", код: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("PHOTO_DEBUG", "Ошибка загрузки фото для пользователя ID: " + user.id + ": " + t.getMessage());
            }
        });
    }

    private void updateAdapterIfNeeded(Profile updatedUser) {
        // Проверяем, находится ли обновленный пользователь в текущей очереди отображения
        for (int i = 0; i < profileQueue.size(); i++) {
            if (profileQueue.get(i).id == updatedUser.id) {
                adapter.notifyItemChanged(i);
                break;
            }
        }
    }

    private void addNextProfilesToQueue(int count) {
        int profilesToAdd = Math.min(count, allFilteredProfiles.size());

        List<Profile> newProfiles = new ArrayList<>();
        for (int i = 0; i < profilesToAdd; i++) {
            if (!allFilteredProfiles.isEmpty()) {
                Profile profile = allFilteredProfiles.remove(0);
                newProfiles.add(profile);
            }
        }

        adapter.addProfiles(newProfiles);

        if (profileQueue.isEmpty() && allFilteredProfiles.isEmpty()) {
            Toast.makeText(this, "Больше нет пользователей", Toast.LENGTH_SHORT).show();
        }
    }

    // Остальные методы без изменений
    private void setupRecyclerView() {
        rvProfiles.setLayoutManager(new LinearLayoutManager(this));
        profileQueue = new ArrayList<>();
        adapter = new ProfileAdapter(profileQueue);
        rvProfiles.setAdapter(adapter);

        rvProfiles.setClipToPadding(false);
        rvProfiles.setPadding(24, 100, 24, 200);
        rvProfiles.setLayoutFrozen(false);
    }

    private void setupSwipeHelper() {
        SwipeHelper swipeHelper = new SwipeHelper(
                adapter,
                rvProfiles,
                swipeIndicator,
                () -> {
                    Toast.makeText(this, "Не нравится", Toast.LENGTH_SHORT).show();
                    removeTopCardAndCheckQueue();
                },
                () -> {
                    Toast.makeText(this, "Лайк!", Toast.LENGTH_SHORT).show();
                    removeTopCardAndCheckQueue();
                }
        );

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeHelper);
        itemTouchHelper.attachToRecyclerView(rvProfiles);
    }

    private void removeTopCardAndCheckQueue() {
        if (!profileQueue.isEmpty()) {
            profileQueue.remove(0);
            adapter.notifyItemRemoved(0);

            if (profileQueue.size() <= 2 && !allFilteredProfiles.isEmpty()) {
                addNextProfilesToQueue(3);
            }

            if (!profileQueue.isEmpty()) {
                adapter.notifyItemChanged(0);
            }
        }

        if (profileQueue.isEmpty() && !allFilteredProfiles.isEmpty()) {
            addNextProfilesToQueue(5);
        }
    }

    public void loadMoreProfiles(View view) {
        if (!allFilteredProfiles.isEmpty()) {
            addNextProfilesToQueue(3);
            Toast.makeText(this, "Загружаем новые профили...", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Больше нет пользователей", Toast.LENGTH_SHORT).show();
        }
    }
}