package ckmah.mangoreader;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.william.mangoreader.R;

import org.joda.time.DateTime;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ckmah.mangoreader.activity.MangoReaderActivity;
import ckmah.mangoreader.database.Chapter;
import ckmah.mangoreader.database.Manga;
import ckmah.mangoreader.model.MangaEdenMangaListItem;
import ckmah.mangoreader.parse.MangaEden;
import io.paperdb.Paper;

public class UpdateService extends IntentService {
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     * Used to name the worker thread, important only for debugging.
     */
    public UpdateService() {
        super("UpdateService");
    }

    @Override
    protected void onHandleIntent(Intent workIntent) {
        try {
            Log.d("UpdateService", "starting");
            // Synchronously download list of all manga
            MangaEden.MangaEdenList list = MangaEden
                    .getMangaEdenService(this, true)
                    .listAllManga()
                    .execute()
                    .body();

            // Start up user library
            // TODO update Paper entries for all manga
            Paper.init(this);

            List<Manga> updated = new ArrayList<>();

            for (MangaEdenMangaListItem item : list.manga) {
                // Check whether manga was released in the last 24 hours. TODO longer window?
                DateTime lastChapterDate = new DateTime(item.lastChapterDate * 1000L); // Convert ms to sec
                DateTime yesterday = new DateTime().minusDays(1);
                if (lastChapterDate.isAfter(yesterday)) {

                    // Check whether manga is in library
                    Manga m = Paper.book(UserLibraryHelper.USER_LIBRARY_DB).read(item.id);
                    if (m != null && m.favorite) {
                        m = UserLibraryHelper.updateManga(this, item.id);

                        // Check whether latest chapter was read
                        Chapter latest = m.chaptersList.get(m.chaptersList.size()-1);
                        if (latest.mostRecentPage == -1) {
                            updated.add(m);
                        }
                    }
                }
            }
            // TODO always run UpdateService, just skip notification
            notify(updated);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Builds a notification from a list of updated manga
     */
    private void notify(List<Manga> updated) {
        // Create the notification title
        String title;
        switch (updated.size()) {
            case 0:
                // Nothing updated, exit without notification
                return;
            case 1:
                // Only 1 item updated
                title = "New chapter available";
                break;
            default:
                // Multiple updated
                title = "New chapters available";
                break;
        }

        // Create a comma-separated string of manga titles
        String message = "";
        for (Manga manga : updated) {
            message += ", " + manga.title;
        }
        message = message.substring(2);

        // Sets up the notification
        android.support.v4.app.NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(title)
                        .setContentText(message)
                        .setAutoCancel(true); // Dismiss notification when clicked

        // Sets up the larger notification aka expanded layout
        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        inboxStyle.setBigContentTitle(title);
        for (Manga manga : updated) {
            String number = manga.chaptersList.get(manga.chaptersList.size()-1).number;
            String line = String.format("Ch. %s — %s", number, manga.title);
            inboxStyle.addLine(line);
        }
        mBuilder.setStyle(inboxStyle);

        // Launch MangoReaderActivity when notification is clicked
        Intent resultIntent = new Intent(this, MangoReaderActivity.class);
        // Because clicking the notification opens a new ("special") activity, there's
        // no need to create an artificial back stack.
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);

        // Sets an ID for the notification
        int mNotificationId = 001;
        // Gets an instance of the NotificationManager service
        NotificationManager mNotifyMgr =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // Builds the notification and issues it.
        mNotifyMgr.notify(mNotificationId, mBuilder.build());
    }
}
