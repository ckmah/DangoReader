package com.william.mangoreader.fragment;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.william.mangoreader.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class MangaItemDescriptionFragment extends Fragment {

    public MangaItemDescriptionFragment() {
        // required empty constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_manga_item, container, false);
    }
}
