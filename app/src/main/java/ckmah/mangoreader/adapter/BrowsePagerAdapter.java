package ckmah.mangoreader.adapter;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import com.william.mangoreader.R;

import java.util.ArrayList;
import java.util.Arrays;

import ckmah.mangoreader.fragment.BrowseMangaFragment;
import ckmah.mangoreader.fragment.BrowsePageFragment;

public class BrowsePagerAdapter extends FragmentPagerAdapter {
    private String sortKey = "";
    private Activity activity;
    CardLayoutAdapter cardLayoutAdapter;
    ArrayList<String> sortItems;

    public BrowsePagerAdapter(Activity activity, FragmentManager fm, String sortKey) {
        super(fm);
        this.activity = activity;
        this.sortKey = sortKey;
        sortItems = new ArrayList<>(Arrays.asList(activity.getResources().getStringArray(R.array.sort_items)));
    }

    @Override
    public Fragment getItem(int position) {
        Log.d("BrowsePagerAdapter", "position: " + position);
        return BrowsePageFragment.newInstance(position, sortKey);
    }

    @Override
    public int getCount() {
        // Should only create one page for sorted all manga.
        if (sortItems.contains(sortKey)) {
            return 1;
        } else { // Create pages for each sort order for manga of chosen genre.
            return activity.getResources().getStringArray(R.array.sort_items).length;
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return activity.getResources().getStringArray(R.array.sort_items)[position];
    }
}
