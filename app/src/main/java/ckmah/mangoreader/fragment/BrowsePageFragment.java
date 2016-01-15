package ckmah.mangoreader.fragment;


import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.william.mangoreader.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import ckmah.mangoreader.adapter.CardLayoutAdapter;
import ckmah.mangoreader.database.Manga;
import ckmah.mangoreader.parse.MangaEden;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class BrowsePageFragment extends Fragment {

    private CardLayoutAdapter cardAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;

    // In-memory list of all manga, period
    private List<Manga> allManga = new ArrayList<>();

    private static final String PAGE_NUM = "PAGE_NUM";
    private static final String SORT_KEY = "SORT_KEY";
    private int pageNumber;
    private String sortKey;

    static ArrayList<String> sortItems;
    static ArrayList<String> genreList;
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

        initRecycler(rootView);
        initSwipeRefresh(rootView);

        sortItems = new ArrayList<>(Arrays.asList(getActivity().getResources().getStringArray(R.array.sort_items)));
        genreList = new ArrayList<>(Arrays.asList(getActivity().getResources().getStringArray(R.array.genre_list)));
        sortIndex = sortItems.indexOf(sortKey);
        genreIndex = genreList.indexOf(sortKey);

        if (allManga.size() > 0) {
            // If allManga is already populated, just display them
            sortOrderAndGenre();
        } else {
            // Repopulate the list with an API call, relying on cache if possible
            fetchMangaListFromMangaEden(false);
        }

        setHasOptionsMenu(true);
        return rootView;
    }

    private void initRecycler(View rootView) {
        RecyclerView mRecyclerView = (RecyclerView) rootView.findViewById(R.id.browse_page_recycler_view);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
        mRecyclerView.setLayoutManager(gridLayoutManager);

        cardAdapter = new CardLayoutAdapter(getActivity(), true, false);
        cardAdapter.setAllManga(allManga);
        mRecyclerView.setAdapter(cardAdapter);

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

                sortOrderAndGenre();

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

    private void sortOrderAndGenre() {
        Log.d("BrowsePageFragment", "sortKey: " + sortKey);
        // sort by order only
        if (sortIndex != -1) {
            Log.d("BrowsePageFragment", "sortIndex: " + sortIndex);
            cardAdapter.getFilter(sortIndex, false, Collections.<Integer>emptyList()).filter("");
        }
        // sort by genre and order
        else if (genreIndex != -1) {
            Log.d("BrowsePageFragment", "genreIndex: " + genreIndex);
            Log.d("BrowsePageFragment", "pageNumber: " + pageNumber);
            List<Integer> selectedGenre = new ArrayList<>();
            selectedGenre.add(genreIndex);
            cardAdapter.getFilter(pageNumber, false, selectedGenre).filter("");
        } else {
            Log.d("BrowsePageFragment", "genreIndex and sortIndex = -1");
            cardAdapter.getFilter().filter("");
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuinflator) {
        menuinflator.inflate(R.menu.menu_browse_manga, menu);

        // Configure the SearchView to filter the cards
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query.isEmpty()) {
                    swipeRefreshLayout.setEnabled(true);
                } else {
                    swipeRefreshLayout.setEnabled(false);
                }
                cardAdapter.getFilter().filter(query);
                return true; // The listener has handled the query
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    swipeRefreshLayout.setEnabled(true);
                } else {
                    swipeRefreshLayout.setEnabled(false);
                }
                cardAdapter.getFilter().filter(newText);
                return false; // The searchview should show suggestions
            }
        });

        MenuItem sortItem = menu.findItem(R.id.action_sort);
//        sortItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(MenuItem item) {
//                DialogFragment dialog = SortDialogFragment.newInstance(cardAdapter);
//                dialog.show(getActivity().getSupportFragmentManager(), "SortDialogFragment");
//                return false;
//            }
//        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
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
