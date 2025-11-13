package com.example.boobleproject.MessageMatch;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.boobleproject.Profile;
import com.example.boobleproject.R;

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



            if (profile.photoBytes != null && !profile.photoBytes.isEmpty()) {
                try {


                    byte[] decodedString = android.util.Base64.decode(profile.photoBytes, android.util.Base64.DEFAULT);


                    android.graphics.Bitmap decodedByte = android.graphics.BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                    if (decodedByte != null) {

                        ivPhoto.setImageBitmap(decodedByte);

                    } else {

                        ivPhoto.setImageResource(profile.getPhotoRes());
                    }

                } catch (IllegalArgumentException e) {

                    ivPhoto.setImageResource(profile.getPhotoRes());
                } catch (Exception e) {
                    Log.e("MUTUAL_ADAPTER", "Other error: " + e.getMessage());
                    ivPhoto.setImageResource(profile.getPhotoRes());
                }
            } else {

                ivPhoto.setImageResource(profile.getPhotoRes());
            }


            tvName.setText(profile.getFullName());
            tvAge.setText(profile.getAge() + " лет");




            itemView.findViewById(R.id.btn_send_message).setOnClickListener(v -> {
                if (listener != null) {
                    listener.onMessageClick(profile);
                }
            });
        }
    }
}