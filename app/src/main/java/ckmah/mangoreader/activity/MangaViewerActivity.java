package ckmah.mangoreader.activity;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.william.mangoreader.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ckmah.mangoreader.adapter.MangaImagePagerAdapter;
import ckmah.mangoreader.animation.AnimationHelper;
import ckmah.mangoreader.listener.MVPGestureListener;
import ckmah.mangoreader.listener.MangaViewPagerSeekBarChangeListener;
import ckmah.mangoreader.model.MangaEdenImageItem;
import ckmah.mangoreader.parse.MangaEden;
import ckmah.mangoreader.seekbar.ReversibleSeekBar;
import ckmah.mangoreader.viewpager.MangaViewPager;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class MangaViewerActivity extends AppCompatActivity {

    // Constants for animating toolbar positions.
    private static float LAYOUT_HEIGHT;
    private static float SEEKBAR_YPOS;
    private static float STATUS_BAR_YPOS;

    private AnimationHelper animationHelper = new AnimationHelper();

    private Toolbar mToolbar;
    private Toolbar seekBarToolBar;

    private MangaViewPager mangaViewPager;
    private MangaImagePagerAdapter imageAdapter;
    private MVPGestureListener gestureListener;

    private ReversibleSeekBar seekBar;
    private MangaViewPagerSeekBarChangeListener mangaViewPagerSeekBarChangeListener;

    private SharedPreferences sharedPref;

    private List<MangaEdenImageItem> images; // holds requested images for single chapter
    private String mangaTitle;
    private int chapterIndex;
    private int chapterTotalSize;
    private ArrayList<String> chapterIds, chapterTitles;

    private boolean readLeftToRight; // true - left to right; false - right to left
    private TextView leftBubble;
    private TextView rightBubble;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manga_viewer);

        // black notification bar
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.black));
        }

        // set position constants for toolbar animation
        LAYOUT_HEIGHT = getApplicationContext().getResources().getDisplayMetrics().heightPixels;
        STATUS_BAR_YPOS = (float) Math.ceil(25 * getResources().getDisplayMetrics().density);
        SEEKBAR_YPOS = (float) Math.ceil(LAYOUT_HEIGHT - (32 * getResources().getDisplayMetrics().density));

        images = new ArrayList<>();

        // initialize views
        mToolbar = (Toolbar) findViewById(R.id.toolbar_manga_viewer);
        seekBarToolBar = (Toolbar) findViewById(R.id.toolbar_seekbar);
        seekBar = (ReversibleSeekBar) findViewById(R.id.seekBar);
        mangaViewPager = (MangaViewPager) findViewById(R.id.manga_view_pager);
        leftBubble = (TextView) findViewById(R.id.left_bubble);
        rightBubble = (TextView) findViewById(R.id.right_bubble);
        ImageButton leftChapterButton = (ImageButton) findViewById(R.id.left_chapter);
        ImageButton rightChapterButton = (ImageButton) findViewById(R.id.right_chapter);

        // buttons change chapters according to read direction
        leftChapterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (readLeftToRight) {
                    prevChapter();
                } else {
                    nextChapter();
                }
            }
        });
        rightChapterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (readLeftToRight) {
                    nextChapter();
                } else {
                    prevChapter();
                }
            }
        });

        TextView pageNumberView = (TextView) findViewById(R.id.page_number);
        final FrameLayout uiLayout = (FrameLayout) findViewById(R.id.ui_layout);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        seekBarToolBar.setContentInsetsAbsolute(0, 0);
        mangaViewPager.activity = this;

        mangaViewPagerSeekBarChangeListener = new MangaViewPagerSeekBarChangeListener(mangaViewPager, seekBar, pageNumberView);

        // assign gestureListener to handle showing/hiding UI along with other gesture actions
        gestureListener = new MVPGestureListener(this, mangaViewPager, true) {

            @Override
            public void hideSystemUI() {
                animationHelper.fadeOut(uiLayout);
                getWindow().getDecorView().setSystemUiVisibility( // hide system UI
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                                | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                                | View.SYSTEM_UI_FLAG_IMMERSIVE);
            }

            @Override
            public void showSystemUI() {
                animationHelper.fadeIn(uiLayout);
                getWindow().getDecorView().setSystemUiVisibility( // show system UI
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            }
        };

        // set all listeners
        seekBar.setOnSeekBarChangeListener(mangaViewPagerSeekBarChangeListener);
        mangaViewPager.addOnPageChangeListener(mangaViewPagerSeekBarChangeListener);
        mangaViewPager.setMVPGestureListener(gestureListener);

        // pass in reading direction
        gestureListener.setLeftToRight(readLeftToRight);
        mangaViewPager.setLeftToRight(readLeftToRight);

        // read in manga and chapter data
        mangaTitle = getIntent().getExtras().getString("mangaTitle");
        chapterIndex = getIntent().getExtras().getInt("chapterIndex");
        chapterIds = getIntent().getExtras().getStringArrayList("chapterIds");
        chapterTitles = getIntent().getExtras().getStringArrayList("chapterTitles");
        chapterTotalSize = chapterIds != null ? chapterIds.size() : 0;

        // read in global settings
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        readLeftToRight = sharedPref.getBoolean(getString(R.string.PREF_KEY_READ_DIRECTION), false);
        boolean showPageNumbers = sharedPref.getBoolean(getString(R.string.PREF_KEY_PAGE_NUMBERS), false);

        displayChapter();
    }

    private void displayChapter() {
        // chapter title set to manga name and chapter #
        getSupportActionBar().setTitle(mangaTitle + " - Ch. " + getChapterTitle());

        // TODO show progressbar or loading indicator

        // Fetch the chapter images in the background
        String chapterId = chapterIds.get(chapterIndex);
        MangaEden.getMangaEdenService(this).getMangaImages(chapterId).enqueue(new Callback<MangaEden.MangaEdenChapter>() {
            @Override
            public void onResponse(Response<MangaEden.MangaEdenChapter> response, Retrofit retrofit) {
                images = response.body().images;
                imageAdapter = new MangaImagePagerAdapter(getSupportFragmentManager(), images.size());
                mangaViewPager.setAdapter(imageAdapter);

                seekBar.setMax(imageAdapter.getCount() - 1);
                seekBar.setLeftToRight(readLeftToRight);

                Log.d("DisplayChapter", "total pages: " + (imageAdapter.getCount() - 1));

                if (readLeftToRight) {
                    Collections.reverse(images);
                    imageAdapter.notifyDataSetChanged();
                    mangaViewPager.setCurrentItem(0, false);
                    Log.d("DisplayChapter", "showing fragment " + 0);
                } else {
                    mangaViewPager.setCurrentItem(images.size() - 1, false);
                    Log.d("DisplayChapter", "showing fragment " + (images.size() - 1));
                }

                seekBar.refresh();
                mangaViewPagerSeekBarChangeListener.setTotalPages(images.size());
                mangaViewPagerSeekBarChangeListener.onPageSelected(mangaViewPager.getCurrentItem());
            }

            @Override
            public void onFailure(Throwable t) {

            }
        });

    }

    /**
     * Navigates to next available chapter.
     *
     * @return Returns chapter number. Returns -1 if no next chapter.
     */
    public int nextChapter() {
        Toast toast;
        if (chapterIndex == chapterTotalSize - 1) {
            // handle last chapter
            toast = Toast.makeText(this, "There are no more chapters available. This is the last chapter.", Toast.LENGTH_LONG);
            toast.show();

            return -1;
        } else {
            chapterIndex++;
            toast = Toast.makeText(this, "Chapter " + getChapterTitle(), Toast.LENGTH_SHORT);
            toast.show();
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
        Toast toast;
        if (chapterIndex == 0) {
            // handle first chapter
            toast = Toast.makeText(this, "No more previous chapters.", Toast.LENGTH_LONG);
            toast.show();
            return -1;
        } else {
            chapterIndex--;
            toast = Toast.makeText(this, "Chapter " + getChapterTitle(), Toast.LENGTH_SHORT);
            toast.show();
            displayChapter();
            return chapterIndex;
        }
    }

    public void reverseReadingDirection() {
        mangaViewPager.setLeftToRight(readLeftToRight);
        gestureListener.setLeftToRight(readLeftToRight);
        seekBar.setLeftToRight(readLeftToRight);
        if (readLeftToRight) {
            leftBubble.setText(R.string.back);
            rightBubble.setText(R.string.next);
        }
        else {
            leftBubble.setText(R.string.next);
            rightBubble.setText(R.string.back);
        }
        Collections.reverse(images);
        imageAdapter.notifyDataSetChanged();
        mangaViewPager.setCurrentItem(images.size() - 1 - mangaViewPager.getCurrentItem(), false);
        mangaViewPagerSeekBarChangeListener.onPageSelected(mangaViewPager.getCurrentItem());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_manga_viewer, menu);
        MenuItem toggleLeftRight = menu.findItem(R.id.toggle_left_right);
        toggleLeftRight.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                readLeftToRight = !readLeftToRight;
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putBoolean(getString(R.string.PREF_KEY_READ_DIRECTION), readLeftToRight);
                editor.apply();
                reverseReadingDirection();
                String toast = "";
                if (readLeftToRight) {
                    toast = "Reading left to right";
                } else {
                    toast = "Reading right to left";
                }
                Toast.makeText(MangaViewerActivity.this, toast, Toast.LENGTH_SHORT).show();

                return true;
            }
        });

        return true;
    }

    /**
     * Changes pages from the volume buttons
     * TODO make optional, perhaps reverse direction based on read direction
     *
     * From http://stackoverflow.com/a/2875006/1222351
     */
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int action = event.getAction();
        int keyCode = event.getKeyCode();
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
                if (action == KeyEvent.ACTION_DOWN) {
                    mangaViewPager.previousPage();
                }
                return true;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                if (action == KeyEvent.ACTION_DOWN) {
                    mangaViewPager.nextPage();
                }
                return true;
            default:
                return super.dispatchKeyEvent(event);
        }
    }

    private String getChapterTitle() {
        return chapterTitles.get(chapterIndex);
    }

    public List<MangaEdenImageItem> getImages() {
        return images;
    }

}
