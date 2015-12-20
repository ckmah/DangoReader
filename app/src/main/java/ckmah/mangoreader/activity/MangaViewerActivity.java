package ckmah.mangoreader.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.SeekBar;

import com.william.mangoreader.R;

import java.util.ArrayList;
import java.util.List;

import ckmah.mangoreader.activity.seekbar.MangaViewerSeekBarChangeListener;
import ckmah.mangoreader.activity.seekbar.ReversibleSeekBar;
import ckmah.mangoreader.activity.viewpager.MangaViewPager;
import ckmah.mangoreader.adapter.MangaImagePagerAdapter;
import ckmah.mangoreader.listener.MVPGestureListener;
import ckmah.mangoreader.model.MangaEdenImageItem;
import ckmah.mangoreader.parse.MangaEden;
import retrofit.Callback;
import retrofit.RetrofitError;

public class MangaViewerActivity extends AppCompatActivity {

    private static float STATUS_BAR_HEIGHT;
    private Toolbar mToolbar;
    private MangaViewPager mangaViewPager;

    private List<MangaEdenImageItem> images;
    private MangaImagePagerAdapter imageAdapter;

    private int chapterIndex;
    private int chapterTotalSize;
    private ArrayList<String> chapterIds, chapterTitles;

    // shared preferences
    private boolean readLeftToRight;
    private boolean showPageNumbers;

    private ReversibleSeekBar seekBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manga_viewer);

        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.translucent_black));
        }
        STATUS_BAR_HEIGHT = (float) Math.ceil(25 * getResources().getDisplayMetrics().density);

        mToolbar = (Toolbar) findViewById(R.id.toolbar_manga_viewer);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mToolbar.setVisibility(View.GONE);

        images = new ArrayList<>();

        mangaViewPager = (MangaViewPager) findViewById(R.id.manga_view_pager);
        mangaViewPager.activity = this;
        MVPGestureListener gestureListener = new MVPGestureListener(this, mangaViewPager) {
            @Override
            public void hideSystemUI() {
                mToolbar.animate()
                        .y(STATUS_BAR_HEIGHT)
                        .translationY(-1 * mToolbar.getHeight())
                        .setDuration(getResources().getInteger(android.R.integer.config_shortAnimTime))
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                mToolbar.setVisibility(View.GONE);
                            }
                        });
                getWindow().getDecorView().setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                                | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                                | View.SYSTEM_UI_FLAG_IMMERSIVE);
            }

            @Override
            public void showSystemUI() {
                mToolbar.setVisibility(View.VISIBLE);
                mToolbar.animate()
                        .translationY(mToolbar.getHeight())
                        .y(STATUS_BAR_HEIGHT)
                        .setDuration(getResources().getInteger(android.R.integer.config_shortAnimTime))
                        .setListener(null);

                getWindow().getDecorView().setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            }
        };
        mangaViewPager.setMVPGestureListener(gestureListener);

        chapterIndex = getIntent().getExtras().getInt("chapterIndex");
        chapterIds = getIntent().getExtras().getStringArrayList("chapterIds");
        chapterTitles = getIntent().getExtras().getStringArrayList("chapterTitles");
        chapterTotalSize = chapterIds.size();

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        readLeftToRight = sharedPref.getBoolean(getString(R.string.PREF_KEY_READ_DIRECTION), false);
        showPageNumbers = sharedPref.getBoolean(getString(R.string.PREF_KEY_PAGE_NUMBERS), false);
        Log.d("MVACTIVITY", "read left to right?" + readLeftToRight);
        displayChapter();

        seekBar = (ReversibleSeekBar) findViewById(R.id.seekBar);
        seekBar.setisLeftToRight(readLeftToRight);
        SeekBar.OnSeekBarChangeListener mangaViewerSeekBarChangeListener = new MangaViewerSeekBarChangeListener(mangaViewPager);
        seekBar.setOnSeekBarChangeListener(mangaViewerSeekBarChangeListener);
    }

    private void displayChapter() {
        // Set the chapter title accordingly
        String chapterTitle = chapterTitles.get(chapterIndex);
        getSupportActionBar().setTitle(chapterTitle);

        // TODO show progressbar or loading indicator

        // Fetch the chapter images in the background
        String chapterId = chapterIds.get(chapterIndex);
        MangaEden.getMangaEdenService(this).getMangaImages(chapterId, new Callback<MangaEden.MangaEdenChapter>() {
            @Override
            public void success(MangaEden.MangaEdenChapter chapter, retrofit.client.Response response) {
                images = chapter.images;
                imageAdapter = new MangaImagePagerAdapter(getSupportFragmentManager(), images.size());
                mangaViewPager.setAdapter(imageAdapter);

                seekBar.setMax(imageAdapter.getCount() - 1);

                // Show the very first page
                mangaViewPager.setCurrentItem(images.size() - 1);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e("ERROR", error.getMessage());
            }
        });
    }

    /**
     * Navigates to next available chapter.
     *
     * @return Returns chapter number. Returns -1 if no next chapter.
     */
    public int nextChapter() {
        if (chapterIndex == chapterTotalSize - 1) {
            // handle last chapter
            return -1;
        } else {
            chapterIndex++;
            displayChapter();
            return chapterIndex;
        }
    }

    /**
     * Navigates to previous chapter if available.
     *
     * @return Returns chapter number. Returns -1 if no previous chapter.
     */
    public int prevChapter() {
        if (chapterIndex == 0) {
            // handle first chapter
            return -1;
        } else {
            Log.d("MVACTIVITY", "PREVIOUS CHAPTER");
            chapterIndex--;
            displayChapter();
            return chapterIndex;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_manga_viewer, menu);
        return true;
    }

    public List<MangaEdenImageItem> getImages() {
        return images;
    }

}
