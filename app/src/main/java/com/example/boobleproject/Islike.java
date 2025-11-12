package com.example.boobleproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Islike extends AppCompatActivity implements MutualLikeAdapter.OnProfileClickListener {
    private RecyclerView rvMutualLikes;
    private MutualLikeAdapter adapter;
    private ApiService apiService;
    private int currentUserId;
    private List<Profile> mutualProfiles = new ArrayList<>();
    private TextView tvEmptyState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_islike);

        apiService = ApiClient.getApiService();

        // Получаем ID текущего пользователя
        SharedPreferences userPrefs = getSharedPreferences("userPrefs", MODE_PRIVATE);
        currentUserId = userPrefs.getInt("userId", -1);

        if (currentUserId == -1) {
            Toast.makeText(this, "Ошибка авторизации", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Log.d("MUTUAL_DEBUG", "Текущий пользователь ID: " + currentUserId);

        initViews();
        setupRecyclerView();
        loadMutualMatches();

        // Кнопка назад
        ImageButton btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> finish());
    }

    private void initViews() {
        rvMutualLikes = findViewById(R.id.rv_likes);
        tvEmptyState = findViewById(R.id.tv_empty_state);
    }

    private void setupRecyclerView() {
        rvMutualLikes.setLayoutManager(new GridLayoutManager(this, 2));
        mutualProfiles = new ArrayList<>();

        // ПЕРЕДАЕМ ДВА ПАРАМЕТРА: список профилей И listener
        adapter = new MutualLikeAdapter(mutualProfiles, this);
        rvMutualLikes.setAdapter(adapter);

        rvMutualLikes.setClipToPadding(false);
        rvMutualLikes.setPadding(24, 100, 24, 200);
    }

    // Реализация метода интерфейса - открытие чата при клике
    @Override
    public void onMessageClick(Profile profile) {
        Log.d("MUTUAL_CLICK", "Открыть чат с пользователем: " + profile.getFullName() + ", ID: " + profile.id);

        // Передаем только ID получателя
        Intent intent = new Intent(this, Messages.class);
        intent.putExtra("RECIPIENT_ID", profile.id);
        Log.d("ISLIKE_DEBUG", "Передаем в Intent RECIPIENT_ID: " + profile.id);
        startActivity(intent);
    }

    // Остальные методы без изменений
    private void loadMutualMatches() {
        Call<Map<String, Object>> call = apiService.getUserMatches(currentUserId);

        call.enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                Log.d("MUTUAL_DEBUG", "Response code: " + response.code());
                Log.d("MUTUAL_DEBUG", "Response isSuccessful: " + response.isSuccessful());

                if (response.isSuccessful() && response.body() != null) {
                    Map<String, Object> result = response.body();

                    // Логируем весь ответ для отладки
                    Log.d("MUTUAL_DEBUG", "Полный ответ: " + result.toString());

                    if (result.containsKey("success") && (Boolean) result.get("success")) {
                        if (result.containsKey("matches")) {
                            Object matchesObj = result.get("matches");
                            if (matchesObj instanceof List) {
                                List<Map<String, Object>> matches = (List<Map<String, Object>>) matchesObj;
                                Log.d("MUTUAL_DEBUG", "Получено взаимных лайков: " + matches.size());

                                if (!matches.isEmpty()) {
                                    processMutualMatches(matches);
                                } else {
                                    showEmptyState();
                                }
                            } else {
                                Log.d("MUTUAL_DEBUG", "matches не является списком");
                                showEmptyState();
                            }
                        } else {
                            Log.d("MUTUAL_DEBUG", "Ключ matches отсутствует в ответе");
                            showEmptyState();
                        }
                    } else {
                        Log.d("MUTUAL_DEBUG", "success = false или ключ отсутствует");
                        showEmptyState();
                    }
                } else {
                    showEmptyState();
                    Log.d("MUTUAL_DEBUG", "Response error body: " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                Log.e("MUTUAL_DEBUG", "Network error: " + t.getMessage());
                Toast.makeText(Islike.this, "Ошибка сети: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                showEmptyState();
            }
        });
    }

    private void processMutualMatches(List<Map<String, Object>> matches) {
        mutualProfiles.clear();

        // Сначала создаем список профилей из matches
        for (Map<String, Object> match : matches) {
            // Безопасное получение UserId
            Object userIdObj = match.get("userId");
            if (userIdObj == null) {
                Log.e("MUTUAL_DEBUG", "UserId is null, пропускаем запись");
                continue;
            }

            int userId;
            try {
                if (userIdObj instanceof Number) {
                    userId = ((Number) userIdObj).intValue();
                } else if (userIdObj instanceof String) {
                    userId = Integer.parseInt((String) userIdObj);
                } else {
                    Log.e("MUTUAL_DEBUG", "Неизвестный тип UserId: " + userIdObj.getClass().getSimpleName());
                    continue;
                }

                Log.d("MUTUAL_DEBUG", "Загружаем профиль ID: " + userId);

                // Загружаем полный профиль пользователя по ID
                loadUserProfile(userId);

            } catch (Exception e) {
                Log.e("MUTUAL_DEBUG", "Ошибка парсинга UserId: " + e.getMessage());
            }
        }

        // Если matches пустой, показываем empty state
        if (matches.isEmpty()) {
            showEmptyState();
        }
    }

    private void loadUserProfile(int userId) {
        Call<Profile> call = apiService.getUserById(userId);
        call.enqueue(new Callback<Profile>() {
            @Override
            public void onResponse(Call<Profile> call, Response<Profile> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Profile profile = response.body();
                    Log.d("MUTUAL_DEBUG", "Загружен профиль: " + profile.getFullName() + ", ID: " + profile.id);

                    // Загружаем фото для профиля
                    loadUserPhoto(profile);
                } else {
                    Log.d("MUTUAL_DEBUG", "Не удалось загрузить профиль ID: " + userId + ", код: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Profile> call, Throwable t) {
                Log.e("MUTUAL_DEBUG", "Ошибка загрузки профиля ID: " + userId + ": " + t.getMessage());
            }
        });
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

                        Log.d("MUTUAL_PHOTO", "Фото загружено для пользователя ID: " + user.id + ", размер: " + base64Photo.length());

                    } catch (Exception e) {
                        Log.e("MUTUAL_PHOTO", "Ошибка конвертации фото для пользователя ID: " + user.id + ": " + e.getMessage());
                    }
                } else {
                    Log.d("MUTUAL_PHOTO", "Фото не найдено для пользователя ID: " + user.id + ", код: " + response.code());
                }

                // Добавляем профиль в адаптер (с фото или без)
                addProfileToAdapter(user);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("MUTUAL_PHOTO", "Ошибка загрузки фото для пользователя ID: " + user.id + ": " + t.getMessage());
                addProfileToAdapter(user);
            }
        });
    }

    private void addProfileToAdapter(Profile profile) {
        // Проверяем, нет ли уже такого профиля в списке
        for (Profile existingProfile : mutualProfiles) {
            if (existingProfile.id == profile.id) {
                return;
            }
        }

        mutualProfiles.add(profile);
        adapter.setMutualProfiles(mutualProfiles);

        if (mutualProfiles.isEmpty()) {
            showEmptyState();
        } else {
            tvEmptyState.setVisibility(View.GONE);
            rvMutualLikes.setVisibility(View.VISIBLE);
        }
    }

    private void showEmptyState() {
        tvEmptyState.setVisibility(View.VISIBLE);
        rvMutualLikes.setVisibility(View.GONE);
    }
}