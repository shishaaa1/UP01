package com.example.boobleproject;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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
    private boolean currentUserGender;
    private List<Profile> allFilteredProfiles = new ArrayList<>();
    private Random random = new Random();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_mainpage);

        apiService = ApiClient.getApiService();

        // Получаем данные текущего пользователя
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        currentUserId = prefs.getInt("current_user_id", 1);
        currentUserGender = prefs.getBoolean("current_user_gender", true);

        rvProfiles = findViewById(R.id.rv_profiles);
        swipeIndicator = findViewById(R.id.iv_swipe_indicator);

        setupRecyclerView();
        setupSwipeHelper();
        loadAllUsersWithPhotos(); // Используем новый метод

        btnProfile = findViewById(R.id.btn_account);
        btnProfile.setOnClickListener(v -> {
            Intent intent = new Intent(Mainpage.this, Personalaccount.class);
            startActivity(intent);
        });
    }

    public void Back(View view) {
        Intent intent = new Intent(this, Mainpage.class);
        startActivity(intent);
    }

    private void setupRecyclerView() {
        rvProfiles.setLayoutManager(new LinearLayoutManager(this));
        profileQueue = new ArrayList<>();
        adapter = new ProfileAdapter(profileQueue);
        rvProfiles.setAdapter(adapter);

        rvProfiles.setClipToPadding(false);
        rvProfiles.setPadding(24, 100, 24, 200);

        rvProfiles.setLayoutFrozen(false);
    }

    private void loadAllUsersWithPhotos() {
        Call<List<Profile>> call = apiService.getAllUsersWithPhoto();
        call.enqueue(new Callback<List<Profile>>() {
            @Override
            public void onResponse(Call<List<Profile>> call, Response<List<Profile>> response) {
                Log.d("DEBUG_MAINPAGE", "Response code: " + response.code());
                Log.d("DEBUG_MAINPAGE", "Response isSuccessful: " + response.isSuccessful());

                if (response.isSuccessful() && response.body() != null) {
                    List<Profile> allUsers = response.body();
                    Log.d("DEBUG_MAINPAGE", "Users count: " + allUsers.size());

                    // Логируем первого пользователя
                    if (!allUsers.isEmpty()) {
                        Profile firstUser = allUsers.get(0);
                        Log.d("DEBUG_MAINPAGE", "First user - ID: " + firstUser.id);
                        Log.d("DEBUG_MAINPAGE", "First user - Name: " + firstUser.firstName + " " + firstUser.lastName);
                        Log.d("DEBUG_MAINPAGE", "First user - PhotoBytes: " +
                                (firstUser.photoBytes != null ? "exists, length: " + firstUser.photoBytes.length() : "null"));
                        Log.d("DEBUG_MAINPAGE", "First user - Gender: " + firstUser.gender);
                        Log.d("DEBUG_MAINPAGE", "First user - Bio: " + firstUser.bio);
                    }

                    // Фильтруем пользователей
                    for (Profile user : allUsers) {
                        if (user.id != currentUserId && user.gender != currentUserGender) {
                            allFilteredProfiles.add(user);
                        }
                    }

                    if (allFilteredProfiles.isEmpty()) {
                        Toast.makeText(Mainpage.this, "Нет подходящих пользователей", Toast.LENGTH_LONG).show();
                        return;
                    }

                    Collections.shuffle(allFilteredProfiles);
                    addNextProfilesToQueue(5);

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

    private void addNextProfilesToQueue(int count) {
        int profilesToAdd = Math.min(count, allFilteredProfiles.size());

        List<Profile> newProfiles = new ArrayList<>();
        for (int i = 0; i < profilesToAdd; i++) {
            if (!allFilteredProfiles.isEmpty()) {
                Profile profile = allFilteredProfiles.remove(0);
                newProfiles.add(profile);
            }
        }

        // Используем метод адаптера для добавления
        adapter.addProfiles(newProfiles);

        if (profileQueue.isEmpty()) {
            Toast.makeText(this, "Больше нет пользователей", Toast.LENGTH_SHORT).show();
        }
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