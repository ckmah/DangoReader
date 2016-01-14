package ckmah.mangoreader.fragment;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.william.mangoreader.R;

import java.util.Collections;
import java.util.List;

import ckmah.mangoreader.UserLibraryHelper;
import ckmah.mangoreader.adapter.CardLayoutAdapter;
import ckmah.mangoreader.database.Manga;

public class LibraryPageFragment extends Fragment {
    private final static String PAGE_NUM = "ARG_PAGE";

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

        CardLayoutAdapter cgAdapter = new CardLayoutAdapter(getActivity(), false, false);

        List<Manga> library = UserLibraryHelper.findAllFavoritedManga();

        cgAdapter.setAllManga(library);
        cgAdapter.getFilter(2, false, Collections.<Integer>emptyList()).filter(""); // copies allManga to filteredManga, sorted alphabetically
        mRecyclerView.setAdapter(cgAdapter);

        return rootView;
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
