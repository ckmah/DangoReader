package com.william.mangoreader.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.william.mangoreader.fragment.MangaImageFragment;

public class MangaImagePagerAdapter extends FragmentStatePagerAdapter {

    private int size;

    public MangaImagePagerAdapter(FragmentManager fm, int size) {
        super(fm);
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
}
