package moe.dangoreader.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import moe.dangoreader.R;
import moe.dangoreader.activity.MangaViewerActivity;
import moe.dangoreader.parse.MangaEden;
import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

public class MangaImageFragment extends Fragment {
    private static final String IMAGE_DATA_EXTRA = "item_index";
    private int itemIndex;

    public MangaImageFragment() {
        // required empty constructor
    }

    public static MangaImageFragment newInstance(int index) {
        Bundle args = new Bundle();
        args.putInt(IMAGE_DATA_EXTRA, index);
        MangaImageFragment f = new MangaImageFragment();
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        itemIndex = getArguments() != null ? getArguments().getInt(IMAGE_DATA_EXTRA) : 0;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.manga_image_fragment, container, false);
        PhotoView imageView = (PhotoView) root.findViewById(R.id.manga_image_view);
        imageView.setOnViewTapListener(new PhotoViewAttacher.OnViewTapListener() {
            @Override
            public void onViewTap(View view, float x, float y) {
                ((MangaViewerActivity) getActivity()).handleTap(view, x, y);
            }
        });
        final MangaViewerActivity activity = (MangaViewerActivity) getActivity();
        String imageUrl = activity.getImages().get(itemIndex).getUrl();

        MangaEden.setMangaImage(imageUrl, activity, imageView);
        return root;
    }
}

