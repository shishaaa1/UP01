package com.example.boobleproject;

import android.graphics.Canvas;
import android.os.Looper;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;



public class SwipeHelper extends ItemTouchHelper.SimpleCallback {

    private final ProfileAdapter adapter;
    private final ImageView swipeIndicator;
    private final Runnable onSwipeLeft, onSwipeRight;
    private final Handler handler;
    private boolean isSwiping = false;

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
        this.handler = new Handler(Looper.getMainLooper());
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView,
                          @NonNull RecyclerView.ViewHolder viewHolder,
                          @NonNull RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        isSwiping = true;

        int swipedPosition = viewHolder.getBindingAdapterPosition();

        int iconRes = direction == ItemTouchHelper.LEFT ? R.drawable.krest : R.drawable.heart;
        showSwipeIconWithDelay(iconRes, () -> {
            if (swipedPosition != RecyclerView.NO_POSITION) {
                adapter.removeItemAt(swipedPosition);
            }

            if (direction == ItemTouchHelper.LEFT) {
                onSwipeLeft.run();
            } else {
                onSwipeRight.run();
            }

            isSwiping = false;
        });
    }

    @Override
    public void onChildDraw(@NonNull Canvas c,
                            @NonNull RecyclerView recyclerView,
                            @NonNull RecyclerView.ViewHolder viewHolder,
                            float dX, float dY,
                            int actionState, boolean isCurrentlyActive) {

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

        if (isSwiping) return; // Блокируем анимацию во время свайпа

        float threshold = viewHolder.itemView.getWidth() * 0.35f;
        float progress = Math.min(1f, Math.abs(dX) / threshold);

        if (isCurrentlyActive && Math.abs(dX) > 10) {
            int iconRes = dX < 0 ? R.drawable.krest : R.drawable.heart;
            swipeIndicator.setImageResource(iconRes);
            swipeIndicator.setVisibility(View.VISIBLE);

            // Плавное увеличение иконки
            float scale = 0.6f + 0.4f * progress;
            swipeIndicator.setScaleX(scale);
            swipeIndicator.setScaleY(scale);
            swipeIndicator.setAlpha(progress);

        } else if (!isCurrentlyActive) {
            hideSwipeIcon();
        }
    }

    @Override
    public void clearView(@NonNull RecyclerView recyclerView,
                          @NonNull RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);
        if (!isSwiping) {
            hideSwipeIcon();
        }
    }

    private void showSwipeIconWithDelay(int iconRes, Runnable onComplete) {
        // Сначала показываем иконку с анимацией
        swipeIndicator.setImageResource(iconRes);
        swipeIndicator.setVisibility(View.VISIBLE);
        swipeIndicator.setScaleX(0.6f);
        swipeIndicator.setScaleY(0.6f);
        swipeIndicator.setAlpha(0f);

        // Анимация появления и увеличения
        swipeIndicator.animate()
                .alpha(1f)
                .scaleX(1.2f) // Сначала увеличиваем немного больше
                .scaleY(1.2f)
                .setDuration(300)
                .withEndAction(() -> {
                    // Затем уменьшаем до нормального размера и исчезаем
                    swipeIndicator.animate()
                            .scaleX(1f)
                            .scaleY(1f)
                            .setDuration(200)
                            .withEndAction(() -> {
                                // Исчезаем
                                swipeIndicator.animate()
                                        .alpha(0f)
                                        .setDuration(200)
                                        .withEndAction(() -> {
                                            swipeIndicator.setVisibility(View.GONE);
                                            // Вызываем колбэк после всей анимации
                                            handler.postDelayed(onComplete, 100);
                                        })
                                        .start();
                            })
                            .start();
                })
                .start();
    }

    private void hideSwipeIcon() {
        swipeIndicator.animate().cancel();
        swipeIndicator.setAlpha(0f);
        swipeIndicator.setVisibility(View.GONE);
    }

    @Override
    public float getSwipeThreshold(@NonNull RecyclerView.ViewHolder viewHolder) {
        return 0.5f; // Увеличиваем порог свайпа для более плавного срабатывания
    }

    @Override
    public float getSwipeEscapeVelocity(float defaultValue) {
        return defaultValue * 0.5f; // Уменьшаем скорость свайпа
    }
}
