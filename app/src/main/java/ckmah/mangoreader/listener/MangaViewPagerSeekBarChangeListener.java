package ckmah.mangoreader.listener;

import android.support.v4.view.ViewPager;
import android.util.Log;
import android.widget.SeekBar;

import ckmah.mangoreader.seekbar.ReversibleSeekBar;


public class MangaViewPagerSeekBarChangeListener extends ViewPager.SimpleOnPageChangeListener implements SeekBar.OnSeekBarChangeListener {

    private final ReversibleSeekBar seekBar;
    private ViewPager viewPager;
    private int progress = 0;
    private int lastPageIndex;

    /**
     * Constructor
     *
     * @param viewPager - Pager to track
     * @param seekBar   - SeekBar to synchronize with ViewPager
     */
    public MangaViewPagerSeekBarChangeListener(ViewPager viewPager, ReversibleSeekBar seekBar) {
        this.viewPager = viewPager;
        this.seekBar = seekBar;
    }

    public void updateMax() {
        lastPageIndex = seekBar.getMax() - 1;
    }

    /**
     * TODO Listens to seekbar; shows page counter when being dragged.
     *
     * @param seekBar
     * @param progressValue
     * @param b
     */
    @Override
    public void onProgressChanged(SeekBar seekBar, int progressValue, boolean b) {
        progress = progressValue;
        Log.d("SEEKBAR", "progress: " + progressValue + "/" + lastPageIndex);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    /**
     * Listens to seekbar; shows correct page after dragging finishes.
     *
     * @param seekBar
     */
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        int currentItem = this.seekBar.isLeftToRight() ? progress : lastPageIndex - progress;
        viewPager.setCurrentItem(currentItem, false);
    }

    /**
     * Listens to viewpager; moves seekbar to indicate correct page.
     *
     * @param position
     */
    @Override
    public void onPageSelected(int position) {
        progress = position;
        Log.d("SeekBarChangeListener", "onPageSelected " + position);
        if (this.seekBar.isLeftToRight()) {
            seekBar.setProgress(progress);
        } else {
            seekBar.setProgress(lastPageIndex - progress);
        }
    }
}
