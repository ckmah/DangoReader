package ckmah.mangoreader.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.william.mangoreader.R;

import java.util.ArrayList;
import java.util.Collections;

import ckmah.mangoreader.UserLibraryHelper;
import ckmah.mangoreader.adapter.MangaItemRowAdapter;
import ckmah.mangoreader.database.Chapter;
import ckmah.mangoreader.database.Manga;
import io.paperdb.Paper;
import xyz.danoz.recyclerviewfastscroller.vertical.VerticalRecyclerViewFastScroller;

public class MangaItemChapterFragment extends Fragment {
    private static final String CHAPTER_FRAGMENT_KEY = "chapter_fragment_key";

    private Manga manga;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager linearLayoutManager;
    //private MangaItemAdapter chapterAdapter;

    public MangaItemChapterFragment() {
        // required empty constructor
    }

    public static MangaItemChapterFragment newInstance(String mangaId) {
        MangaItemChapterFragment fragment = new MangaItemChapterFragment();
        Bundle bundle = new Bundle();
        bundle.putString(CHAPTER_FRAGMENT_KEY, mangaId);
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
        String mangaId = getArguments().getString(CHAPTER_FRAGMENT_KEY);
        manga = Paper.book(UserLibraryHelper.USER_LIBRARY_DB).read(mangaId);
        final View rootView = inflater.inflate(R.layout.fragment_manga_item_chapters, container, false);
        initRecycler(rootView);
        return rootView;
    }

    private void initRecycler(View rootView) {
        // retrieve chapters
        ArrayList<Chapter> mangaItems = new ArrayList<>(manga.chaptersList);
        Collections.reverse(mangaItems);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.chapter_recycler_view);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(linearLayoutManager);
        MangaItemRowAdapter itemRowAdapter = new MangaItemRowAdapter(
                getActivity(), this, mangaItems, manga.title);
        mRecyclerView.setAdapter(itemRowAdapter);

        VerticalRecyclerViewFastScroller fastScroller = (VerticalRecyclerViewFastScroller) rootView.findViewById(R.id.fast_scroller);
        // Connect the recycler to the scroller (to let the scroller scroll the list)
        fastScroller.setRecyclerView(mRecyclerView);
        // Connect the scroller to the recycler (to let the recycler scroll the scroller's handle)
        mRecyclerView.addOnScrollListener(fastScroller.getOnScrollListener());
    }
}
