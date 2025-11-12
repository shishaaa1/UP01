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
        // ДОБАВЬТЕ ЭТИ ЛОГИ ДЛЯ ОТЛАДКИ
        Log.d("MESSAGES_DEBUG", "=== DEBUG USER IDs ===");
        Log.d("MESSAGES_DEBUG", "Current User ID from SharedPreferences: " + currentUserId);
        Log.d("MESSAGES_DEBUG", "Recipient ID from Intent: " + recipientId);
        Log.d("MESSAGES_DEBUG", "=== END DEBUG ===");

        if (currentUserId == -1 || recipientId == -1) {
            Toast.makeText(this, "Ошибка данных пользователя", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        // ДОБАВЬТЕ ЭТИ ЛОГИ ДЛЯ ОТЛАДКИ
        Log.d("MESSAGES_DEBUG", "=== DEBUG USER IDs ===");
        Log.d("MESSAGES_DEBUG", "Current User ID from SharedPreferences: " + currentUserId);
        Log.d("MESSAGES_DEBUG", "Recipient ID from Intent: " + recipientId);
        Log.d("MESSAGES_DEBUG", "=== END DEBUG ===");

        initViews();
        setupRecyclerView();
        loadUserProfiles();
        setupClickListeners();

        // УБЕДИТЕСЬ ЧТО messageList ИНИЦИАЛИЗИРОВАН
        if (messageList == null) {
            messageList = new ArrayList<>();
            Log.d("MESSAGES_DEBUG", "messageList инициализирован");
        }
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

                    // НЕ ВЫЗЫВАЕМ updateAdapterWithProfiles() - адаптер уже создан
                    // Просто загружаем переписку
                    loadConversation();

                } else {
                    Toast.makeText(Messages.this, "Ошибка загрузки профиля собеседника", Toast.LENGTH_SHORT).show();
                    loadConversation();
                }
            }

            @Override
            public void onFailure(Call<Profile> call, Throwable t) {
                Toast.makeText(Messages.this, "Ошибка загрузки профиля собеседника: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                loadConversation();
            }
        });
    }



    private void loadConversation() {
        Log.d("MESSAGES_DEBUG", "Загружаем переписку между: currentUserId=" + currentUserId + " и recipientId=" + recipientId);

        Call<ResponseBody> call = apiService.getConversationRaw(currentUserId, recipientId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d("MESSAGES_DEBUG", "Response code: " + response.code());
                Log.d("MESSAGES_DEBUG", "Response isSuccessful: " + response.isSuccessful());

                if (response.isSuccessful() && response.body() != null) {
                    try {
                        // Получаем сырой JSON
                        String jsonResponse = response.body().string();
                        Log.d("MESSAGES_DEBUG", "Raw JSON response: " + jsonResponse);

                        // Парсим JSON вручную
                        parseMessagesManually(jsonResponse);

                    } catch (Exception e) {
                        Log.e("MESSAGES_DEBUG", "Ошибка парсинга JSON: " + e.getMessage());
                        Toast.makeText(Messages.this, "Ошибка обработки сообщений", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(Messages.this, "Ошибка загрузки переписки", Toast.LENGTH_SHORT).show();
                    Log.e("MESSAGES_DEBUG", "Ошибка ответа сервера: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(Messages.this, "Ошибка сети: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("MESSAGES_DEBUG", "Ошибка сети: " + t.getMessage());
            }
        });
    }

    private void parseMessagesManually(String jsonResponse) {
        try {
            org.json.JSONArray jsonArray = new org.json.JSONArray(jsonResponse);
            List<Message> receivedMessages = new ArrayList<>();

            Log.d("MESSAGES_DEBUG", "Найдено сообщений в JSON: " + jsonArray.length());

            for (int i = 0; i < jsonArray.length(); i++) {
                org.json.JSONObject jsonObject = jsonArray.getJSONObject(i);

                Message message = new Message();
                message.id = jsonObject.getInt("id");
                message.userid1 = jsonObject.getInt("userid1");
                message.userid2 = jsonObject.getInt("userid2");
                message.text = jsonObject.getString("text");
                message.timestamp = jsonObject.getString("sendAt");

                receivedMessages.add(message);
                Log.d("MESSAGES_DEBUG", "Добавлено в receivedMessages: " + receivedMessages.size());
            }

            // ДЕТАЛЬНЫЕ ЛОГИ ДЛЯ ОТЛАДКИ
            Log.d("MESSAGES_DEBUG", "=== ДЕТАЛЬНАЯ ОТЛАДКА ===");
            Log.d("MESSAGES_DEBUG", "receivedMessages size: " + receivedMessages.size());
            Log.d("MESSAGES_DEBUG", "messageList до очистки: " + messageList.size());

            messageList.clear();
            Log.d("MESSAGES_DEBUG", "messageList после очистки: " + messageList.size());

            // ИСПОЛЬЗУЙТЕ ЦИКЛ ВМЕСТО addAll
            for (Message msg : receivedMessages) {
                messageList.add(msg);
                Log.d("MESSAGES_DEBUG", "Добавлено в messageList: " + msg.text);
            }

            Log.d("MESSAGES_DEBUG", "messageList после добавления: " + messageList.size());

            // ПРОВЕРЬТЕ КАЖДОЕ СООБЩЕНИЕ В messageList
            for (int i = 0; i < messageList.size(); i++) {
                Message msg = messageList.get(i);
                Log.d("MESSAGES_DEBUG", "messageList[" + i + "]: userid1=" + msg.userid1 + ", text=" + msg.text);
            }

            Log.d("MESSAGES_DEBUG", "=== КОНЕЦ ОТЛАДКИ ===");

            // ОБНОВИТЕ АДАПТЕР
            updateAdapter();

        } catch (Exception e) {
            Log.e("MESSAGES_DEBUG", "Ошибка ручного парсинга JSON: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void updateAdapter() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d("MESSAGES_DEBUG", "Обновляем адаптер в UI потоке");
                Log.d("MESSAGES_DEBUG", "messageList size в updateAdapter: " + messageList.size());

                // Убедитесь что адаптер существует
                if (messageAdapter == null) {
                    Log.d("MESSAGES_DEBUG", "Создаем новый адаптер");
                    messageAdapter = new MessageAdapter(messageList, currentUserId, recipientProfile, currentUserProfile);
                    rvMessages.setAdapter(messageAdapter);
                } else {
                    Log.d("MESSAGES_DEBUG", "Обновляем существующий адаптер");
                    messageAdapter.setMessages(messageList);
                }

                // Принудительно обновите RecyclerView
                messageAdapter.notifyDataSetChanged();
                Log.d("MESSAGES_DEBUG", "notifyDataSetChanged вызван");

                scrollToBottom();

                // Проверьте видимость RecyclerView
                if (rvMessages.getVisibility() != View.VISIBLE) {
                    rvMessages.setVisibility(View.VISIBLE);
                    Log.d("MESSAGES_DEBUG", "RecyclerView теперь видим");
                }
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
                    // ОБНОВЛЯЕМ переписку чтобы получить сообщение с сервера
                    loadConversation();
                } else {
                    Toast.makeText(Messages.this, "Ошибка отправки сообщения", Toast.LENGTH_SHORT).show();
                    Log.e("MESSAGES", "Ошибка отправки сообщения: " + response.code());

                    // УДАЛЯЕМ сообщение из списка при ошибке
                    messageList.remove(newMessage);
                    messageAdapter.notifyDataSetChanged();
                    Toast.makeText(Messages.this, "Сообщение не отправлено", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(Messages.this, "Ошибка сети при отправке", Toast.LENGTH_SHORT).show();
                Log.e("MESSAGES", "Ошибка сети при отправке: " + t.getMessage());

                // УДАЛЯЕМ сообщение из списка при ошибке сети
                messageList.remove(newMessage);
                messageAdapter.notifyDataSetChanged();
                Toast.makeText(Messages.this, "Сообщение не отправлено из-за ошибки сети", Toast.LENGTH_SHORT).show();
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