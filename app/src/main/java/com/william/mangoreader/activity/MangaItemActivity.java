package com.william.mangoreader.activity;

import android.database.sqlite.SQLiteConstraintException;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.william.mangoreader.DividerItemDecoration;
import com.william.mangoreader.R;
import com.william.mangoreader.adapter.MangaItemAdapter;
import com.william.mangoreader.daogen.UserLibraryManga;
import com.william.mangoreader.daogen.UserLibraryMangaDao;
import com.william.mangoreader.model.MangaEdenMangaDetailItem;
import com.william.mangoreader.model.MangaEdenMangaListItem;
import com.william.mangoreader.parse.MangaEden;
import com.william.mangoreader.volley.VolleySingleton;

import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;
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

        final ImageButton bookmarkToggle = new ImageButton(this);
        bookmarkToggle.setImageResource(R.drawable.bookmark_toggle);
        bookmarkToggle.setSelected((findMangaInLibrary().size() > 0));
        bookmarkToggle.setBackgroundColor(getResources().getColor(R.color.transparent));
        bookmarkToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bookmarkToggle.isSelected()) {
                    removeFromLibrary();
                    bookmarkToggle.setSelected(!bookmarkToggle.isSelected());
                } else {
                    if (addToLibrary()) {
                        bookmarkToggle.setSelected(!bookmarkToggle.isSelected());
                    }
                }
            }
        });
        bookmarkItem.setActionView(bookmarkToggle);

        return true;
    }

    // returns true if successfully adds
    private boolean addToLibrary() {
        String genres = TextUtils.join("\t", mangaListItem.genres);
        UserLibraryManga mangaItem = new UserLibraryManga(
                mangaListItem.id,
                getResources().getStringArray(R.array.library_categories)[0],
                mangaListItem.title,
                mangaListItem.imageUrl,
                genres,
                mangaListItem.status,
                mangaListItem.lastChapterDate,
                mangaListItem.hits);

        try {
            MangoReaderActivity.userLibraryMangaDao.insert(mangaItem);
            // popup snackbar for undo option
            String added = "\"" + mangaListItem.title + "\" added to your library.";
            Snackbar
                    .make(findViewById(R.id.parent_layout), added, Snackbar.LENGTH_LONG)
                    .setAction("UNDO", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            removeFromLibrary();
                        }
                    })
                    .show();
            return true;
        } catch (SQLiteConstraintException e) {
            String duplicate = "\"" + mangaListItem.title + "\" is already in your library.";
            Snackbar
                    .make(findViewById(R.id.parent_layout), duplicate, Snackbar.LENGTH_SHORT)
                    .show();
            Log.d("LIBRARY", "Entry already exists");
            return false;
        }
    }

    private void removeFromLibrary() {
        List<UserLibraryManga> l = findMangaInLibrary();
        if (l.size() == 0) {
            Log.e("MangoReader", "No manga found in user library.");
        } else {
            UserLibraryManga mangaItem = (UserLibraryManga) l.get(0);
            MangoReaderActivity.userLibraryMangaDao.delete(mangaItem);
            String removed = "\"" + mangaListItem.title + "\" removed from your library.";
            Snackbar
                    .make(findViewById(R.id.parent_layout), removed, Snackbar.LENGTH_LONG)
                            // .setAction() //TODO add undo on click
                    .show();
        }
    }

    private List<UserLibraryManga> findMangaInLibrary() {
        QueryBuilder qb = MangoReaderActivity.userLibraryMangaDao.queryBuilder();
        qb.where(UserLibraryMangaDao.Properties.Title.eq(mangaListItem.title), UserLibraryMangaDao.Properties.ImageURL.eq(mangaListItem.imageUrl));
        return qb.list();
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
