package com.william.mangoreader.listener;

import android.view.ScaleGestureDetector;

/**
 * Created by Clarence on 8/22/2015.
 */
public class MVPScaleGestureListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {

    @Override
    public boolean onScale(ScaleGestureDetector detector) {
        super.onScale(detector);
        return false;
    }
}
