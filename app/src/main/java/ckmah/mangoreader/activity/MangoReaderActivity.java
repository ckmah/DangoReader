package ckmah.mangoreader.activity;

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

import com.william.mangoreader.R;

import ckmah.mangoreader.BootReceiver;
import ckmah.mangoreader.fragment.BrowseMangaFragment;
import ckmah.mangoreader.fragment.MyLibraryFragment;
import io.paperdb.Paper;

/**
 * Main activity screen. Displays various fragments.
 */
public class MangoReaderActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mango_reader);

        // init layout
        initToolbar();
        initNavigation();
//        initSpinner();

        // load user library
        Paper.init(this);

        // display library by default
        displayView(R.id.library_nav_item);

        // Start polling for chapter updates
        BootReceiver.RefreshService.start(this);
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
        drawerLayout.closeDrawers();
        if (menuItem.getItemId() == R.id.settings_nav_item) {
            // Launch settings activity, but don't keep selected in the drawer
            Intent settingsIntent = new Intent(MangoReaderActivity.this, SettingsActivity.class);
            startActivity(settingsIntent);
            return false;
        } else {
            // Switch to the corresponding fragment, and keep selected in the drawer
            displayView(menuItem.getItemId());
            return true;
        }
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
            case R.id.library_nav_item: // library
//                findViewById(R.id.spinner_browse_sources).setVisibility(View.GONE);
                findViewById(R.id.sliding_tabs).setVisibility(View.VISIBLE);
                fragment = new MyLibraryFragment();
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
