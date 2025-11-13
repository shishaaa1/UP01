package com.example.boobleproject.MessageMatch;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
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

import com.example.boobleproject.Api.ApiClient;
import com.example.boobleproject.Api.ApiService;
import com.example.boobleproject.Profile;
import com.example.boobleproject.R;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Messages extends AppCompatActivity {

    private Handler refreshHandler;
    private Runnable refreshRunnable;
    private static final long REFRESH_INTERVAL = 2000;

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

        SharedPreferences userPrefs = getSharedPreferences("userPrefs", MODE_PRIVATE);
        currentUserId = userPrefs.getInt("userId", -1);

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
        setupAutoRefresh();

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

        messageAdapter = new MessageAdapter(messageList, currentUserId, null, null);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);

        rvMessages.setLayoutManager(layoutManager);
        rvMessages.setAdapter(messageAdapter);
    }

    private void loadUserProfiles() {

        loadCurrentUserProfile();
    }

    private void loadCurrentUserProfile() {
        Call<Profile> call = apiService.getUserById(currentUserId);
        call.enqueue(new Callback<Profile>() {
            @Override
            public void onResponse(Call<Profile> call, Response<Profile> response) {
                if (response.isSuccessful() && response.body() != null) {
                    currentUserProfile = response.body();

                    loadCurrentUserPhoto();

                } else {

                    loadRecipientProfile();
                }
            }

            @Override
            public void onFailure(Call<Profile> call, Throwable t) {
                Log.e("MESSAGES", "Ошибка сети при загрузке текущего пользователя: " + t.getMessage());
                loadRecipientProfile();
            }
        });
    }

    private void loadCurrentUserPhoto() {
        Call<ResponseBody> call = apiService.getPhotoByUserId(currentUserId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        byte[] photoBytes = response.body().bytes();
                        String base64Photo = android.util.Base64.encodeToString(photoBytes, android.util.Base64.DEFAULT);
                        currentUserProfile.photoBytes = base64Photo;

                    } catch (Exception e) {
                        Log.e("MESSAGES", "Ошибка загрузки фото текущего пользователя: " + e.getMessage());
                    }
                } else {
                    Log.d("MESSAGES", "Фото текущего пользователя не найдено");
                }

                loadRecipientProfile();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("MESSAGES", "Ошибка сети при загрузке фото текущего пользователя: " + t.getMessage());
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

                    tvUserName.setText(recipientProfile.getFullName());

                    loadRecipientPhoto();

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
    private void loadRecipientPhoto() {
        Call<ResponseBody> call = apiService.getPhotoByUserId(recipientId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        byte[] photoBytes = response.body().bytes();
                        String base64Photo = android.util.Base64.encodeToString(photoBytes, android.util.Base64.DEFAULT);
                        recipientProfile.photoBytes = base64Photo;
                        Log.d("MESSAGES", "Фото получателя загружено");

                        runOnUiThread(() -> {
                            setUserAvatar(ivUserAvatar, recipientProfile);

                            updateAdapterWithProfiles();
                        });

                    } catch (Exception e) {

                    }
                } else {
                    Log.d("MESSAGES", "Фото получателя не найдено");
                }
                loadConversation();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("MESSAGES", "Ошибка сети при загрузке фото получателя: " + t.getMessage());
                loadConversation();
            }
        });
    }

    private void updateAdapterWithProfiles() {
        runOnUiThread(() -> {

            messageAdapter = new MessageAdapter(messageList, currentUserId, recipientProfile, currentUserProfile);
            rvMessages.setAdapter(messageAdapter);

            if (!messageList.isEmpty()) {
                messageAdapter.notifyDataSetChanged();
            }
        });
    }


    private void loadConversation() {


        Call<ResponseBody> call = apiService.getConversationRaw(currentUserId, recipientId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {


                if (response.isSuccessful() && response.body() != null) {
                    try {

                        String jsonResponse = response.body().string();


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


            for (int i = 0; i < jsonArray.length(); i++) {
                org.json.JSONObject jsonObject = jsonArray.getJSONObject(i);

                Message message = new Message();
                message.id = jsonObject.getInt("id");
                message.userid1 = jsonObject.getInt("userid1");
                message.userid2 = jsonObject.getInt("userid2");
                message.text = jsonObject.getString("text");
                message.timestamp = jsonObject.getString("sendAt");

                receivedMessages.add(message);

            }



            messageList.clear();

            for (Message msg : receivedMessages) {
                messageList.add(msg);
                Log.d("MESSAGES_DEBUG", "Добавлено в messageList: " + msg.text);
            }


            for (int i = 0; i < messageList.size(); i++) {
                Message msg = messageList.get(i);
                Log.d("MESSAGES_DEBUG", "messageList[" + i + "]: userid1=" + msg.userid1 + ", text=" + msg.text);
            }

            updateAdapter();

        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    private void updateAdapter() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if (messageAdapter == null) {
                    Log.d("MESSAGES_DEBUG", "Создаем новый адаптер");
                    messageAdapter = new MessageAdapter(messageList, currentUserId, recipientProfile, currentUserProfile);
                    rvMessages.setAdapter(messageAdapter);
                } else {
                    Log.d("MESSAGES_DEBUG", "Обновляем существующий адаптер");
                    messageAdapter.setMessages(messageList);
                }


                messageAdapter.notifyDataSetChanged();
                Log.d("MESSAGES_DEBUG", "notifyDataSetChanged вызван");

                scrollToBottom();

                if (rvMessages.getVisibility() != View.VISIBLE) {
                    rvMessages.setVisibility(View.VISIBLE);
                    Log.d("MESSAGES_DEBUG", "RecyclerView теперь видим");
                }
            }
        });
    }
    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());
        btnSend.setOnClickListener(v -> sendMessage());
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

        Message newMessage = new Message(currentUserId, recipientId, messageText);
        messageList.add(newMessage);
        messageAdapter.notifyItemInserted(messageList.size() - 1);

        etMessageInput.setText("");

        scrollToBottom();

        Call<ResponseBody> call = apiService.sendMessage(currentUserId, recipientId, messageText);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {

                    loadConversation();
                } else {
                    Toast.makeText(Messages.this, "Ошибка отправки сообщения", Toast.LENGTH_SHORT).show();

                    messageList.remove(newMessage);
                    messageAdapter.notifyDataSetChanged();
                    Toast.makeText(Messages.this, "Сообщение не отправлено", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(Messages.this, "Ошибка сети при отправке", Toast.LENGTH_SHORT).show();
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
        imageView.setImageResource(R.drawable.alt1);
    }

    private void setupAutoRefresh() {
        refreshHandler = new Handler();
        refreshRunnable = new Runnable() {
            @Override
            public void run() {
                loadConversation();
                refreshHandler.postDelayed(this, REFRESH_INTERVAL);
            }
        };

    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshHandler.postDelayed(refreshRunnable, REFRESH_INTERVAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        refreshHandler.removeCallbacks(refreshRunnable);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (refreshHandler != null) {
            refreshHandler.removeCallbacks(refreshRunnable);
        }
    }
}