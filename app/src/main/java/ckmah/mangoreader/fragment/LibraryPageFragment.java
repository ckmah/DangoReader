package ckmah.mangoreader.fragment;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Filter;

import com.squareup.picasso.Picasso;
import com.william.mangoreader.R;

import ckmah.mangoreader.UserLibraryHelper;
import ckmah.mangoreader.adapter.helper.SortOrder;
import ckmah.mangoreader.parse.PaletteTransformation;

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
        loadLibraryPlaceholder();
        super.init(false);

        // Sort My Library by most recently updated first, by default
        getFilter().filter("");

        return rootView;
    }

    public void loadLibraryPlaceholder() {
        ImageView emptyLibraryView = (ImageView) rootView.findViewById(R.id.empty_library_image);

        // Manipulates bitmap off main thread
        if (allManga.isEmpty()) {
            rootView.findViewById(R.id.empty_library_image).setVisibility(View.VISIBLE);
            Picasso.with(getActivity())
                    .load(R.drawable.empty_library)
                    .fit().centerCrop()
                    .transform(PaletteTransformation.instance())
                    .into(emptyLibraryView);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        allManga = UserLibraryHelper.findAllFavoritedManga();
        cardAdapter.setAllManga(allManga);
        getFilter().filter("");
    }

    @Override
    public Filter getFilter() {
        // Sort My Library by most recently updated first, by default
        return cardAdapter.getFilter(SortOrder.LAST_UPDATED);
    }
}
