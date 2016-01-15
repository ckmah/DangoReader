package ckmah.mangoreader.fragment;

import android.app.Activity;
import android.content.Intent;
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
import android.widget.TextView;

import com.william.mangoreader.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ckmah.mangoreader.activity.BrowseMangaActivity;
import ckmah.mangoreader.adapter.CardLayoutAdapter;
import ckmah.mangoreader.adapter.GenreTextAdapter;
import ckmah.mangoreader.database.Manga;
import ckmah.mangoreader.parse.MangaEden;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class BrowseMangaFragment extends Fragment {

    private View rootView;
    private View contentView;

    private CardLayoutAdapter updatesCardAdapter;
    private CardLayoutAdapter popularCardAdapter;
    private CardLayoutAdapter alphabetCardAdapter;

    private SwipeRefreshLayout swipeRefreshLayout;

    // In-memory list of all manga, period
    public static List<Manga> allManga = new ArrayList<>();

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

        rootView = inflater.inflate(R.layout.fragment_browse_manga_fancy, container, false);
        contentView = rootView.findViewById(R.id.browse_content);
        initSwipeRefresh();
        initRecyclerViews();

        if (allManga.size() > 0) {
            // If allManga is already populated, just display them and hide the placeholders
            sortAdapterData();
            replacePlaceholders();
        } else {
            contentView.setVisibility(View.GONE);
            // Repopulate the list with an API call, relying on cache if possible
            fetchMangaListFromMangaEden(false);
        }

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Browse");
        setHasOptionsMenu(true);
        return rootView;
    }

    private void initRecyclerViews() {
        // display all genres
        RecyclerView genreRecyclerView = (RecyclerView) rootView.findViewById(R.id.genre_recycler_view);
        genreRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        GenreTextAdapter genreTextAdapter = new GenreTextAdapter(getActivity());
        genreRecyclerView.setAdapter(genreTextAdapter);

        View recentlyUpdatedRow = rootView.findViewById(R.id.recently_updated_row);
        View popularRow = rootView.findViewById(R.id.popular_row);
        View alphabetRow = rootView.findViewById(R.id.alphabet_row);

        ((TextView) recentlyUpdatedRow.findViewById(R.id.sectionTitle)).setText(R.string.recently_updated);
        ((TextView) popularRow.findViewById(R.id.sectionTitle)).setText(R.string.popular);
        ((TextView) alphabetRow.findViewById(R.id.sectionTitle)).setText(R.string.a_to_z);

        // initialize category rows
        RecyclerView updatesRecyclerView = (RecyclerView) recentlyUpdatedRow.findViewById(R.id.row_recycler_view);
        RecyclerView popularRecyclerView = (RecyclerView) popularRow.findViewById(R.id.row_recycler_view);
        RecyclerView alphabetRecyclerView = (RecyclerView) alphabetRow.findViewById(R.id.row_recycler_view);

        updatesRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        popularRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        alphabetRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));

        updatesCardAdapter = new CardLayoutAdapter(getActivity(), true, true);
        popularCardAdapter = new CardLayoutAdapter(getActivity(), true, true);
        alphabetCardAdapter = new CardLayoutAdapter(getActivity(), true, true);

        updatesCardAdapter.setAllManga(allManga);
        popularCardAdapter.setAllManga(allManga);
        alphabetCardAdapter.setAllManga(allManga);

        updatesRecyclerView.setAdapter(updatesCardAdapter);
        popularRecyclerView.setAdapter(popularCardAdapter);
        alphabetRecyclerView.setAdapter(alphabetCardAdapter);

        View updatesHeaderView = recentlyUpdatedRow.findViewById(R.id.row_header);
        View popularHeaderView = popularRow.findViewById(R.id.row_header);
        View alphabetHeaderView = alphabetRow.findViewById(R.id.row_header);

        View[] headerViews = {updatesHeaderView, popularHeaderView, alphabetHeaderView};

        for (int index = 0; index < headerViews.length; index++) {
            final int finalIndex = index;

            headerViews[index].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), BrowseMangaActivity.class);
                    String browseOrder = "";
                    switch (finalIndex) {
                        case 0: // recently updated
                            browseOrder = getString(R.string.recently_updated);
                            break;
                        case 1: // popular
                            browseOrder = getString(R.string.popular);
                            break;
                        case 2: // alphabetical
                            browseOrder = getString(R.string.a_to_z);
                            break;
                    }
                    intent.putExtra(getString(R.string.browse_order), browseOrder);
                    getActivity().startActivity(intent);
                }
            });
        }

    }

    private void initSwipeRefresh() {
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.browse_swipe_refresh);
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

                // show actual content
                replacePlaceholders();

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
        // Sort by recently updated.
        updatesCardAdapter.getFilter(1, false, Collections.<Integer>emptyList()).filter("");
        // Sort by popularity.
        popularCardAdapter.getFilter(0, false, Collections.<Integer>emptyList()).filter("");
        // Sort alphabetical.
        alphabetCardAdapter.getFilter(2, false, Collections.<Integer>emptyList()).filter("");
    }

    private void replacePlaceholders() {
        rootView.findViewById(R.id.browse_placeholder).setVisibility(View.GONE);
        contentView.setVisibility(View.VISIBLE);

        Log.d("BrowseMangaFragment", "content visibile: " + contentView.getVisibility());
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