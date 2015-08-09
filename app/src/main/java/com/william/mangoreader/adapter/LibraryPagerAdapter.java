package com.william.mangoreader.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import com.william.mangoreader.R;
import com.william.mangoreader.fragment.LibraryPageFragment;

/**
 * My Library view pager tabs adapter
 */
public class LibraryPagerAdapter extends FragmentStatePagerAdapter {
    private Context context;
    private FragmentManager fm;

    public LibraryPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        this.context = context;
        this.fm = fm;
    }

    @Override
    public Fragment getItem(int position) {
        return LibraryPageFragment.newInstance(position + 1);
    }

    @Override
    public int getCount() {
        return 4;
    }

    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return context.getString(R.string.library_page1);
            case 1:
                return context.getString(R.string.library_page2);
            case 2:
                return context.getString(R.string.library_page3);
            case 3:
                return context.getString(R.string.library_page4);
            default:
                return null;
        }
    }
}
