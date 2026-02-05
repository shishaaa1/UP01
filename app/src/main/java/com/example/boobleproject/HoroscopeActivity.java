package com.example.boobleproject;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.boobleproject.Api.ApiClient;
import com.example.boobleproject.Api.ApiService;
import com.example.boobleproject.R;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HoroscopeActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private HoroscopeAdapter adapter;
    private ApiService apiService;
    private ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_horoskoup);

        apiService = ApiClient.getApiService();

        recyclerView = findViewById(R.id.rv_achievements);
        btnBack = findViewById(R.id.btn_back);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new HoroscopeAdapter();
        recyclerView.setAdapter(adapter);

        btnBack.setOnClickListener(v -> finish());

        loadHoroscope();
    }

    private void loadHoroscope() {

        apiService.getTodayHoroscope().enqueue(new Callback<HoroscopeResponse>() {
            @Override
            public void onResponse(Call<HoroscopeResponse> call, Response<HoroscopeResponse> response) {

                if (response.isSuccessful() && response.body() != null) {

                    HoroscopeResponse horoscope = response.body();

                    adapter.setData(new ArrayList<>(horoscope.signs.values()));

                } else {
                    Toast.makeText(HoroscopeActivity.this, "Ошибка загрузки гороскопа", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<HoroscopeResponse> call, Throwable t) {
                Toast.makeText(HoroscopeActivity.this, "Ошибка сети", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
