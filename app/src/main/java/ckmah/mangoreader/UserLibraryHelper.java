package ckmah.mangoreader;

import android.app.Activity;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;

import com.william.mangoreader.R;

import java.util.ArrayList;
import java.util.List;

import ckmah.mangoreader.activity.MangaItemActivity;
import ckmah.mangoreader.activity.MangoReaderActivity;
import ckmah.mangoreader.adapter.CardLayoutAdapter;
import ckmah.mangoreader.database.Manga;
import ckmah.mangoreader.model.MangaEdenMangaChapterItem;
import ckmah.mangoreader.model.MangaEdenMangaDetailItem;
import ckmah.mangoreader.parse.MangaEden;
import io.paperdb.Paper;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class UserLibraryHelper {

    public static final String USER_LIBRARY_DB = "user-library";
    private static String added = "\"%s\" added to your library.";
    private static String removed = "\"%s\" removed from your library.";

    public static List<Manga> findAllFavoritedManga() {
        List<Manga> response = new ArrayList<>();
        for (String key : Paper.book(USER_LIBRARY_DB).getAllKeys()) {
            Manga m = Paper.book(USER_LIBRARY_DB).read(key);
            if (m.favorite) {
                response.add(m);
            }
            //TODO: else check if flagged for deletion
        }
        return response;
    }

    public static void addToLibrary(final Manga m, final View button, final Activity activity, boolean showUndo, final CardLayoutAdapter adapter, final int position) {

        m.favorite = true;
        Paper.book(USER_LIBRARY_DB).write(m.id, m);
        button.setSelected(true);

        MangaEden.getMangaEdenService(activity)
                .getMangaDetails(m.id)
                .enqueue(new Callback<MangaEdenMangaDetailItem>() {
                    @Override
                    public void onResponse(Response<MangaEdenMangaDetailItem> response, Retrofit retrofit) {
                        MangaEdenMangaDetailItem r = response.body();
                        List<MangaEdenMangaChapterItem> c = r.getChapters();
                        m.author = r.getAuthor();
                        m.dateCreated = r.getDateCreated();
                        m.description = r.getDescription();
                        m.language = r.getLanguage();
                        m.numChapters = c.size();
                        m.chaptersList = MangaEden.convertChapterItemstoChapters(c);
                        Paper.book(USER_LIBRARY_DB).write(m.id, m);
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        Log.e("UserLibraryHelper", "Could not get manga details to store in db.");
                    }
                });

        Snackbar sb = Snackbar.make(findMyView(activity), String.format(added, m.title), Snackbar.LENGTH_LONG);
        if (showUndo) {
            sb.setAction("UNDO", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    removeFromLibrary(m, button, activity, false, adapter, position);
                }
            });
        }
        sb.show();

        if (adapter != null) { // basically called from browse or library
            addToListUpdate(m, adapter, position);
        }
    }

    public static void removeFromLibrary(final Manga m, final View button, final Activity activity, boolean showUndo, final CardLayoutAdapter adapter, final int position) {

        m.favorite = false;
        Paper.book(USER_LIBRARY_DB).write(m.id, m);
        button.setSelected(false);
        // show undo option only if not called from add undo
        Snackbar sb = Snackbar.make(findMyView(activity), String.format(removed, m.title), Snackbar.LENGTH_LONG);
        if (showUndo) {
            sb.setAction("UNDO", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addToLibrary(m, button, activity, false, adapter, position);
                }
            });
        }
        sb.show();
        if (adapter != null) { // basically called from browse or library
            removeFromListUpdate(adapter, position);
        }
    }

    private static View findMyView(Activity activity) {
        View mView;
        if (activity instanceof MangaItemActivity) {
            mView = activity.findViewById(R.id.manga_item_layout);
        } else if (activity instanceof MangoReaderActivity) {
            mView = activity.findViewById(R.id.drawer_layout);
        } else { // Bookmarked in browse sub-page
            mView = activity.findViewById(R.id.browse_activity_layout);
        }
        return mView;
    }

    public static void removeFromListUpdate( CardLayoutAdapter adapter, int position) {
        if (!adapter.isBrowsing) {
            adapter.filteredManga.remove(position);
            adapter.notifyItemRemoved(position);
            adapter.notifyItemRangeChanged(position, adapter.filteredManga.size());
        }
    }

    private static void addToListUpdate(Manga m, CardLayoutAdapter adapter, int position) {
        if (!adapter.isBrowsing) {
            adapter.filteredManga.add(position, m);
            adapter.notifyItemInserted(position);
            adapter.notifyItemRangeChanged(position, adapter.filteredManga.size());
        }
    }

}
