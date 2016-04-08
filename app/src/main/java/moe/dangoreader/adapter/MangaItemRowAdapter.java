package moe.dangoreader.adapter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import moe.dangoreader.R;
import moe.dangoreader.activity.MangaViewerActivity;
import moe.dangoreader.database.Chapter;
import moe.dangoreader.model.MangaEdenImageItem;
import moe.dangoreader.parse.MangaEden;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Layout adapter for adding chapters
 */
public class MangaItemRowAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {// implements ItemTouchHelperAdapter{

    private static final String CHAPTER_PREFIX = "Chapter ";
    List<Chapter> chapters = null;
    private Activity activity;
    // In-memory list of ids and titles so we can go onto next/prev chapters
    private String mangaId;


    public MangaItemRowAdapter(Activity activity, List<Chapter> chapters, String mangaId) {
        this.activity = activity;
        this.chapters = chapters;
        this.mangaId = mangaId;
        Collections.reverse(chapters); //TODO Sorts chapters in descending number. may consider adding setting/toggle
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View chapterView = inflater.inflate(R.layout.chapter_row, parent, false);
        final ChapterViewHolder chapterHolder = new ChapterViewHolder(chapterView);

        chapterView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MangaViewerActivity.start(activity, mangaId, chapterHolder.chapterIndex);
            }
        });
        return chapterHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ChapterViewHolder chapterHolder = (ChapterViewHolder) holder;
        final Chapter chapterItem = chapters.get(position);

        // set chapter number
        chapterHolder.numberView.setText(String.format("%s%s", CHAPTER_PREFIX, chapterItem.number));

        // set chapter date
        Date chapterDate = new Date(chapterItem.date * 1000L);
        DateFormat sdf = SimpleDateFormat.getDateInstance();
        sdf.setTimeZone(TimeZone.getTimeZone("GMT-8"));
        chapterHolder.dateView.setText(sdf.format(chapterDate));

        // visual indicator for read/unread
        if (chapterItem.read) {
            chapterHolder.numberView.setTextColor(ContextCompat.getColor(activity, R.color.colorPrimaryDark));
        } else {
            chapterHolder.numberView.setTextColor(ContextCompat.getColor(activity, R.color.black));
        }
        // chapterHolder.chapterIndex = position;
        // TODO Sorts chapters in descending number. may consider adding setting/toggle
        chapterHolder.chapterIndex = chapters.size() - position - 1;

        //TODO move out of here
        chapterHolder.downloadButton.setOnClickListener(new ImageButton.OnClickListener() {
            @Override
            public void onClick(View v) {
                //check if external storage available
                if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {

                    String EXT_DIR_PATH = activity.getExternalFilesDir("Manga").getPath();
                    final String CHAPTER_PATH = EXT_DIR_PATH + "/" + mangaId + "/" + chapterItem.id;
                    //get chapter directory
                    File chapterDir = new File(CHAPTER_PATH);
                    chapterDir.mkdirs();
                    MangaEden.getMangaEdenService(activity).getMangaImages(chapterItem.id).enqueue(new Callback<MangaEden.MangaEdenChapter>() {
                        @Override
                        public void onResponse(final Response<MangaEden.MangaEdenChapter> response, Retrofit retrofit) {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    List<MangaEdenImageItem> images = response.body().images;
                                    for (int i = 0; i < images.size(); i++) {
                                        try {
                                            Bitmap bitmap = Picasso.with(activity).load(MangaEden.MANGAEDEN_IMAGE_CDN + images.get(i).getUrl()).get();
                                            File file = new File(CHAPTER_PATH + "/" + String.format("%1$04d", i) + ".png");
                                            try {
                                                file.createNewFile();
                                                FileOutputStream ostream = new FileOutputStream(file);
                                                bitmap.compress(Bitmap.CompressFormat.PNG, 100, ostream);
                                                ostream.close();
                                                Log.d("Downloading", "page " + i + " downloaded.");
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }).start();
                        }

                        @Override
                        public void onFailure(Throwable t) {
                            Log.e("MangaItemRowAdapter", "Could not get image urls");
                            // TODO make failure more verbose/user interactive
                        }
                    });
                } else {
                    Log.e("MangaItemRowAdapter", "External storage not mounted");
                }


            }
        });
    }

    @Override
    public int getItemCount() {
        return chapters.size();
    }

    public class ChapterViewHolder extends RecyclerView.ViewHolder {

        public TextView numberView;
        public TextView dateView;
        public ImageButton downloadButton;

        public int chapterIndex;

        public ChapterViewHolder(View chapterView) {
            super(chapterView);
            numberView = (TextView) chapterView.findViewById(R.id.chapter_number);
            dateView = (TextView) chapterView.findViewById(R.id.chapter_date);
            downloadButton = (ImageButton) chapterView.findViewById(R.id.chapter_download_button);
        }
    }
}
