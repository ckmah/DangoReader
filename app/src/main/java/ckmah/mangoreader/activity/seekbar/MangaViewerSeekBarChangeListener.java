package ckmah.mangoreader.activity.seekbar;

import android.support.v4.view.ViewPager;
import android.util.Log;
import android.widget.SeekBar;

/**
 * Created by Clarence on 12/19/2015.
 */
public class MangaViewerSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {

    private ViewPager viewPager;
    private int progress = 0;
    private boolean isLeftToRight;

    @Override
    public void onProgressChanged(SeekBar seekBar, int progressValue, boolean b) {
        progress = progressValue;
        Log.d("SEEKBAR", "progress: " + progressValue + "/" + seekBar.getMax());
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        ReversibleSeekBar bar = (ReversibleSeekBar) seekBar;
        if (bar.getisLeftToRight()) {
            viewPager.setCurrentItem(progress, false);
        } else {
            viewPager.setCurrentItem(seekBar.getMax() - progress, false);
        }
    }

    public MangaViewerSeekBarChangeListener(ViewPager viewPager) {
        this.viewPager = viewPager;
    }
}
