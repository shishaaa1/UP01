package com.example.boobleproject;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AchievementAdapter  extends RecyclerView.Adapter<AchievementAdapter.ViewHolder> {

    private List<Achievement> list;

    public AchievementAdapter(List<Achievement> list) {
        this.list = list;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView icon;
        TextView title, description, progress;

        ViewHolder(View v) {
            super(v);
            icon = v.findViewById(R.id.ivAchievement);
            title = v.findViewById(R.id.tvTitle);
            description = v.findViewById(R.id.tvDescription);
            progress = v.findViewById(R.id.tvProgress);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_gaim, parent, false);
        return new ViewHolder(v);
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {

        Achievement a = list.get(position);

        h.title.setText(a.title);
        h.description.setText(a.description);
        h.progress.setText(a.getPercent() + "%");
        h.icon.setImageResource(a.iconRes);

        // СНАЧАЛА всегда сбрасываем состояние
        h.itemView.setAlpha(1f);

        // Потом применяем затемнение если не выполнено
        if (!a.completed) {
            h.itemView.setAlpha(0.4f);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
