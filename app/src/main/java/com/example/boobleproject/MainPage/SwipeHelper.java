package com.example.boobleproject.MainPage;

import android.graphics.Canvas;
import android.os.Looper;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.boobleproject.ProfileAdapter;
import com.example.boobleproject.R;


public class SwipeHelper extends ItemTouchHelper.SimpleCallback {

    private final ProfileAdapter adapter;
    private final RecyclerView recyclerView;
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
        this.recyclerView = recyclerView;
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

        int iconRes = direction == ItemTouchHelper.LEFT ? R.drawable.krest : R.drawable.heart;
        showSwipeIconWithDelay(iconRes, () -> {
            // Удаляем верхнюю карточку (всегда позиция 0)
            adapter.removeItemAt(0);

            // Анимируем появление следующей карточки
            animateNextCardAppearance();

            if (direction == ItemTouchHelper.LEFT) {

                onSwipeLeft.run();
            } else {
                onSwipeRight.run();
            }

            isSwiping = false;
        });
    }

    private void animateNextCardAppearance() {
        handler.postDelayed(() -> {
            RecyclerView.ViewHolder nextViewHolder = recyclerView.findViewHolderForAdapterPosition(0);
            if (nextViewHolder != null) {
                View nextCard = nextViewHolder.itemView;

                // Устанавливаем начальное состояние (карточка снизу)
                nextCard.setTranslationY(300f);
                nextCard.setAlpha(0f);
                nextCard.setScaleX(0.8f);
                nextCard.setScaleY(0.8f);

                // Анимация появления снизу
                nextCard.animate()
                        .translationY(0f)
                        .alpha(1f)
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(500)
                        .start();
            }
        }, 100);
    }

    @Override
    public void onChildDraw(@NonNull Canvas c,
                            @NonNull RecyclerView recyclerView,
                            @NonNull RecyclerView.ViewHolder viewHolder,
                            float dX, float dY,
                            int actionState, boolean isCurrentlyActive) {

        // Разрешаем свайп только для верхней карточки (позиция 0)
        if (viewHolder.getBindingAdapterPosition() != 0) {
            // Для неверхних карточек блокируем любой свайп
            viewHolder.itemView.setTranslationX(0);
            viewHolder.itemView.setTranslationY(0);
            return;
        }

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

        if (isSwiping) return;

        float threshold = viewHolder.itemView.getWidth() * 0.35f;
        float progress = Math.min(1f, Math.abs(dX) / threshold);

        if (isCurrentlyActive && Math.abs(dX) > 10) {
            int iconRes = dX < 0 ? R.drawable.krest : R.drawable.heart;
            swipeIndicator.setImageResource(iconRes);
            swipeIndicator.setVisibility(View.VISIBLE);

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

    // Блокируем свайп для неверхних карточек
    @Override
    public int getSwipeDirs(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        if (viewHolder.getBindingAdapterPosition() == 0) {
            return super.getSwipeDirs(recyclerView, viewHolder);
        }
        return 0; // Запрещаем свайп для всех кроме позиции 0
    }

    private void showSwipeIconWithDelay(int iconRes, Runnable onComplete) {
        swipeIndicator.setImageResource(iconRes);
        swipeIndicator.setVisibility(View.VISIBLE);
        swipeIndicator.setScaleX(0.6f);
        swipeIndicator.setScaleY(0.6f);
        swipeIndicator.setAlpha(0f);

        swipeIndicator.animate()
                .alpha(1f)
                .scaleX(1.2f)
                .scaleY(1.2f)
                .setDuration(300)
                .withEndAction(() -> {
                    swipeIndicator.animate()
                            .scaleX(1f)
                            .scaleY(1f)
                            .setDuration(200)
                            .withEndAction(() -> {
                                swipeIndicator.animate()
                                        .alpha(0f)
                                        .setDuration(200)
                                        .withEndAction(() -> {
                                            swipeIndicator.setVisibility(View.GONE);
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
        return 0.5f;
    }

    @Override
    public float getSwipeEscapeVelocity(float defaultValue) {
        return defaultValue * 0.5f;
    }
}
