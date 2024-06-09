package com.example.applik;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

public class PlaneView {
    private float planeX;
    private float planeY;
    private float planeSpeed;
    private ImageView planeView;
    private Context context;

    public PlaneView(Context context, ImageView planeView) {
        this.context = context;
        this.planeView = planeView;
        this.planeX = -getScreenWidth(context);
        this.planeY = (float) getScreenHeight(context) / 5;
        this.planeSpeed = 5.0f;
    }
    public void setPlaneY(float y) {
        this.planeY = y;
        planeView.setY(y);
    }
    public float getPlaneY() {
        return planeY;
    }

    public void startPlaneAnimation() {
        ObjectAnimator animator = ObjectAnimator.ofFloat(planeView, "x", planeX, getScreenWidth(context) );
        animator.setDuration(5000);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setInterpolator(new LinearInterpolator());
        animator.start();
    }

    private int getScreenWidth(Context context) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }

    private int getScreenHeight(Context context) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.heightPixels;
    }
}

