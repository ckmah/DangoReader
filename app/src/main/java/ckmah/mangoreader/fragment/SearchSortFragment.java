package ckmah.mangoreader.fragment;

import android.app.Activity;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.william.mangoreader.R;

import java.util.ArrayList;
import java.util.List;

import ckmah.mangoreader.adapter.CardLayoutAdapter;
import ckmah.mangoreader.adapter.helper.SimpleItemTouchHelperCallback;
import ckmah.mangoreader.database.Manga;

public class SearchSortFragment extends Fragment {

    protected CardLayoutAdapter cardAdapter;
    @Nullable protected SwipeRefreshLayout swipeRefreshLayout;
    protected RecyclerView mRecyclerView;
    protected GridLayoutManager gridLayoutManager;

    // In-memory list of all manga i.e. no filter applied
    protected List<Manga> allManga = new ArrayList<>();

    /**
     * Initialize and hook up the various Android components
     * Call in subclasses at the end of onCreateView
     */
    public void init() {
        gridLayoutManager = new GridLayoutManager(getActivity(), 2);
        mRecyclerView.setLayoutManager(gridLayoutManager);

        cardAdapter = new CardLayoutAdapter(getActivity(), this);
        cardAdapter.setAllManga(allManga);
        cardAdapter.showAllManga();
        mRecyclerView.setAdapter(cardAdapter);

        ItemTouchHelper.Callback callback =
                new SimpleItemTouchHelperCallback(cardAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(mRecyclerView);

        setHasOptionsMenu(true);
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
                if (swipeRefreshLayout != null) {
                    // Disable swipe to refresh if user is searching
                    swipeRefreshLayout.setEnabled(query.isEmpty());
                }
                cardAdapter.getFilter().filter(query);
                return true; // The listener has handled the query
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (swipeRefreshLayout != null) {
                    // Disable swipe to refresh if user is searching
                    swipeRefreshLayout.setEnabled(newText.isEmpty());
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
}
