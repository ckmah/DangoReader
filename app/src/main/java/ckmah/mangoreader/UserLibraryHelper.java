package ckmah.mangoreader;

import android.app.Activity;
import android.content.Context;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.william.mangoreader.R;

import java.util.List;

import ckmah.mangoreader.adapter.CardLayoutAdapter;
import ckmah.mangoreader.daogen.DaoMaster;
import ckmah.mangoreader.daogen.DaoSession;
import ckmah.mangoreader.daogen.UserLibraryManga;
import ckmah.mangoreader.daogen.UserLibraryMangaDao;
import ckmah.mangoreader.fragment.LibraryPageFragment;
import ckmah.mangoreader.model.MangaEdenMangaListItem;
import de.greenrobot.dao.query.QueryBuilder;

public class UserLibraryHelper {
    private SQLiteDatabase userLibraryDb;
    private DaoMaster daoMaster;
    private DaoSession daoSession;
    private static UserLibraryMangaDao userLibraryMangaDao;

    /**
     * Singleton pattern to access DAO
     */
    public static UserLibraryMangaDao getDao(Context context) {
        if (userLibraryMangaDao == null) {
            Log.d("USERLIBRARYHELPER", "Creating DAO");
            // load user library
            DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(context, "user-library-db", null);
            SQLiteDatabase userLibraryDb = helper.getWritableDatabase();
            // helper.onUpgrade(userLibraryDb, userLibraryDb.getVersion(), 1000); // DEBUG PURPOSES ONLY
            DaoMaster daoMaster = new DaoMaster(userLibraryDb);
            DaoSession daoSession = daoMaster.newSession();
            userLibraryMangaDao = daoSession.getUserLibraryMangaDao();
        }
        return userLibraryMangaDao;
    }

    private static String added;
    private static String removed;

    public static boolean isInLibrary(Context context, MangaEdenMangaListItem item) {
        // If any manga ids are matched, then manga must be in library.
        QueryBuilder qb = getDao(context).queryBuilder();
        qb.where(UserLibraryMangaDao.Properties.MangaEdenId.eq(item.id));
        return qb.list().size() > 0;
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
            getDao(activity).insert(mangaItem);
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
        // don't do anything if not found in library
        if(!isInLibrary(activity, m)) {
            Log.e("MangoReader", "No manga found in user library.");
            return;
        }

        added = "\"" + m.title + "\" added to your library under \"Plan to Read\"";
        removed = "\"" + m.title + "\" removed from your library.";

        QueryBuilder qb = getDao(activity).queryBuilder();
        qb.where(UserLibraryMangaDao.Properties.MangaEdenId.eq(m.id));
        List<UserLibraryManga> matches = qb.list();

        final UserLibraryManga mangaItem = matches.get(0);
        getDao(activity).delete(mangaItem);
        removed = "\"" + m.title + "\" removed from your library.";

        // show undo option only if not called from add undo
        if (showUndo) {
            button.setSelected(false);
            Snackbar
                    .make(activity.findViewById(R.id.parent_layout), removed, Snackbar.LENGTH_LONG)
                    .setAction("UNDO", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            getDao(activity).insert(mangaItem);
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
