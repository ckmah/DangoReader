package ckmah.mangoreader.fragment;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.william.mangoreader.R;

import java.util.Collections;
import java.util.List;

import ckmah.mangoreader.UserLibraryHelper;
import ckmah.mangoreader.database.Manga;

public class LibraryPageFragment extends SearchSortFragment {
    private final static String PAGE_NUM = "ARG_PAGE";

    View rootView;

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

        rootView = inflater.inflate(R.layout.card_grid, container, false);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.browse_recycler_view);
        allManga = UserLibraryHelper.findAllFavoritedManga();

        if (allManga.isEmpty()) {
            rootView.findViewById(R.id.empty_library_image).setVisibility(View.VISIBLE);
        }

        super.init();
        // Sort My Library by most recently updated first, by default
        cardAdapter.getFilter(1, false, Collections.<Integer>emptyList()).filter("");

        return rootView;
    }

    /**
     * TODO make this more efficient, figure out what specific call updates view
     */
    @Override
    public void onResume() {
        super.onResume();

        new AsyncTask<Void, Void, List<Manga>>() {
            protected List<Manga> doInBackground(Void... params) {

                return UserLibraryHelper.findAllFavoritedManga();
            }
            protected void onPostExecute(List<Manga> result) {
                allManga = result;
            }
        }.execute();
        cardAdapter.setAllManga(allManga);
        cardAdapter.showAllManga();
        cardAdapter.getFilter(1, false, Collections.<Integer>emptyList()).filter("");

    }
}
