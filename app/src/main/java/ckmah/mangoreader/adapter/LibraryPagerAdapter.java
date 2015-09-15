package ckmah.mangoreader.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.william.mangoreader.R;

import ckmah.mangoreader.fragment.LibraryPageFragment;

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
        return LibraryPageFragment.newInstance(position);
    }

    @Override
    public int getCount() {
        return context.getResources().getStringArray(R.array.library_categories).length;
    }

    public CharSequence getPageTitle(int position) {
        return context.getResources().getStringArray(R.array.library_categories)[position];
    }
}
