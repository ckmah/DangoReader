package ckmah.mangoreader.activity;


import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.william.mangoreader.R;

public class MangaBrowseActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manga_browse);

        String toolbarTitle = getIntent().getStringExtra(getString(R.string.browse_order));

        toolbar = (Toolbar) findViewById(R.id.browse_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(toolbarTitle);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.browse_swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(this);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.browse_tabs);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onRefresh() {

    }
}
