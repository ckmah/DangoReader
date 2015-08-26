package com.william.mangoreader.listener;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;

import com.william.mangoreader.activity.viewpager.MangaViewPager;

public class MVPGestureListener implements GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener {

    private Context context;
    private Window window;
    private MangaViewPager viewPager;

    private boolean systemUIVisible;

    private static final float LEFT_SIDE = 0.33f;
    private static final float RIGHT_SIDE = 0.66f;

    public MVPGestureListener(Context context, Window window, ViewPager viewPager) {
        systemUIVisible = false;
        this.context = context;
        this.window = window;
        this.viewPager = (MangaViewPager) viewPager;
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
    public boolean onSingleTapConfirmed(MotionEvent event) {
        Log.d("GestureListener", "Single tap confirmed.");
        float xPos = event.getX();
        float screenWidth = context.getResources().getDisplayMetrics().widthPixels;

        if (xPos < LEFT_SIDE * screenWidth) {
            viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);

            // TODO go back one page/chapter
        } else if (xPos > RIGHT_SIDE * screenWidth) {
            viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);

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
        return true;
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
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