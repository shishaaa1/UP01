package com.example.boobleproject;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MutualLikeAdapter extends RecyclerView.Adapter<MutualLikeAdapter.MutualLikeViewHolder> {

    private List<Profile> mutualProfiles;
    private OnProfileClickListener listener;

    public interface OnProfileClickListener {
        void onMessageClick(Profile profile);
    }

    public MutualLikeAdapter(List<Profile> mutualProfiles, OnProfileClickListener listener) {
        this.mutualProfiles = new ArrayList<>(mutualProfiles);
        this.listener = listener;
    }

    @NonNull
    @Override
    public MutualLikeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_adapter_is_like, parent, false);
        return new MutualLikeViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull MutualLikeViewHolder holder, int position) {
        holder.bind(mutualProfiles.get(position));
    }

    @Override
    public int getItemCount() {
        return mutualProfiles.size();
    }

    public void setMutualProfiles(List<Profile> profiles) {
        this.mutualProfiles.clear();
        this.mutualProfiles.addAll(profiles);
        notifyDataSetChanged();
    }

    static class MutualLikeViewHolder extends RecyclerView.ViewHolder {
        ImageView ivPhoto;
        TextView tvName;
        TextView tvAge;
        OnProfileClickListener listener;

        MutualLikeViewHolder(View itemView, OnProfileClickListener listener) {
            super(itemView);
            this.listener = listener;
            ivPhoto = itemView.findViewById(R.id.iv_profile_photo);
            tvName = itemView.findViewById(R.id.tv_name);
            tvAge = itemView.findViewById(R.id.tv_age);
        }

        void bind(Profile profile) {
            // Детальная отладка
            Log.d("MUTUAL_ADAPTER", "=== BIND MUTUAL PROFILE ===");
            Log.d("MUTUAL_ADAPTER", "Position: " + getAdapterPosition());
            Log.d("MUTUAL_ADAPTER", "ID: " + profile.id);
            Log.d("MUTUAL_ADAPTER", "Name: " + profile.getFullName());
            Log.d("MUTUAL_ADAPTER", "PhotoBytes null: " + (profile.photoBytes == null));

            // Устанавливаем фото
            if (profile.photoBytes != null && !profile.photoBytes.isEmpty()) {
                try {
                    Log.d("MUTUAL_ADAPTER", "Starting photo decoding...");

                    byte[] decodedString = android.util.Base64.decode(profile.photoBytes, android.util.Base64.DEFAULT);
                    Log.d("MUTUAL_ADAPTER", "Decoded bytes length: " + decodedString.length);

                    android.graphics.Bitmap decodedByte = android.graphics.BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                    if (decodedByte != null) {
                        Log.d("MUTUAL_ADAPTER", "Bitmap created: " + decodedByte.getWidth() + "x" + decodedByte.getHeight());
                        ivPhoto.setImageBitmap(decodedByte);
                        Log.d("MUTUAL_ADAPTER", "Photo set successfully");
                    } else {
                        Log.e("MUTUAL_ADAPTER", "BitmapFactory returned null");
                        ivPhoto.setImageResource(profile.getPhotoRes());
                    }

                } catch (IllegalArgumentException e) {
                    Log.e("MUTUAL_ADAPTER", "Base64 decoding error: " + e.getMessage());
                    ivPhoto.setImageResource(profile.getPhotoRes());
                } catch (Exception e) {
                    Log.e("MUTUAL_ADAPTER", "Other error: " + e.getMessage());
                    ivPhoto.setImageResource(profile.getPhotoRes());
                }
            } else {
                Log.d("MUTUAL_ADAPTER", "No photo bytes available");
                ivPhoto.setImageResource(profile.getPhotoRes());
            }

            // Устанавливаем имя и возраст
            tvName.setText(profile.getFullName());
            tvAge.setText(profile.getAge() + " лет");

            Log.d("MUTUAL_ADAPTER", "=== BIND COMPLETE ===");

            // Обработчик клика на кнопку "Написать сообщение"
            itemView.findViewById(R.id.btn_send_message).setOnClickListener(v -> {
                if (listener != null) {
                    listener.onMessageClick(profile);
                }
            });
        }
    }
}