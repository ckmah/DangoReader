package com.william.mangoreader.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.william.mangoreader.R;
import com.william.mangoreader.fragment.MangaItemChapterFragment;
import com.william.mangoreader.fragment.MangaItemDetailFragment;
import com.william.mangoreader.model.MangaEdenMangaDetailItem;
import com.william.mangoreader.parse.MangaEden;
import com.william.mangoreader.volley.VolleySingleton;

import org.json.JSONObject;

import java.util.List;

/**
 * Activity that displays a single manga, and shows manga info and chapters
 */
public class MangaItemActivity extends AppCompatActivity {

    private String mangaId;
    private RequestQueue queue;
    private MangaEdenMangaDetailItem manga;

    private Palette.Swatch primaryColor;

    private Palette.Swatch secondaryColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manga_item);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar_manga_item);
        setSupportActionBar(mToolbar);

        mToolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        queue = VolleySingleton.getInstance(this.getApplicationContext()).getRequestQueue();

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
        String url = MangaEden.MANGAEDEN_MANGADETAIL_PREFIX + mangaId;

        queue.add(new JsonObjectRequest
                        (url, new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject response) {
                                manga = MangaEden.parseMangaEdenMangaDetailResponse(response.toString());
                                loadContent();
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                System.out.println("Response error: " + error.toString());
                            }
                        })
        );
    }

    private void loadContent() {
        ImageView imageView = (ImageView) findViewById(R.id.manga_item_image_view);
        MangaEden.setMangaArt(manga.getImageUrl(), getApplicationContext(), imageView, this);

    }

    public void loadFragments() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        MangaItemDetailFragment detailsFragment = MangaItemDetailFragment.newInstance(manga);
        MangaItemChapterFragment chaptersFragment = MangaItemChapterFragment.newInstance(manga);
        fragmentTransaction.add(R.id.manga_item_header, detailsFragment);
        fragmentTransaction.add(R.id.manga_item_header, chaptersFragment);
        fragmentTransaction.commit();
    }


    public void extractColorPalette(Bitmap bitmap) {
        List<Palette.Swatch> swatches = Palette.from(bitmap).generate().getSwatches();

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
    }

    public Palette.Swatch getPrimaryColor() {
        return primaryColor;
    }

    public Palette.Swatch getSecondaryColor() {
        return secondaryColor;
    }


}
