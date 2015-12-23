package ckmah.mangoreader.listener;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.view.GestureDetector;
import android.view.MotionEvent;

import ckmah.mangoreader.viewpager.MangaViewPager;

public class MVPGestureListener implements GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener {

    private Context context;
    private MangaViewPager viewPager;

    private boolean leftToRight;

    private boolean systemUIVisible;

    private static final float LEFT_SIDE = 0.33f;
    private static final float RIGHT_SIDE = 0.66f;

    public MVPGestureListener(Context context, ViewPager viewPager) {
        systemUIVisible = false;
        this.context = context;
        this.viewPager = (MangaViewPager) viewPager;
    }

    public void showSystemUI() {
    }

    public void hideSystemUI() {
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent event) {
        float xPos = event.getX();
        float screenWidth = context.getResources().getDisplayMetrics().widthPixels;

        if (xPos < LEFT_SIDE * screenWidth) {
            if (leftToRight) {
                viewPager.previousPage();
            } else {
                viewPager.nextPage();
            }
            // TODO go back one page/chapter
        } else if (xPos > RIGHT_SIDE * screenWidth) {
            if (leftToRight) {
                viewPager.nextPage();
            } else {
                viewPager.previousPage();
            }
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

    public boolean isLeftToRight() {
        return leftToRight;
    }

    public void setLeftToRight(boolean leftToRight) {
        this.leftToRight = leftToRight;
    }
}