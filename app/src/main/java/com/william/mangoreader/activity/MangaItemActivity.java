package com.william.mangoreader.activity;

import android.graphics.Bitmap;
import android.graphics.Color;
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
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.william.mangoreader.DividerItemDecoration;
import com.william.mangoreader.R;
import com.william.mangoreader.adapter.MangaItemAdapter;
import com.william.mangoreader.model.MangaEdenMangaDetailItem;
import com.william.mangoreader.parse.MangaEden;
import com.william.mangoreader.volley.VolleySingleton;

import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;

/**
 * Activity that displays a single manga, and shows manga info and chapters
 */
public class MangaItemActivity extends AppCompatActivity {

    private String mangaId;
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

        mToolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        initRecyclerView();

        queue = VolleySingleton.getInstance(this).getRequestQueue();

        mangaId = (String) getIntent().getExtras().get("mangaId");
        fetchMangaDetailFromMangaEden();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_manga_item, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void fetchMangaDetailFromMangaEden() {
        MangaEden.getMangaEdenService(this).getMangaDetails(mangaId, new Callback<MangaEdenMangaDetailItem>() {
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

    public void extractColorPalette(Bitmap bitmap) {
        List<Palette.Swatch> swatches = Palette.from(bitmap).maximumColorCount(20).generate().getSwatches();

        primaryColor = new Palette.Swatch(R.color.grey, 0);
        int primaryColorPopulation = 0;

        secondaryColor = new Palette.Swatch(R.color.black, 0);
        int secondaryColorPopulation = 0;

        // extract primary color
        for (Palette.Swatch swatch : swatches) {
            int population = swatch.getPopulation();
            if (population > primaryColorPopulation) {
                primaryColorPopulation = population;
                primaryColor = swatch;
            }
        }
        // extract secondary color
        for (Palette.Swatch swatch : swatches) {
            int population = swatch.getPopulation();
            if (population > secondaryColorPopulation && population != primaryColorPopulation) {
                secondaryColorPopulation = population;
                secondaryColor = swatch;
            }
        }

        setLayoutColors(primaryColor);
    }

    public void setLayoutColors(Palette.Swatch primaryColor) {
        Window window = getWindow();
        // status bar assigned primary color
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(primaryColor.getRgb());

        // scrim assigned primary color
        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.manga_item_collapsing_toolbar);
        collapsingToolbarLayout.setContentScrimColor(primaryColor.getRgb());

        // details assigned secondary color
        View detailsView = findViewById(R.id.details_view);
        if (detailsView == null)
            return;
        detailsView.setBackgroundColor(secondaryColor.getRgb());

        int textColor = contrastTextColor(secondaryColor.getRgb());
        ((TextView) detailsView.findViewById(R.id.manga_item_title)).setTextColor(textColor);
        ((TextView) detailsView.findViewById(R.id.manga_item_author)).setTextColor(textColor);
        ((TextView) detailsView.findViewById(R.id.manga_item_description)).setTextColor(textColor);

    }

    public int contrastTextColor(int color) {
        // Contrast formula derived from http://stackoverflow.com/a/1855903/1222351
        double luminance = 0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color);
        return luminance > 127 ? Color.BLACK : Color.WHITE;
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
