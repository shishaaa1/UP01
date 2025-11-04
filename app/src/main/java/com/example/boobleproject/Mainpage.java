package com.example.boobleproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Mainpage extends AppCompatActivity {

    private RecyclerView rvProfiles;
    private ProfileAdapter adapter;
    private List<Profile> profileQueue;
    private ImageView swipeIndicator;
    ImageButton btnProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_mainpage); // ✅ Важно, что идёт первым

        rvProfiles = findViewById(R.id.rv_profiles);
        swipeIndicator = findViewById(R.id.iv_swipe_indicator);

        setupRecyclerView();   // ✅ Создаём и прикрепляем адаптер
        setupSwipeHelper();    // ✅ Подключаем свайпы
        loadInitialProfiles(); // ✅ Загружаем данные


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

        // Блокируем скролл чтобы нельзя было листать карточки
        rvProfiles.setLayoutFrozen(false);
    }

    private void loadInitialProfiles() {
        List<Profile> initial = Arrays.asList(
                new Profile(0,"Бородин","Илья",createDate(2004,11,19),true,"Красивый мужчина, которого можно увидеть на свете",R.drawable.alt1),
                new Profile(1,"Канюков","Анатолтй",createDate(1989,11,19),false,"Красивый мужчина, которого можно увидеть на свете adfagsdfgsdgsdfgsdfgsdfgsdfgsdfgsdfgsfdgsdfgsdfgsdfg adfagsdfgsdgsdfgsdfgsdfgsdfgsdfgsdfgsfdgsdfgsdfgsdfg adfagsdfgsdgsdfgsdfgsdfgsdfgsdfgsdfgsfdgsdfgsdfgsdfg adfagsdfgsdgsdfgsdfgsdfgsdfgsdfgsdfgsfdgsdfgsdfgsdfg adfagsdfgsdgsdfgsdfgsdfgsdfgsdfgsdfgsfdgsdfgsdfgsdfg",R.drawable.alt2),
        new Profile(2,"Гашев","Данил",createDate(2002,06,14),true,"Красивый мужчина, которого можно увидеть на свете",R.drawable.alt3)
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
                () -> {

                    Toast.makeText(this, "Не нравится", Toast.LENGTH_SHORT).show();
                },
                () -> {

                    Toast.makeText(this, "Лайк!", Toast.LENGTH_SHORT).show();
                }
        );

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeHelper);
        itemTouchHelper.attachToRecyclerView(rvProfiles);
    }

    private Date createDate(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, day);
        return calendar.getTime();
    }
}