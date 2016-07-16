package moe.dangoreader;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import moe.dangoreader.model.MangaEdenImageItem;
import moe.dangoreader.parse.MangaEden;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Handles downloading chapters.
 */
public class DownloadHelper {
    public static void downloadChapter(String mangaId, String chapterId, final Activity activity, final ProgressBar progress) {
        //check if external storage available
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {

            progress.setVisibility(View.VISIBLE);

            String EXT_DIR_PATH = activity.getExternalFilesDir("Manga").getPath();
            final String CHAPTER_PATH = EXT_DIR_PATH + "/" + mangaId + "/" + chapterId;
            //get chapter directory
            File chapterDir = new File(CHAPTER_PATH);
            chapterDir.mkdirs();
            MangaEden.getMangaEdenService(activity).getMangaImages(chapterId).enqueue(new Callback<MangaEden.MangaEdenChapter>() {
                @Override
                public void onResponse(final Response<MangaEden.MangaEdenChapter> response, Retrofit retrofit) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            List<MangaEdenImageItem> images = response.body().images;

                            progress.setProgress(0);
                            progress.setMax(images.size());

                            for (int i = 0; i < images.size(); i++) {
                                // server call for image
                                try {
                                    Bitmap bitmap = Picasso.with(activity).load(MangaEden.MANGAEDEN_IMAGE_CDN + images.get(i).getUrl()).get();
                                    File file = new File(CHAPTER_PATH + "/" + String.format("%1$04d", i) + ".png");
                                    // write to file
                                    try {
                                        file.createNewFile();
                                        FileOutputStream ostream = new FileOutputStream(file);
                                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, ostream);
                                        ostream.close();
                                        progress.incrementProgressBy(1);
                                        Log.d("Downloading", "page " + (i + 1) + " of " + images.size() + " downloaded.");
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            progress.setVisibility(View.INVISIBLE);
                        }
                    }).start();
                }

                @Override
                public void onFailure(Throwable t) {
                    String msg = "Chapter failed to load. Check your internet connection and try again later.";
                    Log.e("MangaItemRowAdapter", msg);
                    // TODO make failure more verbose/user interactive
                    Snackbar.make(activity.findViewById(R.id.details_view), msg, Snackbar.LENGTH_LONG).show();
                    progress.setVisibility(View.INVISIBLE);
                }
            });
        } else {
            String msg = "External storage is not mounted. Unable to save offline.";
            Log.e("MangaItemRowAdapter", "External storage not mounted");
            Snackbar.make(activity.findViewById(R.id.details_view), msg, Snackbar.LENGTH_LONG).show();
            progress.setVisibility(View.INVISIBLE);
        }
    }


    // TODO implement removing chapter
    public static boolean deleteChapter() {
        return true;
    }
}
