package com.example.boobleproject.LikesOrNot;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.boobleproject.Api.ApiClient;
import com.example.boobleproject.Api.ApiService;
import com.example.boobleproject.Profile;
import com.example.boobleproject.R;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LikeOrNotAdapter extends RecyclerView.Adapter<LikeOrNotAdapter.ProfileViewHolder> {

    private final Context context;
    private final ApiService apiService;
    private final int currentUserId;
    private List<Profile> profiles = new ArrayList<>();

    public LikeOrNotAdapter(Context context, int currentUserId) {
        this.context = context;
        this.currentUserId = currentUserId;
        this.apiService = ApiClient.getApiService();
    }

    public void setProfiles(List<Profile> profiles) {
        this.profiles = new ArrayList<>(profiles);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProfileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.islike_item, parent, false);
        return new ProfileViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProfileViewHolder holder, int position) {
        holder.bind(profiles.get(position));
    }

    @Override
    public int getItemCount() {
        return profiles.size();
    }

    private void removeProfile(Profile profile) {
        int pos = profiles.indexOf(profile);
        if (pos != -1) {
            profiles.remove(pos);
            notifyItemRemoved(pos);
        }
    }

    class ProfileViewHolder extends RecyclerView.ViewHolder {

        ImageView ivPhoto;
        TextView tvName, tvAge;
        ImageButton btnDislike, btnLikeBack;

        ProfileViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPhoto = itemView.findViewById(R.id.iv_profile_photo);
            tvName = itemView.findViewById(R.id.tv_name);
            tvAge = itemView.findViewById(R.id.tv_age);
            btnDislike = itemView.findViewById(R.id.btn_dislike);
            btnLikeBack = itemView.findViewById(R.id.btn_like_back);
        }

        void bind(Profile profile) {
            tvName.setText(profile.getFullName());
            tvAge.setText(profile.getAge() + " лет");

            // Устанавливаем фото
            if (profile.photoBytes != null && !profile.photoBytes.isEmpty()) {
                try {
                    byte[] decoded = Base64.decode(profile.photoBytes, Base64.DEFAULT);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(decoded, 0, decoded.length);
                    ivPhoto.setImageBitmap(bitmap);
                } catch (Exception e) {
                    ivPhoto.setImageResource(R.drawable.alt1);
                }
            } else {
                ivPhoto.setImageResource(R.drawable.alt1);
            }

            btnDislike.setOnClickListener(v -> revokeLike(profile));
            btnLikeBack.setOnClickListener(v -> sendLike(profile));
        }

        private void sendLike(Profile profile) {
            apiService.sendLike(currentUserId, profile.id, true).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(context, "Ответный лайк отправлен!", Toast.LENGTH_SHORT).show();
                        removeProfile(profile);
                    } else {
                        Toast.makeText(context, "Ошибка отправки лайка", Toast.LENGTH_SHORT).show();
                        Log.e("LikeOrNotAdapter", "sendLike error: " + response.code());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                    Toast.makeText(context, "Ошибка сети: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }

        private void revokeLike(Profile profile) {
            apiService.revokeLike(profile.id, currentUserId).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(context, "Лайк удалён", Toast.LENGTH_SHORT).show();
                        removeProfile(profile);
                    } else {
                        Toast.makeText(context, "Ошибка удаления лайка", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                    Toast.makeText(context, "Ошибка сети: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
