package com.william.mangoreader.fragment;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.william.mangoreader.R;
import com.william.mangoreader.activity.MangoReaderActivity;
import com.william.mangoreader.adapter.CardLayoutAdapter;
import com.william.mangoreader.daogen.UserLibraryManga;
import com.william.mangoreader.model.MangaEdenMangaListItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LibraryPageFragment extends Fragment {
    private final static String PAGE_NUM = "ARG_PAGE";
    private List<UserLibraryManga> userLibraryCategory;

    public static LibraryPageFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(PAGE_NUM, page);
        LibraryPageFragment fragment = new LibraryPageFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.card_grid, container, false);

        RecyclerView mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
        mRecyclerView.setLayoutManager(gridLayoutManager);

        CardLayoutAdapter cgAdapter = new CardLayoutAdapter(getActivity());
        userLibraryCategory = new ArrayList<>();

        int page = getArguments().getInt((PAGE_NUM));
        String category = getResources().getStringArray(R.array.library_categories)[page];
        for (UserLibraryManga manga : MangoReaderActivity.userLibraryMangaDao.loadAll()) {
            if (manga.getTab().compareTo(category) == 0)
                userLibraryCategory.add(manga);
        }

        cgAdapter.setAllManga(convertUserLibraryManga(userLibraryCategory));
        cgAdapter.getFilter().filter(""); // copies allManga to filteredManga
        mRecyclerView.setAdapter(cgAdapter);

        return rootView;
    }

    //TODO refactor this greendao hackiness
    private List<MangaEdenMangaListItem> convertUserLibraryManga(List<UserLibraryManga> libraryMangaList) {
        List<MangaEdenMangaListItem> result = new ArrayList<>();
        for (UserLibraryManga manga : libraryMangaList) {
            MangaEdenMangaListItem mangaListItem = new MangaEdenMangaListItem();
            mangaListItem.title = manga.getTitle();
            mangaListItem.imageUrl = manga.getImageURL();
            mangaListItem.genres = Arrays.asList(manga.getGenres().split("\t"));
            mangaListItem.status = manga.getStatus();
            mangaListItem.hits = manga.getHits();
            mangaListItem.lastChapterDate = manga.getLastChapterDate();
            mangaListItem.id = manga.getMangaEdenId();
            result.add(mangaListItem);
        }
        return result;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
