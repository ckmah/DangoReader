package com.william.mangoreader;

import android.app.Activity;
import android.database.sqlite.SQLiteConstraintException;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.william.mangoreader.activity.MangoReaderActivity;
import com.william.mangoreader.adapter.CardLayoutAdapter;
import com.william.mangoreader.daogen.UserLibraryManga;
import com.william.mangoreader.daogen.UserLibraryMangaDao;
import com.william.mangoreader.fragment.LibraryPageFragment;
import com.william.mangoreader.model.MangaEdenMangaListItem;

import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;

public class UserLibraryHelper {

    private static String added;
    private static String removed;


    public static List findMangaInLibrary(final MangaEdenMangaListItem m) {
        QueryBuilder qb = MangoReaderActivity.userLibraryMangaDao.queryBuilder();
        qb.where(UserLibraryMangaDao.Properties.Title.eq(m.title), UserLibraryMangaDao.Properties.ImageURL.eq(m.imageUrl));
        return qb.list();
    }

    /**
     * Creates dialog for user to select library category to add to.
     *
     * @param m
     */
    public static boolean addToLibrary(final MangaEdenMangaListItem m, final View button, final Activity activity, final CardLayoutAdapter adapter, final int position) {
        String genres = TextUtils.join("\t", m.genres);
        added = "\"" + m.title + "\" added to your library under \"Plan to Read\"";
        removed = "\"" + m.title + "\" removed from your library.";
        final UserLibraryManga mangaItem = new UserLibraryManga(
                m.id,
                activity.getResources().getStringArray(R.array.library_categories)[0],
                m.title,
                m.imageUrl,
                genres,
                m.status,
                m.lastChapterDate,
                m.hits);

        try { // insert and show snackbar with undo, return true if successful, false otherwise
            MangoReaderActivity.userLibraryMangaDao.insert(mangaItem);
            Snackbar
                    .make(activity.findViewById(R.id.parent_layout), added, Snackbar.LENGTH_LONG)
                    .setAction("UNDO", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            removeFromLibrary(m, button, activity, false, adapter, position);
                            button.setSelected(false);
                            if (adapter != null) { // basically called from browse or library
                                removeFromListUpdate(adapter.fragment, adapter, position);
                            }
                        }
                    })
                    .show();

            if (adapter != null) { // basically called from browse or library
                addToListUpdate(m, adapter.fragment, adapter, position);
            }
            button.setSelected(true);
            return true;
        } catch (SQLiteConstraintException e) {
            String duplicate = "\"" + m.title + "\" is already in your library.";
            Snackbar
                    .make(activity.findViewById(R.id.parent_layout), duplicate, Snackbar.LENGTH_SHORT)
                    .show();
            Log.d("LIBRARY", "Entry already exists");
            return false;
        }
    }

    /**
     * @param m
     * @param button
     * @param activity
     * @param showUndo
     */
    public static void removeFromLibrary(final MangaEdenMangaListItem m, final View button, final Activity activity, boolean showUndo, final CardLayoutAdapter adapter, final int position) {
        final List l = findMangaInLibrary(m);
        added = "\"" + m.title + "\" added to your library under \"Plan to Read\"";
        removed = "\"" + m.title + "\" removed from your library.";

        // don't do anything if not found in library
        if (l.size() == 0) {
            Log.e("MangoReader", "No manga found in user library.");
            return;
        }

        UserLibraryManga mangaItem = (UserLibraryManga) l.get(0);
        MangoReaderActivity.userLibraryMangaDao.delete(mangaItem);
        removed = "\"" + m.title + "\" removed from your library.";

        // show undo option only if not called from add undo
        if (showUndo) {
            button.setSelected(false);
            Snackbar
                    .make(activity.findViewById(R.id.parent_layout), removed, Snackbar.LENGTH_LONG)
                    .setAction("UNDO", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            MangoReaderActivity.userLibraryMangaDao.insert((UserLibraryManga) l.get(0));
                            Snackbar.make(activity.findViewById(R.id.parent_layout), removed, Snackbar.LENGTH_LONG);
                            button.setSelected(true);
                            if (adapter != null) { // basically called from browse or library
                                addToListUpdate(m, adapter.fragment, adapter, position);
                            }
                        }
                    })
                    .show();
        } else {
            button.setSelected(false);
            Snackbar
                    .make(activity.findViewById(R.id.parent_layout), removed, Snackbar.LENGTH_LONG)
                    .show();
        }
        if (adapter != null) { // basically called from browse or library
            removeFromListUpdate(adapter.fragment, adapter, position);
        }
    }

    public static void removeFromListUpdate(Fragment fragment, CardLayoutAdapter adapter, int position) {
        if (fragment instanceof LibraryPageFragment) {
            adapter.filteredManga.remove(position);
            adapter.notifyItemRemoved(position);
            adapter.notifyItemRangeChanged(position, adapter.filteredManga.size());
        }
    }

    public static void addToListUpdate(MangaEdenMangaListItem m, Fragment fragment, CardLayoutAdapter adapter, int position) {
        if (fragment instanceof LibraryPageFragment) {
            adapter.filteredManga.add(position, m);
            adapter.notifyItemInserted(position);
            adapter.notifyItemRangeChanged(position, adapter.filteredManga.size());
        }
    }
}
