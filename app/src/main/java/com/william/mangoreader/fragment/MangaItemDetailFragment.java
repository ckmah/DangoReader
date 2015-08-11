package com.william.mangoreader.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.william.mangoreader.R;
import com.william.mangoreader.model.MangaEdenMangaDetailItem;

public class MangaItemDetailFragment extends Fragment {
    private static final String DESCRIPTION_FRAGMENT_KEY = "description_fragment_key";
    private MangaEdenMangaDetailItem mangaDetailItem;

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
        View rootView = inflater.inflate(R.layout.fragment_details, container, false);

        TextView titleView = (TextView) rootView.findViewById(R.id.manga_item_title);
        titleView.setText(mangaDetailItem.getTitle());

        TextView subtitleView = (TextView) rootView.findViewById(R.id.manga_item_subtitle);
        subtitleView.setText(mangaDetailItem.getAuthor());

        TextView descriptionView = (TextView) rootView.findViewById(R.id.manga_item_description);
        descriptionView.setText(mangaDetailItem.getDescription());

        return rootView;
    }
}
