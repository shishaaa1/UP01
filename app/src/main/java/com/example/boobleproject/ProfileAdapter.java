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
        Profile profile = profiles.get(position);
        holder.bind(profile);

        if (position == 0) {

            holder.itemView.setAlpha(1f);
            holder.itemView.setScaleX(1f);
            holder.itemView.setScaleY(1f);
            holder.itemView.setTranslationY(0f);
            holder.itemView.setVisibility(View.VISIBLE);

            ViewGroup.LayoutParams params = holder.itemView.getLayoutParams();
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            holder.itemView.setLayoutParams(params);


        } else {

            holder.itemView.setVisibility(View.INVISIBLE);


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

            if (!profiles.isEmpty()) {
                notifyItemChanged(0);
            }
        }
    }

    public void addProfiles(List<Profile> newProfiles) {

        int start = profiles.size();
        profiles.addAll(newProfiles);
        notifyItemRangeInserted(start, newProfiles.size());

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


            if (profile.photoBytes != null && !profile.photoBytes.isEmpty()) {
                try {

                    if (!profile.photoBytes.startsWith("/9j/") && !profile.photoBytes.startsWith("iVBOR")) {

                    }

                    byte[] decodedString = Base64.decode(profile.photoBytes, Base64.DEFAULT);


                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                    if (decodedByte != null) {

                        ivPhoto.setImageBitmap(decodedByte);

                    } else {

                        ivPhoto.setImageResource(profile.getPhotoRes());
                    }

                } catch (IllegalArgumentException e) {

                    ivPhoto.setImageResource(profile.getPhotoRes());
                } catch (Exception e) {

                    ivPhoto.setImageResource(profile.getPhotoRes());
                }
            } else {

                ivPhoto.setImageResource(profile.getPhotoRes());
            }

            tvName.setText(profile.getFullName());
            String ageAndGender = profile.getAge() + " лет • " + profile.getGenderAsString();
            tvAge.setText(ageAndGender);
            tvBio.setText(profile.getBio());

        }
    }
}
