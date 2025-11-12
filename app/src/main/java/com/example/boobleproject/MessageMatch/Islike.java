package com.example.boobleproject.MessageMatch;

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

import com.example.boobleproject.Api.ApiClient;
import com.example.boobleproject.Api.ApiService;
import com.example.boobleproject.Profile;
import com.example.boobleproject.R;

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

        adapter = new MutualLikeAdapter(mutualProfiles, this);
        rvMutualLikes.setAdapter(adapter);

        rvMutualLikes.setClipToPadding(false);
        rvMutualLikes.setPadding(24, 100, 24, 200);
    }

    @Override
    public void onMessageClick(Profile profile) {

        Intent intent = new Intent(this, Messages.class);
        intent.putExtra("RECIPIENT_ID", profile.id);

        startActivity(intent);
    }

    private void loadMutualMatches() {
        Call<Map<String, Object>> call = apiService.getUserMatches(currentUserId);

        call.enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                Log.d("MUTUAL_DEBUG", "Response code: " + response.code());
                Log.d("MUTUAL_DEBUG", "Response isSuccessful: " + response.isSuccessful());

                if (response.isSuccessful() && response.body() != null) {
                    Map<String, Object> result = response.body();


                    if (result.containsKey("success") && (Boolean) result.get("success")) {
                        if (result.containsKey("matches")) {
                            Object matchesObj = result.get("matches");
                            if (matchesObj instanceof List) {
                                List<Map<String, Object>> matches = (List<Map<String, Object>>) matchesObj;


                                if (!matches.isEmpty()) {
                                    processMutualMatches(matches);
                                } else {
                                    showEmptyState();
                                }
                            } else {

                                showEmptyState();
                            }
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

                Toast.makeText(Islike.this, "Ошибка сети: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                showEmptyState();
            }
        });
    }

    private void processMutualMatches(List<Map<String, Object>> matches) {
        mutualProfiles.clear();

        for (Map<String, Object> match : matches) {

            Object userIdObj = match.get("userId");
            if (userIdObj == null) {

                continue;
            }

            int userId;
            try {
                if (userIdObj instanceof Number) {
                    userId = ((Number) userIdObj).intValue();
                } else if (userIdObj instanceof String) {
                    userId = Integer.parseInt((String) userIdObj);
                } else {

                    continue;
                }

                loadUserProfile(userId);

            } catch (Exception e) {

            }
        }

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

                    loadUserPhoto(profile);
                } else {

                }
            }

            @Override
            public void onFailure(Call<Profile> call, Throwable t) {

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

                        byte[] photoBytes = response.body().bytes();


                        String base64Photo = Base64.encodeToString(photoBytes, Base64.DEFAULT);
                        user.photoBytes = base64Photo;



                    } catch (Exception e) {

                    }
                }

                addProfileToAdapter(user);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

                addProfileToAdapter(user);
            }
        });
    }

    private void addProfileToAdapter(Profile profile) {
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