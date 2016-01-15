package ckmah.mangoreader.fragment;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Toast;

import com.william.mangoreader.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import ckmah.mangoreader.database.Manga;
import ckmah.mangoreader.parse.MangaEden;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class BrowsePageFragment extends SearchSortFragment {

    private static final String PAGE_NUM = "PAGE_NUM";
    private static final String SORT_KEY = "SORT_KEY";
    private int pageNumber;
    private String sortKey;

    List<String> sortItems;
    List<String> genreList;
    int sortIndex;
    int genreIndex;

    public BrowsePageFragment() {
        // Required empty public constructor
    }

    public static BrowsePageFragment newInstance(int page, String sortKey) {
        BrowsePageFragment fragment = new BrowsePageFragment();
        Bundle args = new Bundle();
        args.putInt(PAGE_NUM, page);
        args.putString(SORT_KEY, sortKey);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageNumber = getArguments().getInt(PAGE_NUM);
        sortKey = getArguments().getString(SORT_KEY);
        allManga = BrowseMangaFragment.allManga;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_browse_page, container, false);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.browse_page_recycler_view);
        initSwipeRefresh(rootView);

        sortItems = new ArrayList<>(Arrays.asList(getActivity().getResources().getStringArray(R.array.sort_items)));
        genreList = new ArrayList<>(Arrays.asList(getActivity().getResources().getStringArray(R.array.genre_list)));
        sortIndex = sortItems.indexOf(sortKey);
        genreIndex = genreList.indexOf(sortKey);

        if (allManga.size() == 0) {
            // If allManga is empty, repopulate the list with an API call, relying on cache if possible
            fetchMangaListFromMangaEden(false);
        }

        super.init(true);
        return rootView;
    }

    private void initSwipeRefresh(View rootView) {
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.browse_page_swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // If user swiped down to refresh, force a check for updates
                fetchMangaListFromMangaEden(true);
            }
        });
    }

    private void fetchMangaListFromMangaEden(boolean skipCache) {
        Log.d("BrowseMangaFragment", "Fetching manga list");

        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
            }
        });

        MangaEden.getMangaEdenService(getActivity(), skipCache).listAllManga().enqueue(new Callback<MangaEden.MangaEdenList>() {
            @Override
            public void onResponse(Response<MangaEden.MangaEdenList> response, Retrofit retrofit) {
                sortMangaInBackground(response.body());
            }

            @Override
            public void onFailure(Throwable t) {
                Log.d("BrowseMangaFragment", "Could not fetch manga list");

                // Hide the refresh layout
                swipeRefreshLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
            }
        });
    }

    private void sortMangaInBackground(MangaEden.MangaEdenList list) {
        Log.d("BrowseMangaFragment", "Sorting manga in background");
        new AsyncTask<MangaEden.MangaEdenList, Void, List<Manga>>() {

            @Override
            protected List<Manga> doInBackground(MangaEden.MangaEdenList... params) {
                // On background thread, sort manga by most to least # of views
                return MangaEden.convertMangaListItemsToManga(params[0].manga);
            }

            @Override
            protected void onPostExecute(List<Manga> results) {
                // On UI thread, update list of all manga and display them
                allManga.clear();
                allManga.addAll(results);
                getFilter().filter("");

                // Hide the refresh layout
                swipeRefreshLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });

                Log.d("BrowseMangaFragment", "Finished sorting manga");
            }
        }.execute(list);
    }

    @Override
    public Filter getFilter() {
        // sort by order only
        if (sortIndex != -1) {
            Log.d("BrowsePageFragment", "sortIndex: " + sortIndex);
            return cardAdapter.getFilter(sortIndex, false, Collections.<Integer>emptyList());
        }
        // sort by genre and order
        else if (genreIndex != -1) {
            Log.d("BrowsePageFragment", "genreIndex: " + genreIndex);
            Log.d("BrowsePageFragment", "pageNumber: " + pageNumber);
            List<Integer> selectedGenre = new ArrayList<>();
            selectedGenre.add(genreIndex);
            return cardAdapter.getFilter(pageNumber, false, selectedGenre);
        } else {
            Log.d("BrowsePageFragment", "genreIndex and sortIndex = -1");
            return cardAdapter.getFilter();
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            Toast.makeText(getContext(), "" + pageNumber, Toast.LENGTH_SHORT).show();
        } else {
        }
    }
}
