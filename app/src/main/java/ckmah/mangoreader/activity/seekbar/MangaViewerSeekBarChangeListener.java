package ckmah.mangoreader.activity.seekbar;

import android.support.v4.view.ViewPager;
import android.util.Log;
import android.widget.SeekBar;

/**
 * Created by Clarence on 12/19/2015.
 */
public class MangaViewerSeekBarChangeListener extends ViewPager.SimpleOnPageChangeListener implements SeekBar.OnSeekBarChangeListener {

    private final ReversibleSeekBar seekBar;
    private ViewPager viewPager;
    private int progress = 0;
    private boolean isLeftToRight;

    /**
     * Constructor
     *
     * @param viewPager - Pager to track
     * @param seekBar   - SeekBar to synchronize with ViewPager
     */
    public MangaViewerSeekBarChangeListener(ViewPager viewPager, ReversibleSeekBar seekBar) {
        this.viewPager = viewPager;
        this.seekBar = seekBar;
    }

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
        if (this.seekBar.getisLeftToRight()) {
            viewPager.setCurrentItem(progress, false);
        } else {
            viewPager.setCurrentItem(this.seekBar.getMax() - progress, false);
        }
    }

    @Override
    public void onPageSelected(int position) {
        progress = position;
        Log.d("SeekBarChangeListener", "onPageSelected " + position);
        if (this.seekBar.getisLeftToRight()) {
            seekBar.setProgress(progress);
        } else {
            seekBar.setProgress(seekBar.getMax() - progress);
        }
    }
}
