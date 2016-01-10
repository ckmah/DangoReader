package ckmah.mangoreader.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.william.mangoreader.R;

import ckmah.mangoreader.UserLibraryHelper;
import ckmah.mangoreader.adapter.MangaItemPageAdapter;
import ckmah.mangoreader.database.Manga;
import ckmah.mangoreader.model.MangaEdenMangaDetailItem;
import ckmah.mangoreader.parse.MangaEden;
import io.paperdb.Paper;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Activity that displays a single manga, and shows manga info and chapters
 */
public class MangaItemActivity extends AppCompatActivity {
    private Manga manga;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manga_item);
        manga = new Manga();

        initToolBar();

        String mangaId = getIntent().getStringExtra("mangaId");
        manga = Paper.book(UserLibraryHelper.USER_LIBRARY_DB).read(mangaId);
        if (manga == null || manga.chaptersList == null) {
            fetchMangaDetailFromMangaEden(mangaId);
        } else {
            initViewPager();
            initMarqueeTitle();
        }
    }

    private void initToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.manga_item_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void initViewPager() {
        ViewPager viewPager = (ViewPager) findViewById(R.id.manga_item_pager);
        MangaItemPageAdapter mangaItemPageAdapter = new MangaItemPageAdapter(this, getSupportFragmentManager(), manga.id);
        viewPager.setAdapter(mangaItemPageAdapter);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.manga_item_tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void initMarqueeTitle() {
        TextView mangaTitleView = (TextView) findViewById(R.id.manga_item_chapter_title);
        mangaTitleView.setText(manga.title);
        mangaTitleView.setSelected(true); // for marquee to scroll
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_manga_item, menu);
        MenuItem bookmarkItem = menu.findItem(R.id.manga_item_bookmark_button);
        final MangaItemActivity activity = this;
        final ImageButton bookmarkToggle = new ImageButton(this);

        bookmarkToggle.setImageResource(R.drawable.bookmark_toggle);
        bookmarkToggle.setSelected(manga != null && manga.favorite);
        bookmarkToggle.setBackgroundResource(R.color.transparent);
        bookmarkToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bookmarkToggle.isSelected()) {
                    UserLibraryHelper.removeFromLibrary(manga, bookmarkToggle, activity, true, null, -1);
                } else {
                    UserLibraryHelper.addToLibrary(manga, bookmarkToggle, activity, true, null, -1);
                }
            }
        });
        bookmarkItem.setActionView(bookmarkToggle);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    private void fetchMangaDetailFromMangaEden(String mangaId) {
        manga = new Manga();
        Log.d("MangaItemActivity", "Fetching manga details");
        MangaEden.getMangaEdenService(this)
                .getMangaDetails(mangaId)
                .enqueue(new Callback<MangaEdenMangaDetailItem>() {
                    @Override
                    public void onResponse(Response<MangaEdenMangaDetailItem> response, Retrofit retrofit) {
                        MangaEdenMangaDetailItem mangaDetailItem = response.body();
                        manga.title = mangaDetailItem.getTitle();
                        manga.imageSrc = mangaDetailItem.getImageUrl();
                        manga.lastChapterDate = mangaDetailItem.getLastChapterDate();
                        manga.genres = mangaDetailItem.getCategories();
                        manga.hits = mangaDetailItem.getHits();
                        manga.status = mangaDetailItem.getStatus();
                        manga.author = mangaDetailItem.getAuthor();
                        manga.dateCreated = mangaDetailItem.getDateCreated();
                        manga.description = mangaDetailItem.getDescription();
                        manga.language = mangaDetailItem.getLanguage();
                        manga.numChapters = mangaDetailItem.getNumChapters();
                        manga.chaptersList = MangaEden.convertChapterItemstoChapters(mangaDetailItem.getChapters());
                        Paper.book(UserLibraryHelper.USER_LIBRARY_DB).write(manga.id, manga);

                        initViewPager();
                        initMarqueeTitle();
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        Log.e("MangaItemActivity", "Could not fetch details from MangaEden");
                    }
                });
    }
    
}
