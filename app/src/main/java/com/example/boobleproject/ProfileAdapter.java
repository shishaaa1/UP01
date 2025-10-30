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

public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.ProfileViewHolder> {

    private List<Profile> profiles;

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

    }

    @Override
    public int getItemCount() {
        return profiles.size();
    }

    public void removeTopItem() {
        if (!profiles.isEmpty()) {
            profiles.remove(0);
            notifyItemRemoved(0);
            notifyItemRangeChanged(0, profiles.size());
        }
    }

    public void addProfiles(List<Profile> newProfiles) {
        int start = profiles.size();
        profiles.addAll(newProfiles);
        notifyItemRangeInserted(start, newProfiles.size());
    }

    static class ProfileViewHolder extends RecyclerView.ViewHolder {
        ImageView ivPhoto;
        TextView tvName, tvAge;

        ProfileViewHolder(View itemView) {
            super(itemView);
            ivPhoto = itemView.findViewById(R.id.iv_profile_photo);
            tvName = itemView.findViewById(R.id.tv_name);
            tvAge = itemView.findViewById(R.id.tv_age);
        }

        void bind(Profile profile) {
            ivPhoto.setImageResource(profile.getPhotoRes());
            tvName.setText(profile.getName());
            tvAge.setText(profile.getAge() + " лет • " + profile.getCity());
        }
    }
}
