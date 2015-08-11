package com.william.mangoreader.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.william.mangoreader.R;
import com.william.mangoreader.fragment.ChaptersFragment;
import com.william.mangoreader.model.MangaEdenMangaDetailItem;
import com.william.mangoreader.parse.MangaEden;
import com.william.mangoreader.volley.VolleySingleton;

import org.json.JSONObject;

/**
 * Activity that displays a single manga, and shows manga info and chapters
 */
public class MangaItemActivity extends AppCompatActivity {

    private String mangaId;
    private RequestQueue queue;
    private MangaEdenMangaDetailItem manga;

    private Fragment chaptersFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(getResources().getColor(R.color.transparent));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        setContentView(R.layout.activity_manga_item);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.manga_item_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

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
        MangaEden.setImage(manga.getImageUrl(), this, imageView);

        TextView titleView = (TextView) findViewById(R.id.manga_item_title);
        titleView.setText(manga.getTitle());

        TextView subtitleView = (TextView) findViewById(R.id.manga_item_subtitle);
        subtitleView.setText(manga.getAuthor());

        TextView descriptionView = (TextView) findViewById(R.id.manga_item_description);
        descriptionView.setText(manga.getDescription());

        chaptersFragment = ChaptersFragment.newInstance(manga);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.manga_item_chapters, chaptersFragment);
        fragmentTransaction.commit();

    }
}
