package com.example.applik;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.util.List;

public class BulletShooter  {
    private Context context;
    private FrameLayout previewContainer;
    private List<ImageView> bulletList;

    public BulletShooter(Context context, FrameLayout previewContainer, List<ImageView> bulletList) {
        this.context = context;
        this.previewContainer = previewContainer;
        this.bulletList = bulletList;
    }

    public void fireBullet(float x, float y, int xbul, int ybul) {
        // Создаем новую ImageView для пули
        ImageView bulletView = new ImageView(context);
        bulletView.setImageResource(R.drawable.bullet);
        // Установите ресурс картинки пули
        bulletView.setLayoutParams(new FrameLayout.LayoutParams(xbul, ybul)); // Установите размер пули

        bulletView.setX(x);
        bulletView.setY(y);

        // Добавляем пулю в список пуль
        bulletList.add(bulletView);

        // Добавляем пулю на экран
        previewContainer.addView(bulletView);

        // Анимируем движение пули
        ObjectAnimator animator = ObjectAnimator.ofFloat(bulletView, "y", bulletView.getY(), -previewContainer.getHeight());
        animator.setDuration(1000);
        animator.setInterpolator(new LinearInterpolator());
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                // Удаляем пулю из списка пуль и из экрана
                bulletList.remove(bulletView);
                previewContainer.removeView(bulletView);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        animator.start();
    }


    public float getBulletX(int index) {
        if (index >= 0 && index < bulletList.size()) {
            return bulletList.get(index).getX();
        }
        return 0f;
    }

    public float getBulletY(int index) {
        if (index >= 0 && index < bulletList.size()) {
            return bulletList.get(index).getY();
        }
        return 0f;
    }
}
