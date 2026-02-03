package com.example.boobleproject.MessageMatch;

import androidx.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.boobleproject.R;

import java.util.List;

public class AiSuggestionAdapter extends RecyclerView.Adapter<AiSuggestionAdapter.ViewHolder> {

    public interface OnSuggestionClickListener {
        void onClick(String text);
    }

    private List<AiSuggestion> suggestions;
    private OnSuggestionClickListener listener;

    public AiSuggestionAdapter(List<AiSuggestion> suggestions,
                               OnSuggestionClickListener listener) {
        this.suggestions = suggestions;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_ai_suggestion, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AiSuggestion suggestion = suggestions.get(position);
        holder.textView.setText(suggestion.text);

        holder.itemView.setOnClickListener(v ->
                listener.onClick(suggestion.text)
        );
    }

    @Override
    public int getItemCount() {
        return suggestions.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        ViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.tv_suggestion);
        }
    }
}
