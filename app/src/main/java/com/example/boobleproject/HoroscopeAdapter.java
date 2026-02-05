package com.example.boobleproject;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class HoroscopeAdapter extends RecyclerView.Adapter<HoroscopeAdapter.HoroscopeViewHolder>{

private List<HoroscopeSign> horoscopeList = new ArrayList<>();

public void setData(List<HoroscopeSign> list) {
    horoscopeList = list;
    notifyDataSetChanged();
}

@NonNull
@Override
public HoroscopeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

    View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_horoscope, parent, false);

    return new HoroscopeViewHolder(view);
}

@Override
public void onBindViewHolder(@NonNull HoroscopeViewHolder holder, int position) {
    holder.bind(horoscopeList.get(position));
}

@Override
public int getItemCount() {
    return horoscopeList.size();
}

static class HoroscopeViewHolder extends RecyclerView.ViewHolder {

    TextView tvSignName, tvDescription;
    ImageView ivSign;

    public HoroscopeViewHolder(@NonNull View itemView) {
        super(itemView);

        tvSignName = itemView.findViewById(R.id.tvSignName);
        tvDescription = itemView.findViewById(R.id.tvDescription);
        ivSign = itemView.findViewById(R.id.ivSign);
    }

    void bind(HoroscopeSign sign) {

        tvSignName.setText(sign.signName);
        tvDescription.setText(sign.text);

        Glide.with(itemView.getContext())
                .load(sign.imageUrl)
                .into(ivSign);
    }
}
}

