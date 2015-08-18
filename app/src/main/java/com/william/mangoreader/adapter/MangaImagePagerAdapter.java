package com.william.mangoreader.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.View;

import com.william.mangoreader.fragment.MangaImageFragment;

public class MangaImagePagerAdapter extends FragmentStatePagerAdapter {

    private int size;
    private FragmentManager fm;

    public MangaImagePagerAdapter(FragmentManager fm, int size) {
        super(fm);
        this.fm = fm;
        this.size = size;
    }

    @Override
    public int getCount() {
        return size;
    }

    @Override
    public Fragment getItem(int position) {
        return MangaImageFragment.newInstance(position);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return false;
    }


}
