package com.example.boobleproject;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LikeManager {
    private Context context;
    private ApiService apiService;
    private SharedPreferences sharedPreferences;

    public LikeManager(Context context) {
        this.context = context;
        this.apiService = ApiClient.getApiService();
        this.sharedPreferences = context.getSharedPreferences("userPrefs", Context.MODE_PRIVATE);
    }

    public void sendLike(int toUserId, boolean isLike) {
        int fromUserId = sharedPreferences.getInt("userId", -1);

        if (fromUserId == -1) {
            Log.e("LIKE_ERROR", "User ID not found in SharedPreferences");
            return;
        }

        Log.d("LIKE_DEBUG", "Sending like: fromUserId=" + fromUserId +
                ", toUserId=" + toUserId + ", isLike=" + isLike);

        apiService.sendLike(fromUserId, toUserId, isLike).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d("LIKE_SUCCESS", "Like sent successfully: " + (isLike ? "LIKE" : "DISLIKE"));
                } else {
                    Log.e("LIKE_ERROR", "Server error: " + response.code() + " - " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("LIKE_ERROR", "Network error: " + t.getMessage());
            }
        });
    }
}