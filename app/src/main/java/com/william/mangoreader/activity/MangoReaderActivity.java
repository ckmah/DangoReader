package com.william.mangoreader.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.william.mangoreader.R;
import com.william.mangoreader.daogen.DaoMaster;
import com.william.mangoreader.daogen.DaoSession;
import com.william.mangoreader.daogen.UserLibraryMangaDao;
import com.william.mangoreader.fragment.BrowseMangaFragment;
import com.william.mangoreader.fragment.MyLibraryFragment;

/**
 * Main activity screen. Displays various fragments.
 */
public class MangoReaderActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;

    public SQLiteDatabase userLibraryDb;
    public DaoMaster daoMaster;
    public DaoSession daoSession;
    public UserLibraryMangaDao userLibraryMangaDao;

    private Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mango_reader);

        initToolbar();
        initNavigation();
//        initSpinner();

        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "user-library-db", null);
        userLibraryDb = helper.getWritableDatabase();
        helper.onUpgrade(userLibraryDb, userLibraryDb.getVersion(), 1000); // DEBUG PURPOSES ONLY
        daoMaster = new DaoMaster(userLibraryDb);
        daoSession = daoMaster.newSession();
        userLibraryMangaDao = daoSession.getUserLibraryMangaDao();


        displayView(R.id.navigation_item_1);
    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    private void initNavigation() {
        NavigationView view = (NavigationView) findViewById(R.id.navigation_view);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        view.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);
        drawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();
    }

    private void initSpinner() {
        // spinner for source selection
//        Spinner spinner = (Spinner) findViewById(R.id.spinner_browse_sources);
//        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getApplicationContext(),
//                R.array.browse_sources, R.layout.simple_spinner_item);
//        adapter.setDropDownViewResource(R.layout.drop_list);
//        spinner.setAdapter(adapter);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        menuItem.setChecked(true);

        drawerLayout.closeDrawers();

        displayView(menuItem.getItemId());
        return true;
    }

    /**
     * Handles what fragment to display based on navdrawer selection.
     *
     * @param id Position of clicked view in navdrawer.
     */
    private void displayView(int id) {
        Fragment fragment = null;
        String title = getString(R.string.app_name);

        // create corresponding fragment
        switch (id) {
            case R.id.navigation_item_1: // library
//                findViewById(R.id.spinner_browse_sources).setVisibility(View.GONE);
                findViewById(R.id.sliding_tabs).setVisibility(View.VISIBLE);
                fragment = new MyLibraryFragment();
                title = getString(R.string.title_my_library);
                break;
            case R.id.navigation_item_2: // browse
//                findViewById(R.id.spinner_browse_sources).setVisibility(View.VISIBLE);
                findViewById(R.id.sliding_tabs).setVisibility(View.GONE);
                fragment = new BrowseMangaFragment();
                title = getString(R.string.title_browse);
                break;
            case R.id.navigation_item_3: // settings
                Intent settingsIntent = new Intent(MangoReaderActivity.this, SettingsActivity.class);
                startActivity(settingsIntent);
                return;
            default:
                break;
        }
        // add fragment to MangoReaderActivity #container_body
        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container_body, fragment);
            fragmentTransaction.commit();
            getSupportActionBar().setTitle(title);
        }
    }



}
