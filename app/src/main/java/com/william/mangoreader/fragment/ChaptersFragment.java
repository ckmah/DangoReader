package com.william.mangoreader.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.william.mangoreader.R;
import com.william.mangoreader.model.MangaEdenMangaChapterItem;
import com.william.mangoreader.model.MangaEdenMangaDetailItem;

public class ChaptersFragment extends Fragment {
    private static final String CHAPTER_FRAGMENT_KEY = "chapter_fragment_key";
    private MangaEdenMangaDetailItem mangaDetailItem;

    public ChaptersFragment() {
        // required empty constructor
    }

    public static ChaptersFragment newInstance(MangaEdenMangaDetailItem mangaDetailItem) {
        ChaptersFragment fragment = new ChaptersFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(CHAPTER_FRAGMENT_KEY, mangaDetailItem);
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

        mangaDetailItem = (MangaEdenMangaDetailItem) getArguments().getSerializable(CHAPTER_FRAGMENT_KEY);
        final View rootView = inflater.inflate(R.layout.fragment_chapters, container, false);

        for (MangaEdenMangaChapterItem chapterItem : mangaDetailItem.getChapters()) {
            View chapterRow = inflater.inflate(R.layout.chapter_row, null);

            TextView chapterNumber = (TextView) chapterRow.findViewById(R.id.chapter_number);
            TextView chapterTitle = (TextView) chapterRow.findViewById(R.id.chapter_title);

            chapterNumber.setText("" + chapterItem.getNumber());
            chapterTitle.setText(chapterItem.getTitle());

            ViewGroup insertPoint = (ViewGroup) rootView;
            insertPoint.addView(chapterRow);
        }


        return rootView;
    }
}
