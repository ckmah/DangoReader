package ckmah.mangoreader.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Point;
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
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.william.mangoreader.R;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ckmah.mangoreader.UserLibraryHelper;
import ckmah.mangoreader.adapter.MangaImagePagerAdapter;
import ckmah.mangoreader.animation.AnimationHelper;
import ckmah.mangoreader.database.Chapter;
import ckmah.mangoreader.database.Manga;
import ckmah.mangoreader.listener.MangaViewPagerSeekBarChangeListener;
import ckmah.mangoreader.model.MangaEdenImageItem;
import ckmah.mangoreader.parse.MangaEden;
import ckmah.mangoreader.seekbar.ReversibleSeekBar;
import ckmah.mangoreader.viewpager.MangaViewPager;
import io.paperdb.Paper;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class MangaViewerActivity extends AppCompatActivity {
    // Constants
    private static final float LEFT_SIDE = 0.33f;
    private static final float RIGHT_SIDE = 0.66f;
    private final static String
            KEY_IMAGES = "images",
            KEY_MANGA_ID = "manga_title",
            KEY_CHAPTER_INDEX = "chapter_index";

    // Constants for animating toolbar positions.
    private static float LAYOUT_HEIGHT;
    private static float SEEKBAR_YPOS;
    private static float STATUS_BAR_YPOS;
    private static float SCALE;

    // Views and layouts
    private FrameLayout uiLayout;
    private Toolbar mToolbar;
    private Toolbar seekBarToolBar;
    private MangaViewPager mangaViewPager;
    private ReversibleSeekBar seekBar;
    private TextView leftBubble;
    private TextView rightBubble;

    // Other Android objects
    private AnimationHelper animationHelper = new AnimationHelper();
    private MangaImagePagerAdapter imageAdapter;
    private MangaViewPagerSeekBarChangeListener mangaViewPagerSeekBarChangeListener;
    private SharedPreferences sharedPref;

    // Actual data
    private Manga manga;
    private List<MangaEdenImageItem> images; // holds requested images for single chapter
    private List<Chapter> chapters;

    // Flags and other state
    private boolean isUIVisible;
    private int chapterTotalSize;
    private int chapterIndex;
    private boolean readLeftToRight; // true - left to right; false - right to left
    private boolean loading = false;

    /**
     * Start a viewer activity with the specified parameters
     */
    public static void start(Context context, String mangaId, int chapterIndex) {
        Intent intent = new Intent(context, MangaViewerActivity.class);
        intent.putExtra(KEY_MANGA_ID, mangaId);
        intent.putExtra(KEY_CHAPTER_INDEX, chapterIndex);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manga_viewer);

        Paper.init(this);
        // read in manga and chapter data
        String mangaId = getIntent().getExtras().getString(KEY_MANGA_ID);
        manga = Paper.book(UserLibraryHelper.USER_LIBRARY_DB).read(mangaId);
        chapters = manga.chaptersList;
        chapterIndex = getIntent().getExtras().getInt(KEY_CHAPTER_INDEX);
        chapterTotalSize = chapters != null ? chapters.size() : 0;

        // black notification bar
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.black));
        }

        // set position constants for toolbar animation
        LAYOUT_HEIGHT = getApplicationContext().getResources().getDisplayMetrics().heightPixels;
        STATUS_BAR_YPOS = (float) Math.ceil(25 * getResources().getDisplayMetrics().density);
        SEEKBAR_YPOS = (float) Math.ceil(LAYOUT_HEIGHT - (32 * getResources().getDisplayMetrics().density));
        SCALE = getResources().getDisplayMetrics().density;

        // Check whether we're recreating a previously destroyed instance
        if (savedInstanceState != null) {
            // Restore image list from saved state
            images = (ArrayList<MangaEdenImageItem>) savedInstanceState.getSerializable(KEY_IMAGES);
        } else {
            // Otherwise initialize empty list of images
            images = new ArrayList<>();
        }


        // initialize views
        mToolbar = (Toolbar) findViewById(R.id.toolbar_manga_viewer);
        seekBarToolBar = (Toolbar) findViewById(R.id.toolbar_seekbar);
        seekBar = (ReversibleSeekBar) findViewById(R.id.seekBar);
        uiLayout = (FrameLayout) findViewById(R.id.ui_layout);
        mangaViewPager = (MangaViewPager) findViewById(R.id.manga_view_pager);
        leftBubble = (TextView) findViewById(R.id.left_bubble);
        rightBubble = (TextView) findViewById(R.id.right_bubble);
        ImageButton leftChapterButton = (ImageButton) findViewById(R.id.left_chapter);
        ImageButton rightChapterButton = (ImageButton) findViewById(R.id.right_chapter);
        TextView pageNumberView = (TextView) findViewById(R.id.page_number);

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


        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        mToolbar.setTitle("");
        seekBarToolBar.setContentInsetsAbsolute(0, 0);
        mangaViewPager.activity = this;

        // read in global settings
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        readLeftToRight = sharedPref.getBoolean(getString(R.string.PREF_KEY_READ_DIRECTION), false);

        mangaViewPagerSeekBarChangeListener = new MangaViewPagerSeekBarChangeListener(this, mangaViewPager, seekBar, pageNumberView);

        // set all listeners
        seekBar.setOnSeekBarChangeListener(mangaViewPagerSeekBarChangeListener);
        mangaViewPager.addOnPageChangeListener(mangaViewPagerSeekBarChangeListener);

        // pass in reading direction
        mangaViewPager.setLeftToRight(readLeftToRight);

        isUIVisible = true;
        displayChapter();
    }

    @Override
    /**
     * Change layout responding to orientation change.
     */
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) seekBarToolBar.getLayoutParams();
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            p.setMargins(0, (int) (25 * SCALE), 0, 0);
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            p.setMargins(0, (int) (25 * SCALE), 0, (int) (48 * SCALE));

        }
    }


    private void displayChapter() {
        // chapter title set to manga name and chapter #. Example: "Ch. 1 - Naruto"
        String title = "Ch. " + getChapterNumber() + " - " + manga.title;
        ((TextView) findViewById(R.id.chapter_title)).setText(title);

        // TODO show progressbar or loading indicator
        loading = true;

        // Fetch the chapter images in the background
        String chapterId = chapters.get(chapterIndex).id;
        MangaEden.getMangaEdenService(this).getMangaImages(chapterId).enqueue(new Callback<MangaEden.MangaEdenChapter>() {
            @Override
            public void onResponse(Response<MangaEden.MangaEdenChapter> response, Retrofit retrofit) {
                images = response.body().images;
                if (readLeftToRight) {
                    Collections.reverse(images);
                    leftBubble.setText(R.string.back);
                    rightBubble.setText(R.string.next);

                } else {
                    leftBubble.setText(R.string.next);
                    rightBubble.setText(R.string.back);
                }

                imageAdapter = new MangaImagePagerAdapter(getSupportFragmentManager(), images.size());

                int currentPage;
                if (chapters.get(chapterIndex).mostRecentPage != -1) {
                    currentPage = chapters.get(chapterIndex).mostRecentPage;
                } else {
                    if (readLeftToRight) {
                        currentPage = 0;
                    } else {
                        currentPage = images.size() - 1;
                    }
                }

                mangaViewPager.setAdapter(imageAdapter);

                seekBar.setMax(imageAdapter.getCount() - 1);
                seekBar.setLeftToRight(readLeftToRight);

                Log.d("DisplayChapter", "total pages: " + (imageAdapter.getCount() - 1));

                mangaViewPager.setCurrentItem(currentPage, false);
                mangaViewPager.setPageIndex(currentPage);

                Log.d("DisplayChapter", "showing isBrowsing " + currentPage);

                seekBar.refresh();
                mangaViewPagerSeekBarChangeListener.setTotalPages(images.size());
                mangaViewPagerSeekBarChangeListener.onPageSelected(mangaViewPager.getCurrentItem());

                loading = false;

            }

            @Override
            public void onFailure(Throwable t) {
                Log.e("MangaViewerActivity", "Could not load chapter " + getChapterNumber());
            }
        });

    }

    /**
     * Navigates to next available chapter.
     */
    public void nextChapter() {
        if (loading)
            return;

        if (chapterIndex == chapterTotalSize - 1) {
            // Do nothing if already on last chapter
            Toast.makeText(this, "There are no more chapters available. This is the last chapter.", Toast.LENGTH_LONG).show();
        } else {
            if (readLeftToRight) {
                mangaViewPager.setPageIndex(images.size() - 1);
            }
            else {
                mangaViewPager.setPageIndex(0);
            }
            saveMostRecentPage();
            markChapterRead();
            chapterIndex++;
            Toast.makeText(this, "Chapter " + getChapterNumber(), Toast.LENGTH_SHORT).show();
            displayChapter();
        }
    }

    /**
     * Navigates to previous chapter if available.
     */
    public void prevChapter() {
        if (loading)
            return;

        if (chapterIndex == 0) {
            // Do nothing if already on first chapter
            Toast.makeText(this, "No more previous chapters.", Toast.LENGTH_LONG).show();
        } else {
            saveMostRecentPage();
            chapterIndex--;
            Toast.makeText(this, "Chapter " + getChapterNumber(), Toast.LENGTH_SHORT).show();
            displayChapter();
        }
    }

    public void reverseReadingDirection() {
        mangaViewPager.setLeftToRight(readLeftToRight);
        seekBar.setLeftToRight(readLeftToRight);
        if (readLeftToRight) {
            leftBubble.setText(R.string.back);
            rightBubble.setText(R.string.next);
        } else {
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
     * <p/>
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

    private String getChapterNumber() {
        return chapters.get(chapterIndex).number;
    }

    public List<MangaEdenImageItem> getImages() {
        return images;
    }

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

    public void showSystemUI() {
        animationHelper.fadeIn(uiLayout);
        getWindow().getDecorView().setSystemUiVisibility( // show system UI
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }


    @Override
    protected void onStop() {
        super.onStop();
        saveMostRecentPage();
    }

    public void saveMostRecentPage() {
        manga.chaptersList.get(chapterIndex).mostRecentPage = mangaViewPager.getPageIndex();
        Paper.book(UserLibraryHelper.USER_LIBRARY_DB).write(manga.id, manga);
    }

    public void handleTap(View view, float x, float y) {
        Point size = new Point();
        getWindowManager().getDefaultDisplay().getSize(size);
        float screenWidth = size.x;
        if (x < LEFT_SIDE * screenWidth) {
            if (readLeftToRight) {
                mangaViewPager.previousPage();
            } else {
                mangaViewPager.nextPage();
            }
        } else if (x > RIGHT_SIDE * screenWidth) {
            if (readLeftToRight) {
                mangaViewPager.nextPage();
            } else {
                mangaViewPager.previousPage();
            }
        } else {
            if (isUIVisible) {
                hideSystemUI();
                isUIVisible = false;
            } else {
                showSystemUI();
                isUIVisible = true;
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the list of images, if activity is being destroyed due to lack of resources
        savedInstanceState.putSerializable(KEY_IMAGES, (Serializable) images);

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }


    public void markChapterRead() {
        manga.chaptersList.get(chapterIndex).read = true;
        Paper.book(UserLibraryHelper.USER_LIBRARY_DB).write(manga.id, manga);
    }
}
