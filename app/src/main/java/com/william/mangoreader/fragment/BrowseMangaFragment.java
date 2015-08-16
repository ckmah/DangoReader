package com.william.mangoreader.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.william.mangoreader.R;
import com.william.mangoreader.activity.MangoReaderActivity;
import com.william.mangoreader.adapter.CardLayoutAdapter;
import com.william.mangoreader.listener.BrowseMangaScrollListener;
import com.william.mangoreader.model.MangaEdenMangaListItem;
import com.william.mangoreader.parse.MangaEden;
import com.william.mangoreader.volley.VolleySingleton;

import org.json.JSONObject;

import java.util.ArrayList;

public class BrowseMangaFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private RecyclerView mRecyclerView;
    private LinearLayoutManager linearLayoutManager;
    private CardLayoutAdapter cgAdapter;
    BrowseMangaScrollListener recyclerListener;
    private SwipeRefreshLayout swipeRefreshLayout;

    private RequestQueue queue;
    private int page;

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

        linearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(linearLayoutManager);

        MangoReaderActivity activity = (MangoReaderActivity) getActivity();
        cgAdapter = new CardLayoutAdapter(activity, true);
        mRecyclerView.setAdapter(cgAdapter);

        recyclerListener = new BrowseMangaScrollListener() {
            @Override
            public void loadMore() {
                fetchMangaListFromMangaEden();
            }
        };

        mRecyclerView.addOnScrollListener(recyclerListener);

    }

    private void initSwipeRefresh(View rootView) {
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.browse_swipe_refresh);

        swipeRefreshLayout.setOnRefreshListener(this);

    }

    private void fetchMangaListFromMangaEden() {
        String url = MangaEden.MANGAEDEN_MANGALIST_PREFIX + page + MangaEden.MANGAEDEN_MANGALIST_SUFFIX;

        queue.add(new JsonObjectRequest
                        (url, new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject response) {

                                ArrayList<MangaEdenMangaListItem> results = MangaEden.parseMangaEdenMangaListResponse(response.toString());

                                for (MangaEdenMangaListItem m : results) {
                                    cgAdapter.addItem(m);
                                }
                                page++;
                                BrowseMangaScrollListener.loading = false;

                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                System.out.println("Response error: " + error.toString());
                            }
                        })
        );
    }

    @Override
    public void onRefresh() {
        page = 0;
        cgAdapter.clearList();
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuinflator) {
        menuinflator.inflate(R.menu.menu_browse_manga, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        if (id == R.id.action_search) {
            Toast.makeText(getActivity().getApplicationContext(), "Search action is selected!", Toast.LENGTH_SHORT).show();
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
