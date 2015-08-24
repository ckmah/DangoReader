package com.william.mangoreader.listener;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;

public class MVPGestureListener extends GestureDetector.SimpleOnGestureListener {

    private Context context;
    private Window window;
    private ViewPager viewPager;

    private boolean systemUIVisible;

    private static final float LEFT_SIDE = 0.33f;
    private static final float RIGHT_SIDE = 0.66f;

    public MVPGestureListener(Context context, Window window, ViewPager viewPager) {
        systemUIVisible = false;
        this.context = context;
        this.window = window;
        this.viewPager = viewPager;
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
        return false;
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                           float velocityY) {
        viewPager.onTouchEvent(e1);
        viewPager.onTouchEvent(e2);
        return true;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        viewPager.onTouchEvent(e1);
        viewPager.onTouchEvent(e2);
        return true;
    }

}