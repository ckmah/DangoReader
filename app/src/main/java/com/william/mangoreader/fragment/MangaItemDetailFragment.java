package com.william.mangoreader.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.graphics.Palette;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.william.mangoreader.R;
import com.william.mangoreader.activity.MangaItemActivity;
import com.william.mangoreader.model.MangaEdenMangaDetailItem;

public class MangaItemDetailFragment extends Fragment {
    private static final String DESCRIPTION_FRAGMENT_KEY = "description_fragment_key";
    private static final String SWATCH_KEY = "swatch_key";
    private MangaEdenMangaDetailItem mangaDetailItem;

    private View rootView;
    private TextView titleView;
    private TextView subtitleView;
    private TextView descriptionView;

    private Palette.Swatch secondaryColor;

    public MangaItemDetailFragment() {
        //required empty constructor
    }

    public static MangaItemDetailFragment newInstance(MangaEdenMangaDetailItem mangaDetailItem) {
        MangaItemDetailFragment fragment = new MangaItemDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(DESCRIPTION_FRAGMENT_KEY, mangaDetailItem);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        mangaDetailItem = (MangaEdenMangaDetailItem) getArguments().getSerializable(DESCRIPTION_FRAGMENT_KEY);
        rootView = inflater.inflate(R.layout.fragment_details, container, false);

        titleView = (TextView) rootView.findViewById(R.id.manga_item_title);
        titleView.setText(mangaDetailItem.getTitle());

        subtitleView = (TextView) rootView.findViewById(R.id.manga_item_subtitle);
        subtitleView.setText(mangaDetailItem.getAuthor());

        descriptionView = (TextView) rootView.findViewById(R.id.manga_item_description);
        descriptionView.setText(mangaDetailItem.getDescription());

        setLayoutColors();

        return rootView;
    }

    public void setLayoutColors() {
        secondaryColor = ((MangaItemActivity) getActivity()).getSecondaryColor();

        rootView.setBackgroundColor(secondaryColor.getRgb());
        titleView.setTextColor(secondaryColor.getTitleTextColor());
        subtitleView.setTextColor(secondaryColor.getTitleTextColor());
        descriptionView.setTextColor(secondaryColor.getBodyTextColor());
    }
}