package moe.dangoreader.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

import io.paperdb.Paper;
import moe.dangoreader.DividerItemDecoration;
import moe.dangoreader.R;
import moe.dangoreader.UserLibraryHelper;
import moe.dangoreader.adapter.MangaItemRowAdapter;
import moe.dangoreader.database.Manga;

public class MangaItemChapterFragment extends Fragment {
    private static final String CHAPTER_FRAGMENT_KEY = "chapter_fragment_key";

    private Manga manga;
    private String mangaId;
    private FastScrollRecyclerView mRecyclerView;
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
        this.mangaId = mangaId;
        manga = Paper.book(UserLibraryHelper.USER_LIBRARY_DB).read(mangaId);
        final View rootView = inflater.inflate(R.layout.fragment_manga_item_chapters, container, false);
        initRecycler(rootView);
        return rootView;
    }

    private void initRecycler(View rootView) {
        // retrieve chapters

        mRecyclerView = (FastScrollRecyclerView) rootView.findViewById(R.id.chapter_recycler_view);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(linearLayoutManager);
        MangaItemRowAdapter itemRowAdapter = new MangaItemRowAdapter(
                getActivity(), manga.chaptersList, manga.id);
        mRecyclerView.setAdapter(itemRowAdapter);

        RecyclerView.ItemDecoration itemDecoration = new
                DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST);
        mRecyclerView.addItemDecoration(itemDecoration);

    }

    @Override
    public void onResume() {
        super.onResume();
        manga = Paper.book(UserLibraryHelper.USER_LIBRARY_DB).read(mangaId);
        MangaItemRowAdapter itemRowAdapter = new MangaItemRowAdapter(
                getActivity(), manga.chaptersList, manga.id);
        mRecyclerView.setAdapter(itemRowAdapter);
    }
}
