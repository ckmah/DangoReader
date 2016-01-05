package ckmah.mangoreader.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.william.mangoreader.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ckmah.mangoreader.adapter.MangaItemRowAdapter;
import ckmah.mangoreader.model.MangaEdenMangaChapterItem;
import ckmah.mangoreader.model.MangaEdenMangaDetailItem;
import xyz.danoz.recyclerviewfastscroller.vertical.VerticalRecyclerViewFastScroller;

public class MangaItemChapterFragment extends Fragment {
    private static final String CHAPTER_FRAGMENT_KEY = "chapter_fragment_key";

    private List<MangaEdenMangaChapterItem> chaptersCopy;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager linearLayoutManager;
    //private MangaItemAdapter chapterAdapter;

    public MangaItemChapterFragment() {
        // required empty constructor
    }

    public static MangaItemChapterFragment newInstance(MangaEdenMangaDetailItem mangaDetailItem) {
        MangaItemChapterFragment fragment = new MangaItemChapterFragment();
        fragment.chaptersCopy = new ArrayList<>(mangaDetailItem.getChapters());
        Collections.reverse(fragment.chaptersCopy);
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
        final View rootView = inflater.inflate(R.layout.fragment_manga_item_chapters, container, false);
        initRecycler(rootView);
        return rootView;
    }

    private void initRecycler(View rootView) {

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.chapter_recycler_view);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(linearLayoutManager);
        MangaItemRowAdapter itemRowAdapter = new MangaItemRowAdapter(getActivity(), this);
        itemRowAdapter.setAllChapters(chaptersCopy);
        mRecyclerView.setAdapter(itemRowAdapter);

        VerticalRecyclerViewFastScroller fastScroller = (VerticalRecyclerViewFastScroller) rootView.findViewById(R.id.fast_scroller);
        // Connect the recycler to the scroller (to let the scroller scroll the list)
        fastScroller.setRecyclerView(mRecyclerView);
        // Connect the scroller to the recycler (to let the recycler scroll the scroller's handle)
        mRecyclerView.addOnScrollListener(fastScroller.getOnScrollListener());
    }
}
