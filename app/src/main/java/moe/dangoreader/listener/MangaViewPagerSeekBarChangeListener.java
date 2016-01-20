package moe.dangoreader.listener;

import android.support.v4.view.ViewPager;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.TextView;

import moe.dangoreader.R;
import moe.dangoreader.activity.MangaViewerActivity;
import moe.dangoreader.seekbar.ReversibleSeekBar;
import moe.dangoreader.viewpager.MangaViewPager;

public class MangaViewPagerSeekBarChangeListener extends ViewPager.SimpleOnPageChangeListener implements SeekBar.OnSeekBarChangeListener {

    private MangaViewerActivity activity;
    private final ReversibleSeekBar seekBar;
    private MangaViewPager viewPager;
    private TextView pageNumberView;
    private int progress; // index of current viewpager page
    private int totalPages;

    /**
     * Constructor
     *
     * @param viewPager      - Pager to track
     * @param seekBar        - SeekBar to synchronize with ViewPager
     * @param pageNumberView
     */
    public MangaViewPagerSeekBarChangeListener(MangaViewerActivity activity, MangaViewPager viewPager, ReversibleSeekBar seekBar, TextView pageNumberView) {
        progress = 0;
        this.activity = activity;
        this.viewPager = viewPager;
        this.seekBar = seekBar;
        this.pageNumberView = pageNumberView;
    }

    public void setTotalPages(int numPages) {
        totalPages = numPages;
    }

    /**
     * @param seekBar
     * @param progressValue - index of position o
     * @param b
     */
    @Override
    public void onProgressChanged(SeekBar seekBar, int progressValue, boolean b) {
        progress = progressValue;
        ((TextView) pageNumberView.findViewById(R.id.page_number)).setText((progressValue + 1) + " / " + totalPages);
        Log.d("ProgressChanged", (progressValue + 1) + " / " + totalPages);
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
        int currentItem = this.seekBar.isLeftToRight() ? progress : totalPages - progress - 1;
        viewPager.setCurrentItem(currentItem, false);
    }

    /**
     * Listens to viewpager; moves seekbar to indicate correct page.
     *
     * @param position
     */
    @Override
    public void onPageSelected(int position) {
        String currentPageText;

        viewPager.setPageIndex(position);
        // flip seekbar progress as necessary
        if (this.seekBar.isLeftToRight()) {
            seekBar.setProgress(position);
            currentPageText = (position + 1) + " / " + totalPages;
            if (position + 1 == totalPages) {
                activity.markChapterRead();
            }
        } else {
            seekBar.setProgress(totalPages - position - 1);
            currentPageText = (totalPages - position) + " / " + totalPages;
            if (position == 0) {
                activity.markChapterRead();
            }
        }
        ((TextView) pageNumberView.findViewById(R.id.page_number)).setText(currentPageText);
        Log.d("SeekBarChangeListener", "onPageSelected " + position);
    }
}
