package ckmah.mangoreader.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.william.mangoreader.R;

import ckmah.mangoreader.fragment.BrowsePageFragment;

public class BrowsePagerAdapter extends FragmentStatePagerAdapter {
    private String sortKey = "";
    private Context context;

    public BrowsePagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        this.context = context;
    }

    public BrowsePagerAdapter(Context context, FragmentManager fm, String sortKey) {
        super(fm);
        this.context = context;
        this.sortKey = sortKey;
    }

    @Override
    public Fragment getItem(int position) {
//        Log.d("BrowsePagerAdapter", "sortKey: " + sortKey);
        return BrowsePageFragment.newInstance(position, sortKey);
    }

    @Override
    public int getCount() {
        return context.getResources().getStringArray(R.array.sort_items).length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return context.getResources().getStringArray(R.array.sort_items)[position];
    }
}
