package com.example.boobleproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Messages extends AppCompatActivity {

    private RecyclerView rvMessages;
    private EditText etMessageInput;
    private ImageButton btnSend, btnBack;
    private CircleImageView ivUserAvatar;
    private TextView tvUserName;

    private MessageAdapter messageAdapter;
    private List<Message> messageList = new ArrayList<>();

    private ApiService apiService;
    private int currentUserId;
    private int recipientId;
    private Profile recipientProfile;
    private Profile currentUserProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_messages);

        apiService = ApiClient.getApiService();

        // Получаем ID текущего пользователя
        SharedPreferences userPrefs = getSharedPreferences("userPrefs", MODE_PRIVATE);
        currentUserId = userPrefs.getInt("userId", -1);

        // Получаем ID получателя из Intent
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("RECIPIENT_ID")) {
            recipientId = intent.getIntExtra("RECIPIENT_ID", -1);
        }

        if (currentUserId == -1 || recipientId == -1) {
            Toast.makeText(this, "Ошибка данных пользователя", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        setupRecyclerView();
        loadUserProfiles();
        setupClickListeners();
    }

    private void initViews() {
        rvMessages = findViewById(R.id.rv_messages);
        etMessageInput = findViewById(R.id.et_message_input);
        btnSend = findViewById(R.id.btn_send);
        btnBack = findViewById(R.id.btn_back);
        ivUserAvatar = findViewById(R.id.iv_user_avatar);
        tvUserName = findViewById(R.id.tv_user_name);
    }

    private void setupRecyclerView() {
        // Временно создаем адаптер без профилей
        messageAdapter = new MessageAdapter(messageList, currentUserId, null, null);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);

        rvMessages.setLayoutManager(layoutManager);
        rvMessages.setAdapter(messageAdapter);
    }

    private void loadUserProfiles() {
        // Сначала загружаем профиль текущего пользователя
        loadCurrentUserProfile();
    }

    private void loadCurrentUserProfile() {
        Call<Profile> call = apiService.getUserById(currentUserId);
        call.enqueue(new Callback<Profile>() {
            @Override
            public void onResponse(Call<Profile> call, Response<Profile> response) {
                if (response.isSuccessful() && response.body() != null) {
                    currentUserProfile = response.body();
                    Log.d("MESSAGES", "Загружен профиль текущего пользователя: " + currentUserProfile.getFullName());

                    // После загрузки текущего пользователя загружаем получателя
                    loadRecipientProfile();
                } else {
                    Log.e("MESSAGES", "Ошибка загрузки профиля текущего пользователя");
                    // Все равно пытаемся загрузить получателя
                    loadRecipientProfile();
                }
            }

            @Override
            public void onFailure(Call<Profile> call, Throwable t) {
                Log.e("MESSAGES", "Ошибка сети при загрузке текущего пользователя: " + t.getMessage());
                // Все равно пытаемся загрузить получателя
                loadRecipientProfile();
            }
        });
    }

    private void loadRecipientProfile() {
        Call<Profile> call = apiService.getUserById(recipientId);
        call.enqueue(new Callback<Profile>() {
            @Override
            public void onResponse(Call<Profile> call, Response<Profile> response) {
                if (response.isSuccessful() && response.body() != null) {
                    recipientProfile = response.body();

                    // Устанавливаем имя и фото получателя в верхней панели
                    tvUserName.setText(recipientProfile.getFullName());
                    setUserAvatar(ivUserAvatar, recipientProfile);

                    Log.d("MESSAGES", "Загружен профиль получателя: " + recipientProfile.getFullName());

                    // Обновляем адаптер с загруженными профилями
                    updateAdapterWithProfiles();

                    // Загружаем переписку
                    loadConversation();

                } else {
                    Toast.makeText(Messages.this, "Ошибка загрузки профиля собеседника", Toast.LENGTH_SHORT).show();
                    // Все равно обновляем адаптер и загружаем переписку
                    updateAdapterWithProfiles();
                    loadConversation();
                }
            }

            @Override
            public void onFailure(Call<Profile> call, Throwable t) {
                Toast.makeText(Messages.this, "Ошибка загрузки профиля собеседника: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                // Все равно обновляем адаптер и загружаем переписку
                updateAdapterWithProfiles();
                loadConversation();
            }
        });
    }

    private void updateAdapterWithProfiles() {
        // Обновляем адаптер с загруженными профилями
        messageAdapter = new MessageAdapter(messageList, currentUserId, recipientProfile, currentUserProfile);
        rvMessages.setAdapter(messageAdapter);

        // Если есть сообщения, обновляем их отображение
        if (!messageList.isEmpty()) {
            messageAdapter.notifyDataSetChanged();
        }
    }

    private void loadConversation() {
        Call<List<Message>> call = apiService.getConversation(currentUserId, recipientId);
        call.enqueue(new Callback<List<Message>>() {
            @Override
            public void onResponse(Call<List<Message>> call, Response<List<Message>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    messageList.clear();
                    messageList.addAll(response.body());
                    messageAdapter.setMessages(messageList);
                    scrollToBottom();

                    Log.d("MESSAGES", "Загружено сообщений: " + messageList.size());
                } else {
                    Toast.makeText(Messages.this, "Ошибка загрузки переписки", Toast.LENGTH_SHORT).show();
                    Log.e("MESSAGES", "Ошибка ответа сервера: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Message>> call, Throwable t) {
                Toast.makeText(Messages.this, "Ошибка сети: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("MESSAGES", "Ошибка сети при загрузке переписки: " + t.getMessage());
            }
        });
    }

    private void setupClickListeners() {
        // Кнопка назад
        btnBack.setOnClickListener(v -> finish());

        // Кнопка отправки сообщения
        btnSend.setOnClickListener(v -> sendMessage());

        // Отправка по Enter
        etMessageInput.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                sendMessage();
                return true;
            }
            return false;
        });
    }

    private void sendMessage() {
        String messageText = etMessageInput.getText().toString().trim();

        if (messageText.isEmpty()) {
            Toast.makeText(this, "Введите сообщение", Toast.LENGTH_SHORT).show();
            return;
        }

        if (currentUserId == -1 || recipientId == -1) {
            Toast.makeText(this, "Ошибка данных пользователя", Toast.LENGTH_SHORT).show();
            return;
        }

        // Сразу добавляем сообщение в список для мгновенного отображения
        Message newMessage = new Message(currentUserId, recipientId, messageText);
        messageList.add(newMessage);
        messageAdapter.notifyItemInserted(messageList.size() - 1);

        // Очищаем поле ввода
        etMessageInput.setText("");

        // Скроллим к последнему сообщению
        scrollToBottom();

        // Отправляем сообщение на сервер
        Call<ResponseBody> call = apiService.sendMessage(currentUserId, recipientId, messageText);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Log.d("MESSAGES", "Сообщение успешно отправлено на сервер");
                    // Можно обновить переписку для получения актуальных данных
                    // loadConversation();
                } else {
                    Toast.makeText(Messages.this, "Ошибка отправки сообщения", Toast.LENGTH_SHORT).show();
                    Log.e("MESSAGES", "Ошибка отправки сообщения: " + response.code());

                    // Можно удалить сообщение из списка при ошибке
                    // messageList.remove(newMessage);
                    // messageAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(Messages.this, "Ошибка сети при отправке", Toast.LENGTH_SHORT).show();
                Log.e("MESSAGES", "Ошибка сети при отправке: " + t.getMessage());

                // Можно удалить сообщение из списка при ошибке сети
                // messageList.remove(newMessage);
                // messageAdapter.notifyDataSetChanged();
            }
        });
    }

    private void scrollToBottom() {
        rvMessages.postDelayed(() -> {
            if (messageList.size() > 0) {
                rvMessages.smoothScrollToPosition(messageList.size() - 1);
            }
        }, 100);
    }

    private void setUserAvatar(CircleImageView imageView, Profile profile) {
        if (profile != null && profile.photoBytes != null && !profile.photoBytes.isEmpty()) {
            try {
                byte[] decodedString = android.util.Base64.decode(profile.photoBytes, android.util.Base64.DEFAULT);
                android.graphics.Bitmap decodedByte = android.graphics.BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                if (decodedByte != null) {
                    imageView.setImageBitmap(decodedByte);
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // Если фото не загружено, используем дефолтное
        imageView.setImageResource(R.drawable.alt1);
    }
}