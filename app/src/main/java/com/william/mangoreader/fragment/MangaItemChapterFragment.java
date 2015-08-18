package com.william.mangoreader.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.william.mangoreader.R;
import com.william.mangoreader.activity.MangaViewerActivity;
import com.william.mangoreader.model.MangaEdenMangaChapterItem;
import com.william.mangoreader.model.MangaEdenMangaDetailItem;

public class MangaItemChapterFragment extends Fragment {
    private static final String CHAPTER_FRAGMENT_KEY = "chapter_fragment_key";
    private MangaEdenMangaDetailItem mangaDetailItem;

    public MangaItemChapterFragment() {
        // required empty constructor
    }

    public static MangaItemChapterFragment newInstance(MangaEdenMangaDetailItem mangaDetailItem) {
        MangaItemChapterFragment fragment = new MangaItemChapterFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_chapters, container, false);

        for (final MangaEdenMangaChapterItem chapterItem : mangaDetailItem.getChapters()) {
            View chapterRow = inflater.inflate(R.layout.chapter_row, null);

            chapterRow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(container.getContext(), MangaViewerActivity.class);
                    intent.putExtra("chapterId", chapterItem.getId());
                    container.getContext().startActivity(intent);
                }
            });

            TextView chapterNumber = (TextView) chapterRow.findViewById(R.id.chapter_number);
            TextView chapterTitle = (TextView) chapterRow.findViewById(R.id.chapter_title);

            chapterNumber.setText("Chapter " + chapterItem.getNumber());
            chapterTitle.setText(chapterItem.getTitle());

            ViewGroup insertPoint = (ViewGroup) rootView;

            insertPoint.addView(chapterRow);
        }

        return rootView;
    }
}
