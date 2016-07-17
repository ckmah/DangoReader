package moe.dangoreader;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.widget.ProgressBar;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import moe.dangoreader.model.MangaEdenImageItem;
import moe.dangoreader.parse.MangaEden;

/**
 * Handles downloading chapters.
 */
public class DownloadHelper {

    private static HashMap<String[], AsyncTask> downloadQueue = new HashMap<>();
    private static List<String[]> finishedQueue = new ArrayList<>();

    public static void queueChapter(String mangaId, String chapterId, final Activity activity, final ProgressBar progress) {
        // explicitly execute tasks serially
        downloadQueue.put(new String[]{mangaId, chapterId}, new DownloadTask(activity).executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, mangaId, chapterId));

    }

    // TODO implement removing chapter
    public static boolean deleteChapter() {
        return true;
    }

    private static class DownloadTask extends AsyncTask<String, Integer, String[]> {

        private static Activity activity;

        DownloadTask(Activity activity) {
            DownloadTask.activity = activity;
        }

        @Override
        protected String[] doInBackground(String... params) {
            // check if external storage available
            if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {

                String mangaId = params[0];
                String chapterId = params[1];
                String EXT_DIR_PATH = activity.getExternalFilesDir("Manga").getPath();
                final String CHAPTER_PATH = EXT_DIR_PATH + "/" + mangaId + "/" + chapterId;

                //get chapter directory
                File chapterDir = new File(CHAPTER_PATH);
                chapterDir.mkdirs();
                List<MangaEdenImageItem> images = new ArrayList<MangaEdenImageItem>();
                // retrieve image URLs
                try {
                    images = MangaEden.getMangaEdenService(activity).getMangaImages(chapterId).execute().body().images;
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // load images with picasso
                for (int i = 0; i < images.size(); i++) {
                    // server call for image
                    try {
                        Bitmap bitmap = Picasso.with(activity).load(MangaEden.MANGAEDEN_IMAGE_CDN + images.get(i).getUrl()).get();
                        String filename = CHAPTER_PATH + "/" + String.format("%1$04d", i) + ".png";
                        File file = new File(filename);
                        Log.d("Downloading", filename);
                        // write to file
                        try {
                            file.createNewFile();
                            FileOutputStream ostream = new FileOutputStream(file);
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, ostream);
                            ostream.close();
                            publishProgress(i, images.size());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                String msg = "External storage is not mounted. Unable to save offline.";
                Log.e("MangaItemRowAdapter", "External storage not mounted");
                Snackbar.make(activity.findViewById(R.id.details_view), msg, Snackbar.LENGTH_LONG).show();
            }
            return params;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            int progress = values[0];
            int max = values[1];
            Log.d("Downloading", (progress + 1) + "/" + max);
        }

        @Override
        protected void onPostExecute(String[] result) {
            super.onPostExecute(result);
            downloadQueue.remove(result);
            finishedQueue.add(result);
        }
    }
}
