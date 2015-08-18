package com.william.mangoreader.listener;

import android.content.Context;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;

public class MangaViewPagerListener implements GestureDetector.OnGestureListener {

    private Context context;
    private Window window;
    private boolean systemUIVisible;

    private static final float LEFT_SIDE = 0.33f;
    private static final float RIGHT_SIDE = 0.66f;

    public MangaViewPagerListener(Context context, Window window) {
        systemUIVisible = false;
        this.context = context;
        this.window = window;
    }

    private void showSystemUI() {
        window.getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

    private void hideSystemUI() {
        window.getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent event) {
        float xPos = event.getX();
        float screenWidth = context.getResources().getDisplayMetrics().widthPixels;

        Log.d("POSITION", "" + xPos);


        if (xPos < LEFT_SIDE * screenWidth) {
            // TODO go back one page/chapter
        } else if (xPos > RIGHT_SIDE * screenWidth) {
            // TODO go forward one page/chapter
        } else {
            if (systemUIVisible) {
                hideSystemUI();
                systemUIVisible = false;
            } else {
                showSystemUI();
                systemUIVisible = true;
            }
        }
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }
}