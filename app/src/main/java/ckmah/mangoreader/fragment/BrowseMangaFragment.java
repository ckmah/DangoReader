package ckmah.mangoreader.fragment;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.william.mangoreader.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ckmah.mangoreader.adapter.CardLayoutAdapter;
import ckmah.mangoreader.database.Manga;
import ckmah.mangoreader.parse.MangaEden;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class BrowseMangaFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private CardLayoutAdapter updatesCardAdapter;
    private CardLayoutAdapter popularCardAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;

    // In-memory list of all manga, period
    private List<Manga> allManga = new ArrayList<>();

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

        final View rootView = inflater.inflate(R.layout.fragment_browse_manga_fancy, container, false);
        initSwipeRefresh(rootView);
        initRecyclerViews(rootView);

        if (allManga.size() > 0) {
            // If allManga is already populated, just display them
            sortAdapterData();

        } else {
            // Repopulate the list with an API call
            fetchMangaListFromMangaEden();
        }

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Browse");
        setHasOptionsMenu(true);
        return rootView;
    }

    private void initRecyclerViews(View rootView) {
        RecyclerView updatesRecyclerView = (RecyclerView) rootView.findViewById(R.id.recently_updated_row).findViewById(R.id.row_recycler_view);
        RecyclerView popularRecyclerView = (RecyclerView) rootView.findViewById(R.id.popular_row).findViewById(R.id.row_recycler_view);
        RecyclerView genreRecyclerView = (RecyclerView) rootView.findViewById(R.id.genre_row).findViewById(R.id.row_recycler_view);

        updatesRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        popularRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        genreRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));

        updatesCardAdapter = new CardLayoutAdapter(getActivity(), this);
        popularCardAdapter = new CardLayoutAdapter(getActivity(), this);

        updatesCardAdapter.setAllManga(allManga);
        popularCardAdapter.setAllManga(allManga);

        updatesRecyclerView.setAdapter(updatesCardAdapter);
        popularRecyclerView.setAdapter(popularCardAdapter);

        // TODO genre row
    }

    private void initSwipeRefresh(View rootView) {
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.browse_swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(this);
    }

    private void fetchMangaListFromMangaEden() {
        Log.d("BrowseMangaFragment", "Fetching manga list");

        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
            }
        });

        MangaEden.getMangaEdenService(getActivity()).listAllManga().enqueue(new Callback<MangaEden.MangaEdenList>() {
            @Override
            public void onResponse(Response<MangaEden.MangaEdenList> response, Retrofit retrofit) {
                getMangaInBackground(response.body());
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

    private void getMangaInBackground(MangaEden.MangaEdenList list) {
        Log.d("BrowseMangaFragment", "Sorting manga in background");
        new AsyncTask<MangaEden.MangaEdenList, Void, List<Manga>>() {
            //TODO add manga to respective recyclerviews

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
                sortAdapterData();
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

    private void sortAdapterData() {
        popularCardAdapter.getFilter(0, false, Collections.<Integer>emptyList()).filter("");
        updatesCardAdapter.getFilter(1, false, Collections.<Integer>emptyList()).filter("");
    }

    @Override
    public void onRefresh() {
        fetchMangaListFromMangaEden();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuinflator) {
//        menuinflator.inflate(R.menu.menu_browse_manga, menu);
//
//        // Configure the SearchView to filter the cards
//        MenuItem searchItem = menu.findItem(R.id.action_search);
//        SearchView searchView = (SearchView) searchItem.getActionView();
//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                if (query.isEmpty()) {
//                    swipeRefreshLayout.setEnabled(true);
//                } else {
//                    swipeRefreshLayout.setEnabled(false);
//                }
//                popularCardAdapter.getFilter().filter(query);
//                return true; // The listener has handled the query
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                if (newText.isEmpty()) {
//                    swipeRefreshLayout.setEnabled(true);
//                } else {
//                    swipeRefreshLayout.setEnabled(false);
//                }
//                popularCardAdapter.getFilter().filter(newText);
//                return false; // The searchview should show suggestions
//            }
//        });
//
//        MenuItem sortItem = menu.findItem(R.id.action_sort);
//        sortItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(MenuItem item) {
//                DialogFragment dialog = SortDialogFragment.newInstance(popularCardAdapter);
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
