package ckmah.mangoreader.activity.viewpager;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import ckmah.mangoreader.activity.MangaViewerActivity;
import ckmah.mangoreader.listener.MVPGestureListener;
import it.sephiroth.android.library.imagezoom.ImageViewTouch;

public class MangaViewPager extends ViewPager {

    public MangaViewerActivity activity; // THESE CIRCULAR DEPENDENCIES THO
    private GestureDetector gestureDetector;
    private Context context;

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

    // Callback, for viewpager to advance either the page or the chapter
    // TODO trigger this on swipe in addition to touch
    public void onNext() {
        if (getCurrentItem() == 0) {
            // Currently on the last page, move to next chapter
            Toast toast;
            int chapter = activity.nextChapter();

            // reached last chapter, do nothing
            if (chapter == -1) {
                toast = Toast.makeText(activity, "There are no more chapters available. This is the last chapter.", Toast.LENGTH_LONG);
                toast.show();
                return;
            }
            toast = Toast.makeText(activity, "Chapter " + chapter, Toast.LENGTH_SHORT);
            toast.show();
        } else {
            // Move on to the next page
            setCurrentItem(getCurrentItem() - 1);
        }
    }

    public void onPrevious() {
        Log.d("MVPAGER", "currentItemIndex" + getCurrentItem());
        Log.d("MVPAGER", "childCount" + getAdapter().getCount());
        if (getCurrentItem() == getAdapter().getCount() - 1) {
            // Currently on first page, move to previous chapter
            Toast toast;
            int chapter = activity.prevChapter();

            if (chapter == -1) {
                // reached first chapter
                toast = Toast.makeText(activity, "No more previous chapters.", Toast.LENGTH_LONG);
                toast.show();
                return;
            }
            toast = Toast.makeText(activity, "Chapter " + chapter, Toast.LENGTH_SHORT);
            toast.show();
        } else {
            // Move on to previous page
            setCurrentItem(getCurrentItem() + 1);
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
