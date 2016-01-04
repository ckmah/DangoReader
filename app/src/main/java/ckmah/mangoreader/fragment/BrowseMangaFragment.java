package ckmah.mangoreader.fragment;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.william.mangoreader.R;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import ckmah.mangoreader.adapter.CardLayoutAdapter;
import ckmah.mangoreader.adapter.helper.SimpleItemTouchHelperCallback;
import ckmah.mangoreader.model.MangaEdenMangaListItem;
import ckmah.mangoreader.parse.MangaEden;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class BrowseMangaFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private RecyclerView mRecyclerView;
    private GridLayoutManager gridLayoutManager;
    private CardLayoutAdapter cardAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;

    // In-memory list of all manga, period
    private List<MangaEdenMangaListItem> allManga = new ArrayList<>();

    public BrowseMangaFragment() {
        // Required empty public constructor
    }

    private static BrowseMangaFragment instance;
    public static BrowseMangaFragment getInstance() {
        if (instance == null) {
            instance = new BrowseMangaFragment();
        }
        return instance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_browse_manga, container, false);
        initRecycler(rootView);
        initSwipeRefresh(rootView);

        if (allManga.size() > 0) {
            // If allManga is already populated, just display them
            cardAdapter.getFilter().filter("");
        } else if (savedInstanceState != null) {
            // If the previous list was saved (e.g. on device rotation), use and display them
            allManga.addAll((List<MangaEdenMangaListItem>) savedInstanceState.getSerializable("allManga"));
            cardAdapter.getFilter().filter("");
        } else {
            // Repopulate the list with an API call
            fetchMangaListFromMangaEden();
        }

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Browse");
        setHasOptionsMenu(true);
        return rootView;
    }

    private void initRecycler(View rootView) {
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.browse_recycler_view);

        gridLayoutManager = new GridLayoutManager(getActivity(), 2);
        mRecyclerView.setLayoutManager(gridLayoutManager);

        cardAdapter = new CardLayoutAdapter(getActivity(), this);
        cardAdapter.setAllManga(allManga);
        mRecyclerView.setAdapter(cardAdapter);

        ItemTouchHelper.Callback callback =
                new SimpleItemTouchHelperCallback(cardAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(mRecyclerView);
    }

    private void initSwipeRefresh(View rootView) {
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.browse_swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(
                R.color.colorPrimary,
                R.color.colorPrimaryDark,
                R.color.textColorSecondary);
    }

    private void fetchMangaListFromMangaEden() {
        Log.d("SORTING", "FETCHING");

        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
            }
        });

        MangaEden.getMangaEdenService(getActivity()).listAllManga().enqueue(new Callback<MangaEden.MangaEdenList>() {
            @Override
            public void onResponse(Response<MangaEden.MangaEdenList> response, Retrofit retrofit) {
                response.body();
                sortMangaInBackground(response.body());
            }

            @Override
            public void onFailure(Throwable t) {
                Log.d("SORTING", "FAILED");
                Log.d("SORTING", t.getMessage());

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
        Log.d("SORTING", "STARTING");
        new AsyncTask<MangaEden.MangaEdenList, Void, List<MangaEdenMangaListItem>>() {

            @Override
            protected List<MangaEdenMangaListItem> doInBackground(MangaEden.MangaEdenList... params) {
                // On background thread, sort manga by most to least # of views
                List<MangaEdenMangaListItem> results = params[0].manga;
                Collections.sort(results, new Comparator<MangaEdenMangaListItem>() {

                    @Override
                    public int compare(MangaEdenMangaListItem lhs, MangaEdenMangaListItem rhs) {
                        return ((Integer) rhs.hits).compareTo(lhs.hits);
                    }
                });
                return results;
            }

            @Override
            protected void onPostExecute(List<MangaEdenMangaListItem> results) {
                // On UI thread, update list of all manga and display them
                allManga.clear();
                allManga.addAll(results);
                cardAdapter.getFilter().filter("");


                // Hide the refresh layout
                swipeRefreshLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });

                Log.d("SORTING", "ENDING");
            }
        }.execute(list);
    }

    @Override
    public void onRefresh() {
        fetchMangaListFromMangaEden();
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
        sortItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                DialogFragment dialog = SortDialogFragment.newInstance(cardAdapter);
                dialog.show(getActivity().getSupportFragmentManager(), "SortDialogFragment");
                return false;
            }
        });
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

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the list, if activity is being destroyed due to lack of resources (TODO or rotation?)
        savedInstanceState.putSerializable("allManga", (Serializable) allManga);

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }
}
