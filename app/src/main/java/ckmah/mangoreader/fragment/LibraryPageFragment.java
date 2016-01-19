package ckmah.mangoreader.fragment;


import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;

import com.william.mangoreader.R;

import ckmah.mangoreader.UserLibraryHelper;
import ckmah.mangoreader.adapter.helper.SortOrder;

public class LibraryPageFragment extends SearchSortFragment {
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

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.browse_recycler_view);
        allManga = UserLibraryHelper.findAllFavoritedManga();

        super.init(false);
        return rootView;
    }

    @Override
    public Filter getFilter() {
        // Sort My Library by most recently updated first, by default
        return cardAdapter.getFilter(SortOrder.LAST_UPDATED);
    }
}
