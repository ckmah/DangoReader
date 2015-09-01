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
import java.util.List;

public class LibraryPageFragment extends Fragment {

    private List<UserLibraryManga> userLibrary;

    public static LibraryPageFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt("ARG_PAGE", page);
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
        //TODO hack methods, refactor later
        userLibrary = ((MangoReaderActivity) getActivity()).userLibraryMangaDao.loadAll();
        cgAdapter.setAllManga(convertUserLibraryManga(userLibrary));
        cgAdapter.getFilter().filter("");
        mRecyclerView.setAdapter(cgAdapter);

        return rootView;
    }

    //TODO more hacks :( holy shit I hate greendao
    private List<MangaEdenMangaListItem> convertUserLibraryManga(List<UserLibraryManga> m) {
        List<MangaEdenMangaListItem> result = new ArrayList<>();
        for (UserLibraryManga temp : m) {
            MangaEdenMangaListItem temp2 = new MangaEdenMangaListItem();
            temp2.title = temp.getTitle();
            temp2.imageUrl = temp.getImageURL();
            temp2.status = temp.getStatus();
            temp2.hits = temp.getHits();
            temp2.lastChapterDate = temp.getLastChapterDate();
            temp2.id = temp.getMangaEdenId();
            result.add(temp2);
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
