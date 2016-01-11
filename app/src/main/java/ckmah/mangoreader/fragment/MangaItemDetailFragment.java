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

import ckmah.mangoreader.UserLibraryHelper;
import ckmah.mangoreader.activity.MangaItemActivity;
import ckmah.mangoreader.database.Manga;
import ckmah.mangoreader.parse.MangaEden;
import io.paperdb.Paper;

public class MangaItemDetailFragment extends Fragment {
    private static final String DESCRIPTION_FRAGMENT_KEY = "mangaListItem";
    private static final String SWATCH_KEY = "swatch_key";

    private Palette.Swatch secondaryColor;

    public MangaItemDetailFragment() {
        //required empty constructor
    }

    public static MangaItemDetailFragment newInstance(String mangaId) {
        MangaItemDetailFragment fragment = new MangaItemDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putString(DESCRIPTION_FRAGMENT_KEY, mangaId);
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

        String mangaId = getArguments().getString(DESCRIPTION_FRAGMENT_KEY);
        Manga manga = Paper.book(UserLibraryHelper.USER_LIBRARY_DB).read(mangaId);

        rootView = inflater.inflate(R.layout.fragment_manga_details, container, false);

        //set image for details page
        image = (ImageView) rootView.findViewById(R.id.manga_item_image_view);
        MangaEden.setMangaArt(manga.imageSrc, image, (MangaItemActivity) this.getActivity());

        //populate text sections of details page
        author = (TextView) rootView.findViewById(R.id.manga_item_author);
        author.setText(manga.author);

        hits = (TextView) rootView.findViewById(R.id.manga_item_hits);
        hits.setText(String.valueOf(manga.hits));

        language = (TextView) rootView.findViewById(R.id.manga_item_language);
        language.setText((manga.language == 0) ? "English" : "Italian");

        lastUpdated = (TextView) rootView.findViewById(R.id.manga_item_last_chapter_date);
        Date lastChapterDate = new Date(manga.lastChapterDate * 1000L);
        DateFormat sdf = SimpleDateFormat.getDateInstance();
        sdf.setTimeZone(TimeZone.getTimeZone("GMT-8"));
        lastUpdated.setText(sdf.format(lastChapterDate));

        status = (TextView) rootView.findViewById(R.id.manga_item_status);
        String stat= "";
        switch (manga.status) {
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
        Date creationDate = new Date(manga.dateCreated * 1000L);
        dateCreated.setText(sdf.format(creationDate));

        categories = (TextView) rootView.findViewById(R.id.manga_item_categories);
        String categoryListString = manga.genres.toString();
        categories.setText(categoryListString.substring(1, categoryListString.length() - 1));

        description = (TextView) rootView.findViewById(R.id.manga_item_description);
        description.setText(manga.description);

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
