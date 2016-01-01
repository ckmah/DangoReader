package ckmah.mangoreader.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.william.mangoreader.R;

import ckmah.mangoreader.adapter.MangaItemAdapter;
import ckmah.mangoreader.model.MangaEdenMangaDetailItem;

public class MangaItemChapterFragment extends Fragment {
    private static final String CHAPTER_FRAGMENT_KEY = "chapter_fragment_key";
    private MangaEdenMangaDetailItem mangaDetailItem;

    private RecyclerView mRecyclerView;
    private LinearLayoutManager linearLayoutManager;
    private MangaItemAdapter chapterAdapter;

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
//        View rootView = inflater.inflate(R.layout.fragment_chapters, container, false);
//
//        initRecycler(rootView);
//        return rootView;
        return null;
    }

    private void initRecycler(View rootView) {
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.manga_item_recycler_view);

        linearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(linearLayoutManager);

        chapterAdapter = new MangaItemAdapter(getActivity(), mangaDetailItem);

        mRecyclerView.setAdapter(chapterAdapter);
    }
}
