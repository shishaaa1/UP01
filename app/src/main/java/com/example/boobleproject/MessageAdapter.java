package com.example.boobleproject;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private List<Message> messages;
    private int currentUserId;
    private Profile recipientProfile;  // Профиль собеседника
    private Profile currentUserProfile; // Профиль текущего пользователя

    public MessageAdapter(List<Message> messages, int currentUserId, Profile recipientProfile, Profile currentUserProfile) {
        this.messages = messages != null ? messages : new ArrayList<>();
        this.currentUserId = currentUserId;
        this.recipientProfile = recipientProfile;
        this.currentUserProfile = currentUserProfile;

        Log.d("MESSAGE_ADAPTER", "Конструктор адаптера вызван");
        Log.d("MESSAGE_ADAPTER", "Сообщений: " + this.messages.size());
        Log.d("MESSAGE_ADAPTER", "currentUserId: " + currentUserId);
        Log.d("MESSAGE_ADAPTER", "recipientProfile: " + (recipientProfile != null ? recipientProfile.getFullName() : "null"));
        Log.d("MESSAGE_ADAPTER", "currentUserProfile: " + (currentUserProfile != null ? currentUserProfile.getFullName() : "null"));
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.message_item, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Log.d("MESSAGE_ADAPTER", "onBindViewHolder позиция: " + position);
        if (position < messages.size()) {
            Message message = messages.get(position);
            holder.bind(message);
        } else {
            Log.e("MESSAGE_ADAPTER", "Позиция " + position + " выходит за пределы списка (" + messages.size() + ")");
        }
    }

    @Override
    public int getItemCount() {
        int count = messages.size();
        Log.d("MESSAGE_ADAPTER", "getItemCount: " + count);
        return count;
    }

    public void setMessages(List<Message> newMessages) {
        Log.d("MESSAGE_ADAPTER", "setMessages вызван, новых сообщений: " + newMessages.size());

        // СОЗДАЕМ НОВЫЙ СПИСОК вместо очистки существующего
        this.messages = new ArrayList<>(newMessages); // ВАЖНО: создаем копию!

        Log.d("MESSAGE_ADAPTER", "Адаптер теперь содержит: " + this.messages.size() + " сообщений");
        notifyDataSetChanged();
        Log.d("MESSAGE_ADAPTER", "notifyDataSetChanged вызван");
    }

    public void addMessage(Message message) {
        this.messages.add(message);
        notifyItemInserted(messages.size() - 1);
    }

    class MessageViewHolder extends RecyclerView.ViewHolder {
        LinearLayout layoutReceived, layoutSent;
        TextView tvReceivedMessage, tvSentMessage;
        CircleImageView ivReceivedAvatar, ivSentAvatar;

        MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            layoutReceived = itemView.findViewById(R.id.layout_received);
            layoutSent = itemView.findViewById(R.id.layout_sent);
            tvReceivedMessage = itemView.findViewById(R.id.tv_received_message);
            tvSentMessage = itemView.findViewById(R.id.tv_sent_message);
            ivReceivedAvatar = itemView.findViewById(R.id.iv_received_avatar);
            ivSentAvatar = itemView.findViewById(R.id.iv_sent_avatar);
        }

        void bind(Message message) {
            boolean isSentByMe = message.userid1 == currentUserId;

            if (isSentByMe) {
                // Мое сообщение (правая сторона) - показываем мое фото
                layoutSent.setVisibility(View.VISIBLE);
                layoutReceived.setVisibility(View.GONE);
                tvSentMessage.setText(message.text);
                setUserAvatar(ivSentAvatar, currentUserProfile); // Мое фото

            } else {
                // Полученное сообщение (левая сторона) - показываем фото собеседника
                layoutSent.setVisibility(View.GONE);
                layoutReceived.setVisibility(View.VISIBLE);
                tvReceivedMessage.setText(message.text);
                setUserAvatar(ivReceivedAvatar, recipientProfile); // Фото собеседника
            }
        }

        private void setUserAvatar(CircleImageView imageView, Profile profile) {
            Log.d("MESSAGE_ADAPTER", "Устанавливаем аватар для: " + (profile != null ? profile.getFullName() : "null"));

            if (profile != null && profile.photoBytes != null && !profile.photoBytes.isEmpty()) {
                try {
                    Log.d("MESSAGE_ADAPTER", "PhotoBytes доступны, длина: " + profile.photoBytes.length());

                    byte[] decodedString = android.util.Base64.decode(profile.photoBytes, android.util.Base64.DEFAULT);
                    android.graphics.Bitmap decodedByte = android.graphics.BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                    if (decodedByte != null) {
                        Log.d("MESSAGE_ADAPTER", "Битмап создан: " + decodedByte.getWidth() + "x" + decodedByte.getHeight());
                        imageView.setImageBitmap(decodedByte);
                        Log.d("MESSAGE_ADAPTER", "Аватар установлен успешно");
                        return;
                    } else {
                        Log.e("MESSAGE_ADAPTER", "BitmapFactory вернул null");
                    }
                } catch (Exception e) {
                    Log.e("MESSAGE_ADAPTER", "Ошибка установки аватара: " + e.getMessage());
                }
            } else {
                Log.d("MESSAGE_ADAPTER", "PhotoBytes недоступны или пусты");
            }

            imageView.setImageResource(R.drawable.alt1);
        }
    }
}