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
import ckmah.mangoreader.adapter.MangaItemTabbedAdapter;
import ckmah.mangoreader.model.MangaEdenMangaDetailItem;
import ckmah.mangoreader.model.MangaEdenMangaListItem;
import ckmah.mangoreader.parse.MangaEden;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Activity that displays a single manga, and shows manga info and chapters
 */
public class MangaItemActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    private MangaEdenMangaListItem mangaListItem;
    private MangaEdenMangaDetailItem manga;

    private Palette.Swatch primaryColor;
    private Palette.Swatch secondaryColor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manga_item_tabbed);
        initToolBar();
        mangaListItem = (MangaEdenMangaListItem) getIntent().getSerializableExtra("mangaListItem");
        fetchMangaDetailFromMangaEden();
    }


    private void initToolBar() {
        toolbar = (Toolbar) findViewById(R.id.item_toolbar);
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
        ViewPager viewPager = (ViewPager) findViewById(R.id.details_and_chapter_pager);
        MangaItemTabbedAdapter mangaItemTabbedAdapter = new MangaItemTabbedAdapter(this, this.getSupportFragmentManager(), manga);
        viewPager.setAdapter(mangaItemTabbedAdapter);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.item_sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void initMarqueeTitle() {
        TextView mangaTitleView = (TextView) findViewById(R.id.manga_item_chapter_title);
        mangaTitleView.setText(manga.getTitle());
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
        bookmarkToggle.setSelected((UserLibraryHelper.isInLibrary(this, mangaListItem)));
        bookmarkToggle.setBackgroundColor(getResources().getColor(R.color.transparent));
        bookmarkToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bookmarkToggle.isSelected()) {
                    UserLibraryHelper.removeFromLibrary(mangaListItem, bookmarkToggle, activity, true, null, -1);
                } else {
                    UserLibraryHelper.addToLibrary(mangaListItem, bookmarkToggle, activity, null, -1);
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

    private void fetchMangaDetailFromMangaEden() {
        Log.d("SORTING", "FETCHING");
        MangaEden.getMangaEdenService(this)
                .getMangaDetails(mangaListItem.id)
                .enqueue(new Callback<MangaEdenMangaDetailItem>() {
                    @Override
                    public void onResponse(Response<MangaEdenMangaDetailItem> response, Retrofit retrofit) {
                        manga = response.body();
                        initViewPager();
                        initMarqueeTitle();
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        Log.d("ERROR", t.getMessage());
                    }
            });
    }

    @Deprecated
    public Palette.Swatch getPrimaryColor() {
        return primaryColor;
    }

    @Deprecated
    public Palette.Swatch getSecondaryColor() {
        return secondaryColor;
    }
}
