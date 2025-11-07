package com.example.boobleproject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
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


public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.ProfileViewHolder> {

    List<Profile> profiles;

    public ProfileAdapter(List<Profile> profiles) {
        this.profiles = new ArrayList<>(profiles);
    }

    @NonNull
    @Override
    public ProfileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_profile_card, parent, false);
        return new ProfileViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProfileViewHolder holder, int position) {
        holder.bind(profiles.get(position));

        if (position == 0) {
            // Только верхняя карточка видима и нормального размера
            holder.itemView.setAlpha(1f);
            holder.itemView.setScaleX(1f);
            holder.itemView.setScaleY(1f);
            holder.itemView.setTranslationY(0f);
            holder.itemView.setVisibility(View.VISIBLE);

            // Устанавливаем нормальную высоту
            ViewGroup.LayoutParams params = holder.itemView.getLayoutParams();
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            holder.itemView.setLayoutParams(params);
        } else {
            // Все остальные карточки невидимы и с нулевой высотой
            holder.itemView.setVisibility(View.INVISIBLE);

            // Устанавливаем нулевую высоту чтобы нельзя было прокрутить
            ViewGroup.LayoutParams params = holder.itemView.getLayoutParams();
            params.height = 0;
            holder.itemView.setLayoutParams(params);
        }
    }

    @Override
    public int getItemCount() {
        return profiles.size();
    }

    public void removeItemAt(int position) {
        if (position >= 0 && position < profiles.size()) {
            profiles.remove(position);
            notifyItemRemoved(position);

            // Обновляем новую верхнюю карточку
            if (!profiles.isEmpty()) {
                notifyItemChanged(0);
            }
        }
    }

    public void addProfiles(List<Profile> newProfiles) {
        Log.d("DEBUG_ADAPTER", "addProfiles called with " + newProfiles.size() + " profiles");
        int start = profiles.size();
        profiles.addAll(newProfiles);
        notifyItemRangeInserted(start, newProfiles.size());
        Log.d("DEBUG_ADAPTER", "Now total profiles: " + profiles.size());
    }

    public boolean isEmpty() {
        return profiles.isEmpty();
    }

    static class ProfileViewHolder extends RecyclerView.ViewHolder {
        ImageView ivPhoto;
        TextView tvName, tvAge, tvBio;

        ProfileViewHolder(View itemView) {
            super(itemView);
            ivPhoto = itemView.findViewById(R.id.iv_profile_photo);
            tvName = itemView.findViewById(R.id.tv_name);
            tvAge = itemView.findViewById(R.id.tv_age);
            tvBio = itemView.findViewById(R.id.tv_bio);
        }

        void bind(Profile profile) {
            // Детальная отладка
            Log.d("DEBUG_ADAPTER", "=== BIND PROFILE ===");
            Log.d("DEBUG_ADAPTER", "Position: " + getAdapterPosition());
            Log.d("DEBUG_ADAPTER", "ID: " + profile.id);
            Log.d("DEBUG_ADAPTER", "Name: " + profile.getFullName());
            Log.d("DEBUG_ADAPTER","SEX" + profile.getGenderAsString());
            Log.d("DEBUG_ADAPTER", "PhotoBytes null: " + (profile.photoBytes == null));
            Log.d("DEBUG_ADAPTER", "PhotoBytes empty: " + (profile.photoBytes != null && profile.photoBytes.isEmpty()));
            Log.d("DEBUG_ADAPTER", "PhotoBytes length: " + (profile.photoBytes != null ? profile.photoBytes.length() : 0));

            // Пробуем установить фото
            if (profile.photoBytes != null && !profile.photoBytes.isEmpty()) {
                try {
                    Log.d("DEBUG_ADAPTER", "Starting photo decoding...");

                    // Проверяем Base64 строку
                    if (!profile.photoBytes.startsWith("/9j/") && !profile.photoBytes.startsWith("iVBOR")) {
                        Log.d("DEBUG_ADAPTER", "Base64 doesn't look like JPEG/PNG, starts with: " +
                                profile.photoBytes.substring(0, Math.min(10, profile.photoBytes.length())));
                    }

                    byte[] decodedString = Base64.decode(profile.photoBytes, Base64.DEFAULT);
                    Log.d("DEBUG_ADAPTER", "Decoded bytes length: " + decodedString.length);

                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                    if (decodedByte != null) {
                        Log.d("DEBUG_ADAPTER", "Bitmap created: " + decodedByte.getWidth() + "x" + decodedByte.getHeight());
                        ivPhoto.setImageBitmap(decodedByte);
                        Log.d("DEBUG_ADAPTER", "Photo set successfully");
                    } else {
                        Log.e("DEBUG_ADAPTER", "BitmapFactory returned null");
                        ivPhoto.setImageResource(profile.getPhotoRes());
                    }

                } catch (IllegalArgumentException e) {
                    Log.e("DEBUG_ADAPTER", "Base64 decoding error: " + e.getMessage());
                    ivPhoto.setImageResource(profile.getPhotoRes());
                } catch (Exception e) {
                    Log.e("DEBUG_ADAPTER", "Other error: " + e.getMessage());
                    ivPhoto.setImageResource(profile.getPhotoRes());
                }
            } else {
                Log.d("DEBUG_ADAPTER", "No photo bytes available");
                ivPhoto.setImageResource(profile.getPhotoRes());
            }

            // Устанавливаем остальные данные
            tvName.setText(profile.getFullName());
            String ageAndGender = profile.getAge() + " лет • " + profile.getGenderAsString();
            tvAge.setText(ageAndGender);
            tvBio.setText(profile.getBio());

            Log.d("DEBUG_ADAPTER", "=== BIND COMPLETE ===");
        }
    }
}
