package com.william.mangoreader.activity;

import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
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

import com.android.volley.RequestQueue;
import com.william.mangoreader.DividerItemDecoration;
import com.william.mangoreader.R;
import com.william.mangoreader.UserLibraryHelper;
import com.william.mangoreader.adapter.MangaItemAdapter;
import com.william.mangoreader.model.MangaEdenMangaDetailItem;
import com.william.mangoreader.model.MangaEdenMangaListItem;
import com.william.mangoreader.parse.MangaEden;
import com.william.mangoreader.volley.VolleySingleton;

import retrofit.Callback;
import retrofit.RetrofitError;

/**
 * Activity that displays a single manga, and shows manga info and chapters
 */
public class MangaItemActivity extends AppCompatActivity {

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

    private void loadContent() {
        ((TextView) findViewById(R.id.subtitle_author)).setText(manga.getAuthor());
        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.manga_item_collapsing_toolbar);
        collapsingToolbarLayout.setTitle(manga.getTitle());
        updateRecyclerView();
    }

    public void initRecyclerView() {
        mRecyclerView = (RecyclerView) findViewById(R.id.manga_item_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mangaItemAdapter = new MangaItemAdapter(this, manga);
        mRecyclerView.setAdapter(mangaItemAdapter);

        RecyclerView.ItemDecoration itemDecoration = new
                DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST);
        mRecyclerView.addItemDecoration(itemDecoration);

        mRecyclerView.setVisibility(View.GONE);
    }

    public void updateRecyclerView() {
        mangaItemAdapter.loadMangaInfo(manga);

        mRecyclerView.setVisibility(View.VISIBLE);
        findViewById(R.id.manga_item_header_placeholder).setVisibility(View.GONE);
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
