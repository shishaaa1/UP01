package com.example.boobleproject;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Authorization extends AppCompatActivity {

    private RecyclerView rvProfiles;
    private ProfileAdapter adapter;
    private List<Profile> profileQueue;
    private ImageView swipeIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_authorization); // ✅ Важно, что идёт первым

        rvProfiles = findViewById(R.id.rv_profiles);
        swipeIndicator = findViewById(R.id.iv_swipe_indicator);

        setupRecyclerView();   // ✅ Создаём и прикрепляем адаптер
        setupSwipeHelper();    // ✅ Подключаем свайпы
        loadInitialProfiles(); // ✅ Загружаем данные
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

    private void loadInitialProfiles() {
        List<Profile> initial = Arrays.asList(
                new Profile(1, "Анна Смирнова", 25, "Москва", R.drawable.alt1),
                new Profile(2, "Мария Иванова", 28, "СПб", R.drawable.alt2),
                new Profile(3, "Екатерина Петрова", 23, "Казань", R.drawable.alt3)
        );

        if (adapter == null) {
            throw new RuntimeException("Adapter is null! Проверь setupRecyclerView");
        }

        adapter.addProfiles(initial);
    }

    private void setupSwipeHelper() {
        SwipeHelper swipeHelper = new SwipeHelper(
                adapter,
                rvProfiles,
                swipeIndicator,
                () -> Toast.makeText(this, "Не нравится", Toast.LENGTH_SHORT).show(),
                () -> Toast.makeText(this, "Лайк!", Toast.LENGTH_SHORT).show()
        );

        new ItemTouchHelper(swipeHelper).attachToRecyclerView(rvProfiles);
    }
}