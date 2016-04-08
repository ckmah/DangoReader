package moe.dangoreader.activity;

import android.content.Intent;
import android.content.res.Configuration;
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

import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;
import io.paperdb.Paper;
import moe.dangoreader.BootReceiver;
import moe.dangoreader.R;
import moe.dangoreader.UserLibraryHelper;
import moe.dangoreader.fragment.BrowseMangaFragment;
import moe.dangoreader.fragment.LibraryPageFragment;

/**
 * Main activity screen. Displays various fragments.
 */
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private Toolbar toolbar;

    private final static String MYLIBRARY = "mylibrary";
    private final static String BROWSE = "browse";
    private final static String SAVED_FRAGMENT = "fragment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main);

        // init layout
        initToolbar();
        initNavigation();

        // load user library
        Paper.init(this);
        String savedFragment;
        if (savedInstanceState != null && (savedFragment = savedInstanceState.getString(SAVED_FRAGMENT)) != null) {
            if (savedFragment.equals(MYLIBRARY)) {
                displayView(R.id.library_nav_item);
                navigationView.setCheckedItem(R.id.library_nav_item);
            }
            else if (savedFragment.equals(BROWSE)){
                displayView(R.id.browse_nav_item);
                navigationView.setCheckedItem(R.id.browse_nav_item);
            }
        }
        else {
            if (UserLibraryHelper.findAllFavoritedManga().size() > 0) {
                // Show library as first page, if any manga are favorited
                displayView(R.id.library_nav_item);
                navigationView.setCheckedItem(R.id.library_nav_item);
            } else {
                // Show browse as first page otherwise
                displayView(R.id.browse_nav_item);
                navigationView.setCheckedItem(R.id.browse_nav_item);
            }
        }

        // Start polling for chapter updates if this is the first launch
        if (!BootReceiver.RefreshService.isStarted(this)) {
            BootReceiver.RefreshService.setStarted(this, true);
            BootReceiver.RefreshService.toggle(this);
        }
    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    private void initNavigation() {
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);
        drawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        drawerLayout.closeDrawers();
        if (menuItem.getItemId() == R.id.settings_nav_item) {
            // Launch settings activity, but don't keep selected in the drawer
            Intent settingsIntent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(settingsIntent);
            return false;
        } else {
            // Switch to the corresponding isBrowsing, and keep selected in the drawer
            displayView(menuItem.getItemId());
            return true;
        }
    }

    /**
     * Handles what isBrowsing to display based on navdrawer selection.
     *
     * @param id Position of clicked view in navdrawer.
     */
    private void displayView(int id) {
        Fragment fragment = null;
        String title = getString(R.string.app_name);

        // create corresponding isBrowsing
        switch (id) {
            case R.id.library_nav_item: // library
//                findViewById(R.id.spinner_browse_sources).setVisibility(View.GONE);
                findViewById(R.id.sliding_tabs).setVisibility(View.GONE);
                fragment = LibraryPageFragment.newInstance(0);
                title = getString(R.string.title_my_library);
                break;
            case R.id.browse_nav_item: // browse
//                findViewById(R.id.spinner_browse_sources).setVisibility(View.VISIBLE);
                findViewById(R.id.sliding_tabs).setVisibility(View.GONE);
                fragment = BrowseMangaFragment.getInstance();
                title = getString(R.string.title_browse);
                break;
            default:
                break;
        }
        // add isBrowsing to MainActivity #container_body
        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container_body, fragment);
            fragmentTransaction.commit();
            getSupportActionBar().setTitle(title);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Fragment f = getSupportFragmentManager().findFragmentById(R.id.container_body);
        if (f instanceof LibraryPageFragment){
            outState.putString(SAVED_FRAGMENT, MYLIBRARY);
        }
        else if (f instanceof BrowseMangaFragment) {
            outState.putString(SAVED_FRAGMENT, BROWSE);
        }

    }

}
