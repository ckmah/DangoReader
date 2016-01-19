package ckmah.mangoreader.activity;


import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.william.mangoreader.R;

import ckmah.mangoreader.adapter.BrowsePagerAdapter;
import io.paperdb.Paper;

public class BrowseMangaActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private String toolbarTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manga_browse);
        Paper.init(this);
        initToolbar();
        initTabLayout();
    }

    public void initToolbar() {
        toolbarTitle = getIntent().getStringExtra(getString(R.string.browse_order));
        toolbar = (Toolbar) findViewById(R.id.browse_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(toolbarTitle);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    public void initTabLayout() {
        String[] allGenres = getResources().getStringArray(R.array.genre_list);
        ViewPager viewPager = (ViewPager) findViewById(R.id.browse_pager);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.browse_tabs);
        tabLayout.setVisibility(View.GONE);

        BrowsePagerAdapter pagerAdapter = null;
        // if genre requested, use TabLayout
        for (String genre : allGenres) {
            if (toolbarTitle.compareTo(genre) == 0) {
                tabLayout.setVisibility(View.VISIBLE);

                pagerAdapter = new BrowsePagerAdapter(this, getSupportFragmentManager(), toolbarTitle);
                break;
            }
        }
        if (pagerAdapter == null) {
            pagerAdapter = new BrowsePagerAdapter(this, getSupportFragmentManager(), toolbarTitle);
        }

        viewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}
