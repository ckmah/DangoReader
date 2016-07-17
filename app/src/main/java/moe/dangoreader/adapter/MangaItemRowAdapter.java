package moe.dangoreader.adapter;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import io.paperdb.Paper;
import moe.dangoreader.DownloadService;
import moe.dangoreader.R;
import moe.dangoreader.UserLibraryHelper;
import moe.dangoreader.activity.MangaViewerActivity;
import moe.dangoreader.database.Chapter;
import moe.dangoreader.database.Manga;

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
        Paper.init(activity);
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
        final ChapterViewHolder chapterHolder = (ChapterViewHolder) holder;
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

        final ProgressBar progress = chapterHolder.downloadProgress;
        progress.setVisibility(View.INVISIBLE);

        final Manga manga = Paper.book(UserLibraryHelper.USER_LIBRARY_DB).read(mangaId);
        // TODO subtraction only because list is reversed
        final Chapter chapter = manga.chaptersList.get(chapterHolder.chapterIndex);
        if (chapter.offlineLocation == null) {
            chapterHolder.downloadButton.setImageResource(R.drawable.download_grey);
        } else {
            chapterHolder.downloadButton.setImageResource(R.drawable.download_amber);
        }

        // TODO update icon when finished downloading
        // download on click, let DownloadService handle checks and errors
        chapterHolder.downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // download manga
                if (chapter.offlineLocation == null) {
                    Intent dlIntent = new Intent(activity, DownloadService.class);
                    dlIntent.putExtra("mangaId", mangaId);
                    dlIntent.putExtra("chapterId", chapterItem.id);
                    // Starts the IntentService
                    activity.startService(dlIntent);
                    Snackbar.make(activity.findViewById(R.id.manga_item_layout), "Downloading \"" + manga.title + "\" chapter " + chapterHolder.chapterIndex + "...", Snackbar.LENGTH_LONG).show();

                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                    builder.setMessage("Remove saved chapter?");
                    // Delete chapter folder after confirming with
                    builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            File chapterDir = new File(chapter.offlineLocation);
                            Log.d("Delete", chapterDir.getPath());
                            if (chapterDir.isDirectory()) {
                                String[] children = chapterDir.list();
                                for (int i = 0; i < children.length; i++) {
                                    new File(chapterDir, children[i]).delete();
                                }
                                Snackbar.make(activity.findViewById(R.id.manga_item_layout), "\"" + manga.title + "\" chapter " + chapterHolder.chapterIndex + " deleted.", Snackbar.LENGTH_LONG).show();
                            } else {
                                Snackbar.make(activity.findViewById(R.id.manga_item_layout), "Unable to find saved chapter folder.", Snackbar.LENGTH_LONG).show();
                            }
                            chapter.offlineLocation = null;
                            chapterHolder.downloadButton.setImageResource(R.drawable.download_grey);
                        }
                    });
                    builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // do nothing
                        }
                    });

                    builder.create().show();
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
        public ProgressBar downloadProgress;
        public ImageButton downloadButton;

        public int chapterIndex;

        public ChapterViewHolder(View chapterView) {
            super(chapterView);
            numberView = (TextView) chapterView.findViewById(R.id.chapter_number);
            dateView = (TextView) chapterView.findViewById(R.id.chapter_date);
            downloadProgress = (ProgressBar) chapterView.findViewById(R.id.chapter_download_progress);
            downloadButton = (ImageButton) chapterView.findViewById(R.id.chapter_download_button);
        }
    }
}
