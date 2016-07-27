package moe.dangoreader;

import android.app.IntentService;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Environment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import moe.dangoreader.model.MangaEdenImageItem;
import moe.dangoreader.parse.MangaEden;
import retrofit.Call;

/**
 * Handles all offline manga download requests. Runs as a background service.
 */
public class DownloadService extends IntentService {

    Intent localIntent = new Intent();

    /**
     * Creates an IntentService.  Invoked by subclass's constructor.
     */
    public DownloadService() {
        super("DownloadService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // unable to write to disk
        if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            String msg = "External storage is not mounted. Unable to save offline.";
            Log.e("MangaItemRowAdapter", "External storage not mounted");
            Toast.makeText(getApplication(), msg, Toast.LENGTH_LONG).show();
            return;
        }

        String mangaId = intent.getStringExtra("mangaId");
        String chapterId = intent.getStringExtra("chapterId");

        // broadcast put in queue
//        localIntent.setAction(Constants.QUEUED_ACTION);
        localIntent.putExtra("mangaId", mangaId);
        localIntent.putExtra("chapterId", chapterId);
//        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(localIntent);

        // get chapter directory
        String EXT_DIR_PATH = getApplication().getExternalFilesDir("Manga").getPath();
        final String CHAPTER_PATH = String.format("%s/%s/%s", EXT_DIR_PATH, mangaId, chapterId);
        Log.d("Download", CHAPTER_PATH);

        new File(CHAPTER_PATH).mkdirs();
        List<MangaEdenImageItem> images = new ArrayList<>();

        // retrieve image URLs
        try {
            Call<MangaEden.MangaEdenChapter> response = MangaEden.getMangaEdenService(getApplicationContext()).getMangaImages(chapterId);
            images = response.execute().body().images;
            localIntent.setAction(Constants.WORKING_ACTION);
            localIntent.putExtra("progressMax", images.size());
            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(localIntent);

        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), "Failed to download manga. Please check your internet connection.", Toast.LENGTH_SHORT).show();
        }

        // load and write images to disk asynchronously
        // TODO: 7/16/2016 handle file exists (rewrite? skip?)
        for (int i = 0; i < images.size(); i++) {
            try {
                Bitmap bitmap = Picasso.with(getApplicationContext()).load(MangaEden.MANGAEDEN_IMAGE_CDN + images.get(i).getUrl()).get();
                String filename = String.format("%s/%s.png", CHAPTER_PATH, String.format("%1$04d", i));
                File file = new File(filename);
                try {
                    file.createNewFile();
                    FileOutputStream ostream = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, ostream);
                    ostream.close();
                    localIntent.putExtra("progress", i + 1);
                    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(localIntent);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // broadcast finished download
        localIntent.setAction(Constants.DONE_ACTION);
        localIntent.putExtra("mangaId", mangaId);
        localIntent.putExtra("chapterId", chapterId);
        localIntent.putExtra("chapterPath", CHAPTER_PATH);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(localIntent);
    }

    public final class Constants {
        // Defines a custom Intent action
        public static final String QUEUED_ACTION = "moe.dangoreader.download.QUEUED";
        public static final String WORKING_ACTION = "moe.dangoreader.download.WORKING";
        public static final String DONE_ACTION = "moe.dangoreader.download.DONE";
    }
}
