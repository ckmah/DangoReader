package ckmah.mangoreader.viewpager;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import ckmah.mangoreader.activity.MangaViewerActivity;
import ckmah.mangoreader.listener.MVPGestureListener;
import it.sephiroth.android.library.imagezoom.ImageViewTouch;

public class MangaViewPager extends ViewPager {

    public MangaViewerActivity activity; // THESE CIRCULAR DEPENDENCIES THO
    private GestureDetector gestureDetector;
    private Context context;
    private boolean leftToRight;

    public MangaViewPager(Context context) {
        super(context);
        init(context);
    }

    public MangaViewPager(Context context, AttributeSet attr) {
        super(context, attr);
        init(context);
    }

    private void init(Context context) {
        this.context = context;
        ((AppCompatActivity) context).getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
//                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
//                | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
//                | View.SYSTEM_UI_FLAG_IMMERSIVE);
    }

    @Override
    protected boolean canScroll(View v, boolean checkV, int dx, int x, int y) {
        if (v instanceof ImageViewTouch) {
            boolean scroll = ((ImageViewTouch) v).canScroll(dx);
            return scroll;
        } else {
            boolean scroll = super.canScroll(v, checkV, dx, x, y);
            return scroll;
        }
    }

    // Callback, for viewpager to advance either the page or the chapter
    // TODO trigger this on swipe in addition to touch
    public void nextPage() {
        if (leftToRight) {
            if (getCurrentItem() < getAdapter().getCount() - 1) {
                // go to next page
                setCurrentItem(getCurrentItem() + 1);
            } else {
                // next chapter
                activity.nextChapter();
            }
        } else {
            if (getCurrentItem() > 0) {
                // go to next page
                setCurrentItem(getCurrentItem() - 1);
            } else {
                // next chapter
                activity.nextChapter();
            }
        }
    }

    public void previousPage() {
        if (leftToRight) {
            if (getCurrentItem() > 0) {
                // previous page
                setCurrentItem(getCurrentItem() - 1);
            } else {
                // previous chapter
                activity.prevChapter();
            }

        } else {
            if (getCurrentItem() < getAdapter().getCount() - 1) {
//                previous page
                setCurrentItem(getCurrentItem() + 1);
            } else {
                // previous chapter
                activity.prevChapter();
            }
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        return super.onInterceptTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    public void setMVPGestureListener(MVPGestureListener MVPGestureListener) {
        gestureDetector = new GestureDetector(context, MVPGestureListener);
    }

    public boolean isLeftToRight() {
        return leftToRight;
    }

    public void setLeftToRight(boolean leftToRight) {
        this.leftToRight = leftToRight;
    }
}
