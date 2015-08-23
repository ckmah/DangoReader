package com.william.mangoreader.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.william.mangoreader.R;
import com.william.mangoreader.activity.viewpager.MangaViewPager;
import com.william.mangoreader.adapter.MangaImagePagerAdapter;
import com.william.mangoreader.model.MangaEdenImageItem;
import com.william.mangoreader.parse.MangaEden;
import com.william.mangoreader.volley.VolleySingleton;

import org.json.JSONObject;

import java.util.ArrayList;

public class MangaViewerActivity extends AppCompatActivity {

    private ArrayList<MangaEdenImageItem> images;
        private MangaViewPager mangaViewPager;
//    private ViewPager viewPager;
    private MangaImagePagerAdapter imageAdapter;

    private String chapterId;

    private RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manga_viewer);

        images = new ArrayList<>();
        chapterId = (String) getIntent().getExtras().get("chapterId");
        queue = VolleySingleton.getInstance(this).getRequestQueue();
//        viewPager = (ViewPager) findViewById(R.id.manga_view_pager);
        mangaViewPager = (MangaViewPager) findViewById(R.id.manga_view_pager);

        fetchMangaImagesFromMangaEden();
    }

    private void fetchMangaImagesFromMangaEden() {
        String url = MangaEden.MANGAEDEN_CHAPTERDETAIL_PREFIX + chapterId;

        queue.add(new JsonObjectRequest
                        (url, new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject response) {
                                images = MangaEden.parseMangaEdenMangaImageResponse(response.toString());
                                imageAdapter = new MangaImagePagerAdapter(getSupportFragmentManager(), images.size());
//                                viewPager.setAdapter(imageAdapter);
                                mangaViewPager.setAdapter(imageAdapter);
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
//        viewPager.setCurrentItem(images.size() - 1);
        mangaViewPager.setCurrentItem(images.size() - 1);
        // TODO: update other stuff
    }

    public ArrayList<MangaEdenImageItem> getImages() {
        return images;
    }

}
