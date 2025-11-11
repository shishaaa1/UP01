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
        Message message = messages.get(position);
        holder.bind(message);
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public void setMessages(List<Message> newMessages) {
        this.messages.clear();
        this.messages.addAll(newMessages);
        notifyDataSetChanged();
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
            boolean isSentByMe = message.senderId == currentUserId;

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
            if (profile != null && profile.photoBytes != null && !profile.photoBytes.isEmpty()) {
                try {
                    Log.d("MESSAGE_ADAPTER", "Setting avatar for user: " + profile.getFullName());

                    byte[] decodedString = android.util.Base64.decode(profile.photoBytes, android.util.Base64.DEFAULT);
                    android.graphics.Bitmap decodedByte = android.graphics.BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                    if (decodedByte != null) {
                        Log.d("MESSAGE_ADAPTER", "Avatar bitmap created: " + decodedByte.getWidth() + "x" + decodedByte.getHeight());
                        imageView.setImageBitmap(decodedByte);
                        Log.d("MESSAGE_ADAPTER", "Avatar set successfully");
                    } else {
                        Log.e("MESSAGE_ADAPTER", "BitmapFactory returned null");
                        imageView.setImageResource(R.drawable.alt1);
                    }

                } catch (IllegalArgumentException e) {
                    Log.e("MESSAGE_ADAPTER", "Base64 decoding error: " + e.getMessage());
                    imageView.setImageResource(R.drawable.alt1);
                } catch (Exception e) {
                    Log.e("MESSAGE_ADAPTER", "Other error: " + e.getMessage());
                    imageView.setImageResource(R.drawable.alt1);
                }
            } else {
                Log.d("MESSAGE_ADAPTER", "No photo bytes available for user");
                imageView.setImageResource(R.drawable.alt1);
            }
        }
    }
}