package com.william.mangoreader.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.william.mangoreader.R;
import com.william.mangoreader.activity.viewpager.MangaViewPager;

public class MangaViewerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manga_viewer);

        MangaViewPager mangaViewPager = (MangaViewPager) findViewById(R.id.manga_view_pager);
    }


}
