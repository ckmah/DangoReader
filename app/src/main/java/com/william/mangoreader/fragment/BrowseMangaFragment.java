package com.william.mangoreader.fragment;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.william.mangoreader.R;
import com.william.mangoreader.activity.MangoReaderActivity;
import com.william.mangoreader.adapter.CardLayoutAdapter;
import com.william.mangoreader.listener.BrowseMangaScrollListener;
import com.william.mangoreader.adapter.helper.SimpleItemTouchHelperCallback;
import com.william.mangoreader.model.MangaEdenMangaListItem;
import com.william.mangoreader.parse.MangaEden;
import com.william.mangoreader.volley.VolleySingleton;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class BrowseMangaFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private RecyclerView mRecyclerView;
    private GridLayoutManager gridLayoutManager;
    private CardLayoutAdapter cardAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;

    private RequestQueue queue;

    // In-memory list of all manga, period
    private List<MangaEdenMangaListItem> allManga = new ArrayList<>();

    public BrowseMangaFragment() {
        // Required empty public constructor
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

        // Volley request queue
        queue = VolleySingleton.getInstance(getActivity().getApplicationContext()).
                getRequestQueue();

        fetchMangaListFromMangaEden();
        setHasOptionsMenu(true);
        return rootView;
    }

    private void initRecycler(View rootView) {
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.browse_recycler_view);

        gridLayoutManager = new GridLayoutManager(getActivity(), 2);
        mRecyclerView.setLayoutManager(gridLayoutManager);

        MangoReaderActivity activity = (MangoReaderActivity) getActivity();
        cardAdapter = new CardLayoutAdapter(activity, true);
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
    }

    private void fetchMangaListFromMangaEden() {
        String url = MangaEden.MANGAEDEN_MANGALIST;

        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
            }
        });

        queue.add(new JsonObjectRequest(
                        url,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                sortMangaInBackground(response);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                System.out.println("Response error: " + error.toString());
                            }
                        })
        );
    }

    private void sortMangaInBackground(JSONObject response) {
        new AsyncTask<JSONObject, Void, List<MangaEdenMangaListItem>>() {

            @Override
            protected List<MangaEdenMangaListItem> doInBackground(JSONObject... params) {
                // On background thread, sort manga by most to least # of views
                List<MangaEdenMangaListItem> results = MangaEden.parseMangaEdenMangaListResponse(params[0].toString());
                Collections.sort(results, new Comparator<MangaEdenMangaListItem>() {

                    @Override
                    public int compare(MangaEdenMangaListItem lhs, MangaEdenMangaListItem rhs) {
                        return ((Integer) rhs.getHits()).compareTo(lhs.getHits());
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

                BrowseMangaScrollListener.loading = false;
            }
        }.execute(response);
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
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
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
