package ckmah.mangoreader.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.william.mangoreader.R;

import ckmah.mangoreader.activity.MangaItemActivity;
import ckmah.mangoreader.fragment.LibraryPageFragment;
import ckmah.mangoreader.fragment.MangaItemChapterFragment;
import ckmah.mangoreader.fragment.MangaItemDetailFragment;
import ckmah.mangoreader.model.MangaEdenMangaDetailItem;

/**
 * My Library view pager tabs adapter
 */
public class MangaItemTabbedAdapter extends FragmentStatePagerAdapter {
    private Context context;
    private FragmentManager fm;
    private MangaEdenMangaDetailItem mangaDetailItem;

    public MangaItemTabbedAdapter(Context context, FragmentManager fm, MangaEdenMangaDetailItem mangaDetailItem) {
        super(fm);
        this.context = context;
        this.fm = fm;
        this.mangaDetailItem = mangaDetailItem;
    }

    // TODO repurpose to load details and chapter fragments respectively
    @Override
    public Fragment getItem(int fragmentType) {
        switch (fragmentType) {
            case 0:
                return MangaItemDetailFragment.newInstance(mangaDetailItem);
            case 1:
                return MangaItemChapterFragment.newInstance(mangaDetailItem);
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return context.getResources().getStringArray(R.array.items_categories).length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return context.getResources().getStringArray(R.array.items_categories)[position];
    }
}
