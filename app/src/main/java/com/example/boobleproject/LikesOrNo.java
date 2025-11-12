package com.example.boobleproject;

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

public class LikesOrNo extends AppCompatActivity {

    private static final String TAG = "LikesOrNo";

    private RecyclerView rvLikes;
    private TextView tvEmptyState;
    private LikeOrNotAdapter adapter;
    private ApiService apiService;
    private int currentUserId;
    private List<Profile> profileList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_likes_or_no);

        apiService = ApiClient.getApiService();

        SharedPreferences prefs = getSharedPreferences("userPrefs", MODE_PRIVATE);
        currentUserId = prefs.getInt("userId", -1);
        Log.d(TAG, "Current userId: " + currentUserId);

        if (currentUserId == -1) {
            Toast.makeText(this, "Ошибка авторизации", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        rvLikes = findViewById(R.id.rv_likes);
        tvEmptyState = findViewById(R.id.tv_empty_state);

        adapter = new LikeOrNotAdapter(this, currentUserId);
        rvLikes.setLayoutManager(new GridLayoutManager(this, 2));
        rvLikes.setAdapter(adapter);

        ImageButton btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> finish());

        loadLikes();
    }

    private void loadLikes() {
        Log.d(TAG, "loadLikes called");

        // Сначала загружаем взаимные лайки
        apiService.getUserMatches(currentUserId).enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Map<String, Object> result = response.body();
                    List<Integer> mutualIds = new ArrayList<>();

                    if (Boolean.TRUE.equals(result.get("success")) && result.containsKey("matches")) {
                        Object matchesObj = result.get("matches");
                        if (matchesObj instanceof List) {
                            List<Map<String, Object>> matches = (List<Map<String, Object>>) matchesObj;
                            for (Map<String, Object> match : matches) {
                                Object idObj = match.get("userId");
                                if (idObj != null) {
                                    try {
                                        int id = (idObj instanceof Number)
                                                ? ((Number) idObj).intValue()
                                                : Integer.parseInt(idObj.toString());
                                        mutualIds.add(id);
                                    } catch (Exception e) {
                                        Log.e(TAG, "Ошибка парсинга userId из matches: " + e.getMessage());
                                    }
                                }
                            }
                        }
                    }

                    Log.d(TAG, "Взаимных лайков найдено: " + mutualIds.size());
                    loadLikesFiltered(mutualIds);
                } else {
                    Log.w(TAG, "Не удалось получить взаимные лайки");
                    loadLikesFiltered(new ArrayList<>()); // Без фильтра, если ошибка
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                Log.e(TAG, "Ошибка загрузки взаимных лайков: " + t.getMessage());
                loadLikesFiltered(new ArrayList<>()); // Без фильтра, если ошибка
            }
        });
    }

    private void loadLikesFiltered(List<Integer> mutualIds) {
        apiService.getUserLikes(currentUserId).enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Map<String, Object> result = response.body();

                    if (Boolean.TRUE.equals(result.get("success")) && result.containsKey("likes")) {
                        Object likesObj = result.get("likes");

                        if (likesObj instanceof List) {
                            List<Map<String, Object>> likesMaps = (List<Map<String, Object>>) likesObj;
                            Log.d(TAG, "Likes list size: " + likesMaps.size());
                            profileList.clear();

                            for (Map<String, Object> likeMap : likesMaps) {
                                Object idObj = likeMap.get("fromUserid");
                                if (idObj == null) continue;

                                int userId;
                                try {
                                    userId = (idObj instanceof Number)
                                            ? ((Number) idObj).intValue()
                                            : Integer.parseInt(idObj.toString());
                                } catch (Exception e) {
                                    continue;
                                }

                                // 🔥 Фильтруем: пропускаем, если уже есть взаимный лайк
                                if (mutualIds.contains(userId)) {
                                    Log.d(TAG, "Пропускаем userId " + userId + " (уже взаимный лайк)");
                                    continue;
                                }

                                loadUserProfile(userId);
                            }

                            if (profileList.isEmpty()) showEmptyState();
                        } else {
                            showEmptyState();
                        }
                    } else {
                        showEmptyState();
                    }
                } else {
                    showEmptyState();
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                Log.e(TAG, "Ошибка загрузки лайков: " + t.getMessage());
                showEmptyState();
            }
        });
    }

    private void loadUserProfile(int userId) {
        Log.d(TAG, "loadUserProfile called for userId: " + userId);
        apiService.getUserById(userId).enqueue(new Callback<Profile>() {
            @Override
            public void onResponse(Call<Profile> call, Response<Profile> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Profile profile = response.body();
                    Log.d(TAG, "Profile loaded: " + profile.getFullName() + ", id: " + profile.id);
                    loadUserPhoto(profile);
                } else {
                    Log.e(TAG, "Failed to load profile, code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Profile> call, Throwable t) {
                Log.e(TAG, "Failed to load profile for userId " + userId + ", error: " + t.getMessage());
            }
        });
    }

    private void loadUserPhoto(Profile profile) {
        Log.d(TAG, "loadUserPhoto called for userId: " + profile.id);
        apiService.getPhotoByUserId(profile.id).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        byte[] photoBytes = response.body().bytes();
                        profile.photoBytes = Base64.encodeToString(photoBytes, Base64.DEFAULT);
                        Log.d(TAG, "Photo loaded for userId " + profile.id + ", size: " + profile.photoBytes.length());
                    } catch (Exception e) {
                        Log.e(TAG, "Error converting photo for userId " + profile.id + ": " + e.getMessage());
                    }
                } else {
                    Log.d(TAG, "No photo found for userId " + profile.id + ", code: " + response.code());
                }
                addProfileToAdapter(profile);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(TAG, "Failed to load photo for userId " + profile.id + ": " + t.getMessage());
                addProfileToAdapter(profile);
            }
        });
    }

    private void addProfileToAdapter(Profile profile) {
        Log.d(TAG, "addProfileToAdapter called for userId: " + profile.id);
        for (Profile p : profileList) {
            if (p.id == profile.id) {
                Log.d(TAG, "Profile already in list, skipping userId: " + profile.id);
                return;
            }
        }
        profileList.add(profile);
        Log.d(TAG, "Profile added to list: " + profile.getFullName() + ", total profiles: " + profileList.size());
        adapter.setProfiles(profileList);

        if (profileList.isEmpty()) {
            Log.d(TAG, "Profile list empty after adding, showing empty state");
            showEmptyState();
        } else {
            tvEmptyState.setVisibility(View.GONE);
            rvLikes.setVisibility(View.VISIBLE);
        }
    }

    private void showEmptyState() {
        Log.d(TAG, "showEmptyState called");
        tvEmptyState.setVisibility(View.VISIBLE);
        rvLikes.setVisibility(View.GONE);
    }
}
