package ckmah.mangoreader.activity;

import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.support.v4.app.Fragment;

import com.android.volley.RequestQueue;
import com.william.mangoreader.R;

import ckmah.mangoreader.DividerItemDecoration;
import ckmah.mangoreader.UserLibraryHelper;
import ckmah.mangoreader.adapter.MangaItemTabbedAdapter;
import ckmah.mangoreader.model.MangaEdenMangaDetailItem;
import ckmah.mangoreader.model.MangaEdenMangaListItem;
import ckmah.mangoreader.parse.MangaEden;
import ckmah.mangoreader.volley.VolleySingleton;
import retrofit.Callback;
import retrofit.RetrofitError;
import xyz.danoz.recyclerviewfastscroller.vertical.VerticalRecyclerViewFastScroller;

/**
 * Activity that displays a single manga, and shows manga info and chapters
 */
public class MangaItemActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    private MangaEdenMangaListItem mangaListItem;
    private RequestQueue queue;
    private MangaEdenMangaDetailItem manga;

    private Palette.Swatch primaryColor;
    private Palette.Swatch secondaryColor;

    /*
    private MangaEdenMangaListItem mangaListItem;
    private RequestQueue queue;
    private MangaEdenMangaDetailItem manga;

    private RecyclerView mRecyclerView;
    private MangaItemAdapter mangaItemAdapter;

    private Palette.Swatch primaryColor;
    private Palette.Swatch secondaryColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manga_item);

        manga = new MangaEdenMangaDetailItem();

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar_manga_item);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        initRecyclerView();
        queue = VolleySingleton.getInstance(this).getRequestQueue();
        mangaListItem = (MangaEdenMangaListItem) getIntent().getSerializableExtra("mangaListItem");
        fetchMangaDetailFromMangaEden();
    }
    */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manga_item_tabbed);
        manga = new MangaEdenMangaDetailItem();
        mangaListItem = (MangaEdenMangaListItem) getIntent().getSerializableExtra("mangaListItem");
        fetchMangaDetailFromMangaEden();
    }

    private void loadContent() {
//        ((TextView) findViewById(R.id.subtitle_author)).setText(manga.getAuthor());
//        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.manga_item_collapsing_toolbar);
//        collapsingToolbarLayout.setTitle(manga.getTitle());
        initToolBar();
        initViewPager();
//        updateRecyclerView();
    }

    private void initToolBar() {
        toolbar = (Toolbar) findViewById(R.id.item_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        toolbar.setTitle(manga.getTitle());
    }

    private void initViewPager() {
        ViewPager viewPager = (ViewPager) findViewById(R.id.details_and_chapter_pager);
        MangaItemTabbedAdapter mangaItemTabbedAdapter = new MangaItemTabbedAdapter(this, this.getSupportFragmentManager(), manga);
        viewPager.setAdapter(mangaItemTabbedAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.item_sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    //from old manga item activity.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_manga_item, menu);
        MenuItem bookmarkItem = menu.findItem(R.id.manga_item_bookmark_button);
        final MangaItemActivity activity = this;
        final ImageButton bookmarkToggle = new ImageButton(this);

        bookmarkToggle.setImageResource(R.drawable.bookmark_toggle);
        bookmarkToggle.setSelected((UserLibraryHelper.findMangaInLibrary(mangaListItem).size() > 0));
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
        MangaEden.getMangaEdenService(this).getMangaDetails(mangaListItem.id, new Callback<MangaEdenMangaDetailItem>() {
            @Override
            public void success(MangaEdenMangaDetailItem item, retrofit.client.Response response) {
                manga = item;
                loadContent();
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d("ERROR", error.getMessage());
            }
        });
    }

//    public void initRecyclerView() {
//
//        mRecyclerView = (RecyclerView) findViewById(R.id.manga_item_recycler_view);
//        VerticalRecyclerViewFastScroller fastScroller = (VerticalRecyclerViewFastScroller) findViewById(R.id.fast_scroller);
//
//        // Connect the recycler to the scroller (to let the scroller scroll the list)
//        fastScroller.setRecyclerView(mRecyclerView);
//        // Connect the scroller to the recycler (to let the recycler scroll the scroller's handle)
//        mRecyclerView.addOnScrollListener(fastScroller.getOnScrollListener());
//
//        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
//        mangaItemAdapter = new MangaItemAdapter(this, manga);
//        mRecyclerView.setAdapter(mangaItemAdapter);
//
//        RecyclerView.ItemDecoration itemDecoration = new
//                DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST);
//        mRecyclerView.addItemDecoration(itemDecoration);
//
//        mRecyclerView.setVisibility(View.GONE);
//    }
//
//    public void updateRecyclerView() {
//        mangaItemAdapter.loadMangaInfo(manga);
//
//        mRecyclerView.setVisibility(View.VISIBLE);
//        findViewById(R.id.manga_item_header_placeholder).setVisibility(View.GONE);
//    }

    @Deprecated
    public Palette.Swatch getPrimaryColor() {
        return primaryColor;
    }

    @Deprecated
    public Palette.Swatch getSecondaryColor() {
        return secondaryColor;
    }
}
