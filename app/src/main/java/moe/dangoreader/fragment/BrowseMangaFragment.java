package moe.dangoreader.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import moe.dangoreader.R;
import moe.dangoreader.activity.BrowseMangaActivity;
import moe.dangoreader.adapter.CardLayoutAdapter;
import moe.dangoreader.adapter.GenreTextAdapter;
import moe.dangoreader.adapter.helper.SortOrder;
import moe.dangoreader.database.Manga;
import moe.dangoreader.parse.MangaEden;
import moe.dangoreader.parse.PaletteTransformation;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class BrowseMangaFragment extends Fragment {
    private View rootView;
    private View contentView;
    private RecyclerView searchRecyclerView;
    private View placeholder;
    private CardLayoutAdapter updatesCardAdapter;
    private CardLayoutAdapter popularCardAdapter;
    private CardLayoutAdapter alphabetCardAdapter;
    private CardLayoutAdapter searchCardAdapter;

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
        placeholder = rootView.findViewById(R.id.browse_placeholder);
        searchRecyclerView = (RecyclerView) rootView.findViewById(R.id.browse_search);

        Picasso.with(getActivity())
                .load(R.drawable.logo_placeholder)
                .fit().centerCrop()
                .transform(PaletteTransformation.instance())
                .into((ImageView) placeholder.findViewById(R.id.placeholder_image));

        initRecyclerViews();
        initSearchRecyclerView();

        // If allManga is already populated, just display them
        if (allManga.size() > 0) {
            sortAdapterData();
            showContent();
        } else {
            showPlaceholder();
            // Repopulate the list with an API call, relying on cache if possible
            fetchMangaListFromMangaEden(false);
        }

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.title_browse);
        setHasOptionsMenu(true);
        return rootView;
    }

    private void initSearchRecyclerView() {
        // Mostly duplicate of SearchSortFragment code -- TODO possible to refactor?
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
        searchRecyclerView.setLayoutManager(gridLayoutManager);

        searchCardAdapter = new CardLayoutAdapter(getActivity(), true, false);
        searchCardAdapter.setAllManga(allManga);
        searchRecyclerView.setAdapter(searchCardAdapter);
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

    private void fetchMangaListFromMangaEden(boolean skipCache) {
        Log.d("BrowseMangaFragment", "Fetching manga list");

        MangaEden.getMangaEdenService(getActivity(), skipCache).listAllManga().enqueue(new Callback<MangaEden.MangaEdenList>() {
            @Override
            public void onResponse(Response<MangaEden.MangaEdenList> response, Retrofit retrofit) {
                getMangaInBackground(response.body());
            }

            @Override
            public void onFailure(Throwable t) {
                Log.d("BrowseMangaFragment", "Failed to fetch manga list,");
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
                showContent();

                Log.d("BrowseMangaFragment", "Finished sorting manga");
            }
        }.execute(list);
    }

    private void sortAdapterData() {
        // Sort by recently updated
        updatesCardAdapter.getFilter(SortOrder.LAST_UPDATED).filter("");
        // Sort by popularity
        popularCardAdapter.getFilter(SortOrder.POPULARITY).filter("");
        // Sort alphabetical
        alphabetCardAdapter.getFilter(SortOrder.ALPHABETICAL).filter("");
    }

    private void showContent() {
        placeholder.setVisibility(View.GONE);
        contentView.setVisibility(View.VISIBLE);
        searchRecyclerView.setVisibility(View.GONE);
    }

    private void showPlaceholder() {
        placeholder.setVisibility(View.VISIBLE);
        contentView.setVisibility(View.GONE);
        searchRecyclerView.setVisibility(View.GONE);
    }

    private void showSearch() {
        placeholder.setVisibility(View.GONE);
        contentView.setVisibility(View.GONE);
        searchRecyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuinflator) {
        menuinflator.inflate(R.menu.menu_browse_search, menu);

        // Configure the SearchView to filter the cards
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchCardAdapter.getFilter(SortOrder.POPULARITY).filter(query);
                return true; // The listener has handled the query
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchCardAdapter.getFilter(SortOrder.POPULARITY).filter(newText);
                return false; // The searchview should show suggestions
            }
        });

        // Show and hide the view for searches where appropriate
        MenuItemCompat.setOnActionExpandListener(searchItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                showSearch();
                return true; // Continue showing the search bar
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                showContent();
                return true; // Continue collapsing the search bar
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}