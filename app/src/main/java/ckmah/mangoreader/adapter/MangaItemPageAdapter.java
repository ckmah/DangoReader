package ckmah.mangoreader.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.william.mangoreader.R;

import ckmah.mangoreader.fragment.MangaItemChapterFragment;
import ckmah.mangoreader.fragment.MangaItemDetailFragment;

/**
 * My Library view pager tabs adapter
 */
public class MangaItemPageAdapter extends FragmentStatePagerAdapter {
    private Context context;
    private String mangaId;

    public MangaItemPageAdapter(Context context, FragmentManager fm, String mangaId) {
        super(fm);
        this.context = context;
        this.mangaId = mangaId;
    }

    @Override
    public Fragment getItem(int fragmentType) {
        switch (fragmentType) {
            case 0:
                return MangaItemDetailFragment.newInstance(mangaId);
            case 1:
                return MangaItemChapterFragment.newInstance(mangaId);
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
