package ckmah.mangoreader.seekbar;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.SeekBar;

/**
 * Optional right-to-left SeekBar, flips touch and corresponding drawn elements of seekbar.
 */
public class ReversibleSeekBar extends SeekBar {
    private boolean isLeftToRight;


    public ReversibleSeekBar(Context context) {
        super(context);
    }

    public ReversibleSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ReversibleSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ReversibleSeekBar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        float px = this.getWidth() / 2.0f;
        float py = this.getHeight() / 2.0f;

        if (isLeftToRight) {
            canvas.scale(1, -1, px, py);
        } else {
            canvas.scale(-1, 1, px, py);
        }
        super.onDraw(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isLeftToRight) {
            event.setLocation(this.getWidth() - event.getX(), event.getY());
        }

        return super.onTouchEvent(event);
    }


    public void setLeftToRight(boolean leftToRight) {
        this.isLeftToRight = leftToRight;
    }

    public boolean isLeftToRight() {
        return isLeftToRight;
    }
}