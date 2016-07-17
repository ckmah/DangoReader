package moe.dangoreader;

import android.app.IntentService;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;
import moe.dangoreader.database.Manga;
import moe.dangoreader.model.MangaEdenImageItem;
import moe.dangoreader.parse.MangaEden;
import retrofit.Call;

public class DownloadService extends IntentService {
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public DownloadService() {
        super("DownloadService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            String msg = "External storage is not mounted. Unable to save offline.";
            Log.e("MangaItemRowAdapter", "External storage not mounted");
            Toast.makeText(getApplication(), msg, Toast.LENGTH_LONG).show();
            return;
        }

        String mangaId = intent.getStringExtra("mangaId");
        String chapterId = intent.getStringExtra("chapterId");
        String EXT_DIR_PATH = getApplication().getExternalFilesDir("Manga").getPath();
        final String CHAPTER_PATH = EXT_DIR_PATH + "/" + mangaId + "/" + chapterId;
        Log.d("Download", CHAPTER_PATH);
        //get chapter directory
        File chapterDir = new File(CHAPTER_PATH);
        chapterDir.mkdirs();
        List<MangaEdenImageItem> images = new ArrayList<>();

        // retrieve image URLs
        try {
            Call<MangaEden.MangaEdenChapter> response = MangaEden.getMangaEdenService(getApplicationContext()).getMangaImages(chapterId);
            images = response.execute().body().images;
        } catch (IOException e) {
            e.printStackTrace();
        }

        // load images with picasso
        for (int i = 0; i < images.size(); i++) {
            // server call for image
            try {
                String filename = CHAPTER_PATH + "/" + String.format("%1$04d", i) + ".png";
                File file = new File(filename);

                // TODO: 7/16/2016 handle file exists (rewrite? skip?)
                Bitmap bitmap = Picasso.with(getApplicationContext()).load(MangaEden.MANGAEDEN_IMAGE_CDN + images.get(i).getUrl()).get();
                // write to file
                try {
                    file.createNewFile();
                    FileOutputStream ostream = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, ostream);
                    ostream.close();
                    Log.d("Download", (i + 1) + "/" + images.size());
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }

        Manga manga = Paper.book(UserLibraryHelper.USER_LIBRARY_DB).read(mangaId);
        // TODO optimize manga chapter db lookup
        for (int index = 0; index < manga.chaptersList.size(); index++) {
            if (manga.chaptersList.get(index).id.compareTo(chapterId) == 0) {
                manga.chaptersList.get(index).offlineLocation = CHAPTER_PATH;
                break;
            }
        }
        Paper.book(UserLibraryHelper.USER_LIBRARY_DB).write(manga.id, manga);
    }
}
