package com.william.mangoreader.activity.viewpager;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.Window;

import com.william.mangoreader.listener.MVPGestureListener;

import it.sephiroth.android.library.imagezoom.ImageViewTouch;

public class MangaViewPager extends ViewPager {

    private GestureDetector gestureDetector;
    private ViewConfiguration vc;
    private Context context;

    private static final float SCROLL_TOLERANCE = 50f;

    private float startX;
    private float startY;
    private boolean isScrolling;
    private MVPGestureListener MVPGestureListener;

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
        Window window = ((AppCompatActivity) context).getWindow();
        vc = ViewConfiguration.get(context);
//        gestureDetector = new GestureDetector(context, new MVPGestureListener(context, this));
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                | View.SYSTEM_UI_FLAG_IMMERSIVE);
    }

    @Override
    protected boolean canScroll(View v, boolean checkV, int dx, int x, int y) {
        if (v instanceof ImageViewTouch) {
            boolean scroll = ((ImageViewTouch) v).canScroll(dx);
            Log.d("ViewPager", "ImageViewTouch canScroll: " + scroll);
            return scroll;
        } else {
            boolean scroll = super.canScroll(v, checkV, dx, x, y);
            Log.d("ViewPager", "ViewPager canScroll: " + scroll);
            return scroll;
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
}
