package ckmah.mangoreader.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.graphics.Palette;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.william.mangoreader.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import ckmah.mangoreader.activity.MangaItemActivity;
import ckmah.mangoreader.model.MangaEdenMangaDetailItem;
import ckmah.mangoreader.parse.MangaEden;

public class MangaItemDetailFragment extends Fragment {
    private static final String DESCRIPTION_FRAGMENT_KEY = "mangaListItem";
    private static final String SWATCH_KEY = "swatch_key";
    private static MangaEdenMangaDetailItem mangaDetailItem;

    private Palette.Swatch secondaryColor;

    public MangaItemDetailFragment() {
        //required empty constructor
    }

    public static MangaItemDetailFragment newInstance(MangaEdenMangaDetailItem mangaDetailItem) {
        MangaItemDetailFragment fragment = new MangaItemDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(DESCRIPTION_FRAGMENT_KEY, mangaDetailItem);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView;
        ImageView image;
        TextView author;
        TextView hits;
        TextView language;
        TextView lastUpdated;
        TextView status;
        TextView dateCreated;
        TextView categories;
        TextView description;

        mangaDetailItem = (MangaEdenMangaDetailItem) getArguments().getSerializable(DESCRIPTION_FRAGMENT_KEY);
        rootView = inflater.inflate(R.layout.fragment_manga_details, container, false);

        //set image for details page
        image = (ImageView) rootView.findViewById(R.id.manga_item_image_view);
        MangaEden.setMangaArt(mangaDetailItem.getImageUrl(), image, (MangaItemActivity) this.getActivity());

        //populate text sections of details page
        author = (TextView) rootView.findViewById(R.id.manga_item_author);
        author.setText("" + mangaDetailItem.getAuthor());

        hits = (TextView) rootView.findViewById(R.id.manga_item_hits);
        hits.setText("" + mangaDetailItem.getHits());

        language = (TextView) rootView.findViewById(R.id.manga_item_language);
        language.setText((mangaDetailItem.getLanguage() == 0) ? "English" : "Italian");

        lastUpdated = (TextView) rootView.findViewById(R.id.manga_item_last_chapter_date);
        Date lastChapterDate = new Date(mangaDetailItem.getLastChapterDate() * 1000L);
        DateFormat sdf = SimpleDateFormat.getDateInstance();
        sdf.setTimeZone(TimeZone.getTimeZone("GMT-8"));
        lastUpdated.setText(sdf.format(lastChapterDate));

        status = (TextView) rootView.findViewById(R.id.manga_item_status);
        String stat= "";
        switch (mangaDetailItem.getStatus()) {
            case 0:
                stat = "Suspended";
                break;
            case 1:
                stat = "Ongoing";
                break;
            case 2:
                stat = "Completed";
            default:
                break;
        }
        status.setText(stat);

        dateCreated = (TextView) rootView.findViewById(R.id.manga_item_created);
        Date creationDate = new Date(mangaDetailItem.getDateCreated() * 1000L);
        dateCreated.setText(sdf.format(creationDate));

        categories = (TextView) rootView.findViewById(R.id.manga_item_categories);
        String categoryListString = mangaDetailItem.getCategories().toString();
        categories.setText(categoryListString.substring(1, categoryListString.length() - 1));

        description = (TextView) rootView.findViewById(R.id.manga_item_description);
        description.setText(mangaDetailItem.getDescription());

        return rootView;
    }

//    public void setLayoutColors() {
//        secondaryColor = ((MangaItemActivity) getActivity()).getSecondaryColor();
//
//        rootView.setBackgroundColor(secondaryColor.getRgb());
//
//        int textColor = contrastTextColor(secondaryColor.getRgb());
//        titleView.setTextColor(textColor);
//        subtitleView.setTextColor(textColor);
//        descriptionView.setTextColor(textColor);
//    }

//    public int contrastTextColor(int color) {
//        // Contrast formula derived from http://stackoverflow.com/a/1855903/1222351
//        double luminance = 0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color);
//        return luminance > 127 ? Color.BLACK : Color.WHITE;
//    }
}
