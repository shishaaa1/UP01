package com.example.boobleproject;

import android.graphics.Canvas;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

public class SwipeHelper extends ItemTouchHelper.SimpleCallback {

    private final ProfileAdapter adapter;
    private final ImageView swipeIndicator;
    private final Runnable onSwipeLeft, onSwipeRight;

    public SwipeHelper(
            ProfileAdapter adapter,
            RecyclerView recyclerView,
            ImageView swipeIndicator,
            Runnable onSwipeLeft,
            Runnable onSwipeRight
    ) {
        super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        this.adapter = adapter;
        this.swipeIndicator = swipeIndicator;
        this.onSwipeLeft = onSwipeLeft;
        this.onSwipeRight = onSwipeRight;
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView,
                          @NonNull RecyclerView.ViewHolder viewHolder,
                          @NonNull RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        int iconRes = direction == ItemTouchHelper.LEFT ? R.drawable.krest : R.drawable.heart;
        showSwipeIcon(iconRes);

        adapter.removeTopItem();

        if (direction == ItemTouchHelper.LEFT) {
            onSwipeLeft.run();
        } else {
            onSwipeRight.run();
        }
    }

    @Override
    public void onChildDraw(@NonNull Canvas c,
                            @NonNull RecyclerView recyclerView,
                            @NonNull RecyclerView.ViewHolder viewHolder,
                            float dX, float dY,
                            int actionState, boolean isCurrentlyActive) {

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

        float threshold = viewHolder.itemView.getWidth() * 0.35f;
        float progress = Math.min(1f, Math.abs(dX) / threshold);

        if (isCurrentlyActive && Math.abs(dX) > 10) {
            int iconRes = dX < 0 ? R.drawable.krest : R.drawable.heart;
            swipeIndicator.setImageResource(iconRes);
            swipeIndicator.setVisibility(View.VISIBLE);
            swipeIndicator.setAlpha(progress);
            swipeIndicator.setScaleX(0.8f + 0.2f * progress);
            swipeIndicator.setScaleY(0.8f + 0.2f * progress);
        } else if (!isCurrentlyActive) {
            hideSwipeIcon();
        }
    }

    @Override
    public void clearView(@NonNull RecyclerView recyclerView,
                          @NonNull RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);
        hideSwipeIcon();
    }

    private void showSwipeIcon(int iconRes) {
        swipeIndicator.setImageResource(iconRes);
        swipeIndicator.setVisibility(View.VISIBLE);
        swipeIndicator.animate()
                .alpha(1f)
                .setDuration(150)
                .withEndAction(() ->
                        swipeIndicator.animate()
                                .alpha(0f)
                                .setDuration(200)
                                .withEndAction(() ->
                                        swipeIndicator.setVisibility(View.GONE))
                                .start())
                .start();
    }

    private void hideSwipeIcon() {
        swipeIndicator.animate().cancel();
        swipeIndicator.setAlpha(0f);
        swipeIndicator.setVisibility(View.GONE);
    }
}
