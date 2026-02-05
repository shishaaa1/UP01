package com.example.boobleproject;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.boobleproject.Account.Personalaccount;
import com.example.boobleproject.Api.ApiClient;
import com.example.boobleproject.Api.ApiService;

import java.util.ArrayList;
import java.util.List;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.boobleproject.Api.ApiClient;
import com.example.boobleproject.Api.ApiService;
import com.example.boobleproject.Profile;
import com.example.boobleproject.R;
import com.google.android.material.switchmaterial.SwitchMaterial;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AchievementActivity extends AppCompatActivity {


    private RecyclerView rvAchievements;
    private AchievementAdapter adapter;
    private List<Achievement> achievementList = new ArrayList<>();
    private ImageButton btnBack;
    private ApiService apiService;
    private int userId;

    private CountStatsResponse stats;
    private AchievementsResponse achievements;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gaimind);

        apiService = ApiClient.getApiService();
        btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(AchievementActivity.this, Personalaccount.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });
        SharedPreferences prefs = getSharedPreferences("userPrefs", MODE_PRIVATE);
        userId = prefs.getInt("userId", -1);

        if (userId == -1) {
            Toast.makeText(this, "Ошибка пользователя", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        loadCountStats();
    }

    private void initViews() {
        rvAchievements = findViewById(R.id.rv_achievements);
        rvAchievements.setLayoutManager(new LinearLayoutManager(this));

        adapter = new AchievementAdapter(achievementList);
        rvAchievements.setAdapter(adapter);
    }

    // ---------------------------
    // ЗАГРУЗКА ДАННЫХ
    // ---------------------------

    private void loadCountStats() {
        apiService.getCountStats(userId).enqueue(new Callback<CountStatsResponse>() {
            @Override
            public void onResponse(Call<CountStatsResponse> call, Response<CountStatsResponse> response) {
                if (!response.isSuccessful() || response.body() == null) return;
                stats = response.body();

                loadAchievements(); // после stats загружаем completedTasks
            }

            @Override
            public void onFailure(Call<CountStatsResponse> call, Throwable t) {
                Log.e("ACH", "Ошибка stats: " + t.getMessage());
                Toast.makeText(AchievementActivity.this, "Ошибка загрузки статистики", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadAchievements() {
        apiService.getAchievements(userId).enqueue(new Callback<AchievementsResponse>() {
            @Override
            public void onResponse(Call<AchievementsResponse> call, Response<AchievementsResponse> response) {
                if (!response.isSuccessful() || response.body() == null) return;
                achievements = response.body();

                achievementList.clear();
                achievementList.addAll(AchievementMapper.fromApi(achievements.completedTasks, stats));

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<AchievementsResponse> call, Throwable t) {
                Log.e("ACH", "Ошибка achievements: " + t.getMessage());
                Toast.makeText(AchievementActivity.this, "Ошибка загрузки достижений", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
