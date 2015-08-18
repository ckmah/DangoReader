package com.william.mangoreader.activity.viewpager;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.Window;

import com.william.mangoreader.listener.MangaViewPagerListener;
import com.william.mangoreader.model.MangaEdenImageItem;

import java.util.ArrayList;

public class MangaViewPager extends ViewPager {

    private Context context;
    private Window window;

    private GestureDetector gestureDetector;

    private ArrayList<MangaEdenImageItem> mangaPages;

    private boolean systemUIVisible;

    private static final float LEFT_SIDE = 0.33f;
    private static final float RIGHT_SIDE = 0.66f;

    private float mLastX;
    private float mLastY;
    private float mStartX;
    private boolean mIsBeingDragged;

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
        window = ((AppCompatActivity) context).getWindow();
        systemUIVisible = true;
        gestureDetector = new GestureDetector(context, new MangaViewPagerListener(context, window));

        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                | View.SYSTEM_UI_FLAG_IMMERSIVE);

        mangaPages = new ArrayList<>();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        gestureDetector.onTouchEvent(event);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastX = event.getX();
                mLastY = event.getY();
                mStartX = mLastX;
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                mIsBeingDragged = false;
                break;
            case MotionEvent.ACTION_MOVE:
                float x = event.getX();
                float y = event.getY();
                float xDelta = Math.abs(x - mLastX);
                float yDelta = Math.abs(y - mLastY);

                float xDeltaTotal = x - mStartX;

                // scroll if pass threshold
                if (xDelta > yDelta && Math.abs(xDeltaTotal) > ViewConfiguration.get(context).getScaledTouchSlop()) {
                    mIsBeingDragged = true;
                    mStartX = x;
                    return true;
                }
                break;
        }
        return false;
        // return true if this ViewGroup handles touch event
        // return false if child handles touch event
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        gestureDetector.onTouchEvent(event);

//        switch (event.getAction()) {
//            case MotionEvent.ACTION_CANCEL:
//            case MotionEvent.ACTION_UP:
//                mIsBeingDragged = false;
//                break;
//            case MotionEvent.ACTION_MOVE:
//                float x = event.getX();
//                float y = event.getY();
//
//                float xDelta = Math.abs(x - mLastX);
//                float yDelta = Math.abs(y - mLastY);
//
//                float xDeltaTotal = x - mStartX;
//                if (!mIsBeingDragged && yDelta > xDelta && Math.abs(xDeltaTotal) > ViewConfiguration.get(context).getScaledTouchSlop()) {
//                    mIsBeingDragged = true;
//                    mStartX = x;
//                    xDeltaTotal = 0;
//                }
//                if (xDeltaTotal < 0)
//                    xDeltaTotal = 0;
//
//                if (mIsBeingDragged) {
//                    scrollTo(0, (int) xDeltaTotal);
//                }
//
//                mLastX = x;
//                mLastY = y;
//                break;
//        }

        return true;
    }
}
