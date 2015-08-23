package com.william.mangoreader.activity.viewpager;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;

import com.william.mangoreader.listener.MVPGestureListener;
import com.william.mangoreader.model.MangaEdenImageItem;

import java.util.ArrayList;

public class MangaViewPager extends ViewPager {

    private Context context;
    private Window window;

    private GestureDetector gestureDetector;
//    private ScaleGestureDetector scaleGestureDetector;

    private ArrayList<MangaEdenImageItem> mangaPages;

    private boolean systemUIVisible;

    private static final float LEFT_SIDE = 0.2f;
    private static final float RIGHT_SIDE = 0.8f;

    private static final float TOLERANCE = 100;

    private static float pointX;
    private static float pointY;

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
        systemUIVisible = false;
        gestureDetector = new GestureDetector(context, new MVPGestureListener(context, window, this));
//        scaleGestureDetector = new ScaleGestureDetector(context, new MVPScaleGestureListener());

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
//        boolean intercepted = super.onInterceptTouchEvent(event);
//
//        switch (event.getAction()) {
//            case MotionEvent.ACTION_DOWN:
//                pointX = event.getX();
//                pointY = event.getY();
//                break;
//            case MotionEvent.ACTION_MOVE:
//                return true;
//
//        }
        // viewpager handles actions only if detectors don't handle event
//        if (gestureDetector.onTouchEvent(event)) {
//            return true;
//        }
        gestureDetector.onTouchEvent(event);
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
//        switch (event.getAction()) {
//            case MotionEvent.ACTION_UP:
//                boolean sameX = pointX + TOLERANCE > event.getX() && pointX - TOLERANCE < event.getX();
//                boolean sameY = pointY + TOLERANCE > event.getY() && pointY - TOLERANCE < event.getY();
//                float screenWidth = context.getResources().getDisplayMetrics().widthPixels;
//
//                Log.d("POSITIONX", "" + pointX + ", " + event.getX());
//                Log.d("POSITIONY", "" + pointY + ", " + event.getY());
//
//                if (sameX && sameY) {
//                    if (pointX < LEFT_SIDE * screenWidth) {
//                        setCurrentItem(getCurrentItem() - 1);
//                        // TODO go back one page/chapter
//                    } else if (pointX > RIGHT_SIDE * screenWidth) {
//                        setCurrentItem(getCurrentItem() + 1);
//                        // TODO go forward one page/chapter
//                    } else {
//                        if (systemUIVisible) {
//                            hideSystemUI();
//                            systemUIVisible = false;
//                        } else {
//                            showSystemUI();
//                            systemUIVisible = true;
//                        }
//                    }
//                }
//        }
        return super.onTouchEvent(event);
    }

    private void showSystemUI() {
        Log.d("MANGOREADER", "Show system UI.");
        window.getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

    private void hideSystemUI() {
        Log.d("MANGOREADER", "Hide system UI.");
        window.getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);
    }

}
