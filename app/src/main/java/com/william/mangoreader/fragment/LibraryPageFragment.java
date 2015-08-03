package com.william.mangoreader.fragment;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.william.mangoreader.R;
import com.william.mangoreader.activity.MangoReaderActivity;
import com.william.mangoreader.adapter.CardLayoutAdapter;
import com.william.mangoreader.db.EntriesDataSource;
import com.william.mangoreader.model.MangaCardItem;

import java.util.ArrayList;

public class LibraryPageFragment extends Fragment {

    private final int INIT_QUANTITY = 25;
    private final int QUERY_QUANTITY = 10;

    ArrayList<MangaCardItem> mData = new ArrayList<MangaCardItem>();

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

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        MangoReaderActivity main_activity = (MangoReaderActivity) getActivity();
        EntriesDataSource mangadb = main_activity.getUserDB();

        CardLayoutAdapter cgAdapter = new CardLayoutAdapter(mangadb, getActivity());
        mRecyclerView.setAdapter(cgAdapter);

        for (MangaCardItem m : mangadb.getAllEntries())
            cgAdapter.addItem(m);

//        // TODO: asynchronous loading

        // Inflate the layout for this fragment
        return rootView;
    }

    // Called when add button is clicked.
    public void addItem(CardLayoutAdapter adapter) {

        // Add data locally to the list.
        MangaCardItem mangaItem = new MangaCardItem();
//        mangaItem.title = "MangaTitle";
//        mData.add(mangaItem);

        // Update adapter.
        adapter.addItem(mangaItem);
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
