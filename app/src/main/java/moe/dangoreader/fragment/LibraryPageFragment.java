package moe.dangoreader.fragment;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.ImageView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import moe.dangoreader.R;
import moe.dangoreader.UserLibraryHelper;
import moe.dangoreader.adapter.helper.SortOrder;
import moe.dangoreader.parse.PaletteTransformation;

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
        allManga = UserLibraryHelper.findAllFavoriteManga();
        loadLibraryPlaceholder();
        super.init(false);

        // Sort My Library by most recently updated first, by default
        getFilter().filter("");

        return rootView;
    }

    public void loadLibraryPlaceholder() {
        final ImageView emptyLibraryView = (ImageView) rootView.findViewById(R.id.empty_library_image);

        // Manipulates bitmap off main thread
        if (allManga.isEmpty()) {
            Picasso.with(getActivity())
                    .load(R.drawable.empty_library)
                    .fit().centerCrop()
                    .transform(PaletteTransformation.instance())
                    .into(emptyLibraryView, new Callback.EmptyCallback() {
                        @Override
                        public void onSuccess() {
                            Bitmap bitmap = ((BitmapDrawable) emptyLibraryView.getDrawable()).getBitmap();
                            emptyLibraryView.setImageBitmap(bitmap);
                            rootView.findViewById(R.id.library_page_placeholder).setVisibility(View.VISIBLE);
                        }
                    });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        allManga = UserLibraryHelper.findAllFavoriteManga();
        cardAdapter.setAllManga(allManga);
        getFilter().filter("");
    }

    @Override
    public Filter getFilter() {
        // Sort My Library by most recently updated first, by default
        return cardAdapter.getFilter(SortOrder.LAST_UPDATED);
    }
}
