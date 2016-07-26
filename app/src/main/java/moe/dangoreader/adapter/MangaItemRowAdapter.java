package moe.dangoreader.adapter;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.github.lzyzsd.circleprogress.DonutProgress;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.TimeZone;

import io.paperdb.Paper;
import moe.dangoreader.DownloadService;
import moe.dangoreader.R;
import moe.dangoreader.UserLibraryHelper;
import moe.dangoreader.activity.MangaViewerActivity;
import moe.dangoreader.database.Chapter;
import moe.dangoreader.database.Manga;

/**
 * Layout adapter for adding chaptersList
 */
public class MangaItemRowAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {// implements ItemTouchHelperAdapter{

    private static final String CHAPTER_PREFIX = "Chapter ";
    private Manga manga;
    private String mangaId;
    private List<Chapter> chaptersList;
    private Activity activity;

    public MangaItemRowAdapter(Activity activity, Manga manga) {
        this.activity = activity;
        this.manga = manga;
        this.mangaId = manga.id;
        chaptersList = manga.chaptersList;
//        updateChapterList(manga.chaptersList);
        Paper.init(activity);

        // Define filters for Receiver to look for.
        IntentFilter queueIntentFilter = new IntentFilter(DownloadService.Constants.QUEUED_ACTION);
        IntentFilter workingIntentFilter = new IntentFilter(DownloadService.Constants.WORKING_ACTION);
        IntentFilter doneIntentFilter = new IntentFilter(DownloadService.Constants.DONE_ACTION);

        // Registers BroadcastReceiver and its intent filters
        DownloadReceiver downloadReceiver = new DownloadReceiver();
        LocalBroadcastManager.getInstance(activity).registerReceiver(downloadReceiver, queueIntentFilter);
        LocalBroadcastManager.getInstance(activity).registerReceiver(downloadReceiver, workingIntentFilter);
        LocalBroadcastManager.getInstance(activity).registerReceiver(downloadReceiver, doneIntentFilter);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View chapterView = inflater.inflate(R.layout.chapter_row, parent, false);
        final ChapterViewHolder chapterHolder = new ChapterViewHolder(chapterView);

        chapterView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MangaViewerActivity.start(activity, mangaId, chapterHolder.getAdapterPosition());
            }
        });
        return chapterHolder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        final ChapterViewHolder chapterHolder = (ChapterViewHolder) holder;
        final Chapter chapterItem = chaptersList.get(position);

        // set chapter number
        chapterHolder.numberView.setText(String.format("%s%s", CHAPTER_PREFIX, chapterItem.number));

        // set chapter date
        Date chapterDate = new Date(chapterItem.date * 1000L);
        DateFormat sdf = SimpleDateFormat.getDateInstance();
        sdf.setTimeZone(TimeZone.getDefault());
        chapterHolder.dateView.setText(sdf.format(chapterDate));

        // set read/unread indicator
        if (chapterItem.read) {
            chapterHolder.numberView.setTextColor(ContextCompat.getColor(activity, R.color.colorPrimaryDark));
        } else {
            chapterHolder.numberView.setTextColor(ContextCompat.getColor(activity, R.color.black));
        }

        // TODO Sorts chaptersList in descending number. may consider adding setting/toggle
        if (chapterItem.getDlStatus() != 2) {
            chapterHolder.downloadButton.setVisibility(View.VISIBLE);
            chapterHolder.downloadProgress.setVisibility(View.INVISIBLE);
        }
        Log.d("DownloadStatus", String.format("%s, position %d", String.valueOf(chapterItem.getDlStatus()), position));
        switch (chapterItem.getDlStatus()) {
            case 1: // queued
                chapterHolder.downloadButton.setImageResource(R.drawable.queue_grey);
                chapterHolder.downloadProgress.setMax(0); // default max shows that dl is queued
                break;
            case 2: // downloading
                chapterHolder.downloadButton.setVisibility(View.INVISIBLE);
                chapterHolder.downloadProgress.setVisibility(View.VISIBLE);
                // update progress circle
                Log.d("Download", String.format("%d/%d", chapterItem.getDlProgress(), chapterItem.getNumPages()));

                chapterHolder.downloadProgress.setMax(100);
                int max = (chapterItem.getNumPages() != 0) ? chapterItem.getNumPages() : 0;
                int percentage = (int) (((double) chapterItem.getDlProgress()) / max * 100);
                chapterHolder.downloadProgress.setProgress(percentage);
                break;
            case 3: // downloaded
                chapterHolder.downloadButton.setImageResource(R.drawable.downloaded_amber);
                break;
            default: // not downloaded
                chapterHolder.downloadButton.setImageResource(R.drawable.download_grey);
                break;
        }

        // TODO update icon when finished downloading
        // download on click, let DownloadService handle checks and errors
        chapterHolder.downloadButton.setOnClickListener(new View.OnClickListener() {
            Intent dlIntent = new Intent(activity, DownloadService.class);

            @Override
            public void onClick(View v) {
                // download manga
                if (chapterItem.getDlStatus() == 0) {
                    // download status is in queue
                    dlIntent.putExtra("mangaId", mangaId);
                    dlIntent.putExtra("chapterId", chapterItem.id);
                    dlIntent.putExtra("chaptersListIndex", holder.getAdapterPosition());
                    activity.startService(dlIntent);
                    LocalBroadcastManager.getInstance(activity).sendBroadcast(new Intent(DownloadService.Constants.QUEUED_ACTION).putExtra("chapterId", chapterItem.id));

                    Snackbar.make(activity.findViewById(R.id.manga_item_layout), "Downloading \"" + manga.title + "\" chapter " + chapterItem.number + "...", Snackbar.LENGTH_LONG).show();
                } else {
                    // confirmation dialog
                    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                    builder.setMessage("Remove chapter " + chapterItem.number + "?");

                    // Delete chapter folder after confirming with
                    builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            File chapterDir;
                            try {
                                notifyItemChanged(holder.getAdapterPosition());
                                Log.d("File init", chapterItem.offlineLocation);
                                chapterDir = new File(chapterItem.offlineLocation);
                                Log.d("Delete", chapterDir.getPath());
                                if (chapterDir.isDirectory()) {
                                    String[] children = chapterDir.list();
                                    for (int i = 0; i < children.length; i++) {
                                        new File(chapterDir, children[i]).delete();
                                    }
                                    Snackbar.make(activity.findViewById(R.id.manga_item_layout), "\"" + manga.title + "\" chapter " + chapterItem.number + " deleted.", Snackbar.LENGTH_LONG).show();
                                } else {
                                    Snackbar.make(activity.findViewById(R.id.manga_item_layout), "Unable to find saved chapter folder.", Snackbar.LENGTH_LONG).show();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                Snackbar.make(activity.findViewById(R.id.manga_item_layout), "Unable to find saved chapter folder.", Snackbar.LENGTH_LONG).show();
                            }

                            chapterItem.offlineLocation = null;
                            chapterItem.setDlStatus(0);
                            Log.d("Download", "deleted");
                            Snackbar.make(activity.findViewById(R.id.manga_item_layout), "\"" + manga.title + "\" chapter " + chapterItem.number + " deleted.", Snackbar.LENGTH_LONG).show();
                            chaptersList.set(holder.getAdapterPosition(), chapterItem);
//                            updateChapterList(manga.chaptersList);
                            Paper.book(UserLibraryHelper.USER_LIBRARY_DB).write(manga.id, manga);
                            notifyItemChanged(holder.getAdapterPosition());
                        }
                    });
                    builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    });
                    builder.create().show();
                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return chaptersList.size();
    }

    // TODO Sorts chaptersList in descending number. may consider adding setting/toggle

    /**
     * Update chaptersList respective to RecyclerView list order.
     *
     * @param chaptersList
     */
    public void updateChapterList(List<Chapter> chaptersList) {
        this.chaptersList = chaptersList;
//        Collections.reverse(this.chaptersList);
    }

    // Broadcast receiver for receiving status updates from the IntentService
    public class DownloadReceiver extends BroadcastReceiver {
        // Prevents instantiation
        public DownloadReceiver() {
        }

        // Called when the BroadcastReceiver gets an Intent it's registered to receive
        @Override
        public void onReceive(Context context, Intent intent) {
            // find chapter
            int chapterIndex = 0;
            // prevents mixing download status across multiple adapter instances
            if (!Objects.equals(intent.getStringExtra("mangaId"), manga.id)) {
                return;
            }
            for (int index = 0; index < chaptersList.size(); index++) {
                if (chaptersList.get(index).id.compareTo(intent.getStringExtra("chapterId")) == 0) {
                    chapterIndex = index;
                    break;
                }
            }
            // handle download status
            Chapter chapter = chaptersList.get(chapterIndex);
            switch (intent.getAction()) {
                case DownloadService.Constants.QUEUED_ACTION:
                    Log.d("Broadcast", "Queued");
                    chapter.setDlStatus(1);
                    break;
                case DownloadService.Constants.WORKING_ACTION:
                    Log.d("Broadcast", "Downloading...");
                    chapter.setDlStatus(2);
                    chapter.setDlProgress(intent.getIntExtra("progress", 0));
                    chapter.setNumPages(intent.getIntExtra("progressMax", 1));
                    break;
                case DownloadService.Constants.DONE_ACTION:
                    Log.d("Broadcast", "Download finished.");
                    chapter.setDlStatus(3);
                    // TODO optimize manga chapter db lookup
                    chaptersList.set(chapterIndex, chapter);
//                    updateChapterList(manga.chaptersList);
                    Log.d("Download", "Offline location saved.");
                    Paper.book(UserLibraryHelper.USER_LIBRARY_DB).write(manga.id, manga);
                    break;
                default:
                    break;
            }
            chaptersList.set(chapterIndex, chapter);
//            updateChapterList(manga.chaptersList);
            Paper.book(UserLibraryHelper.USER_LIBRARY_DB).write(manga.id, manga);
            notifyItemChanged(chapterIndex);
        }
    }

    public class ChapterViewHolder extends RecyclerView.ViewHolder {

        public TextView numberView;
        public TextView dateView;
        public DonutProgress downloadProgress;
        public ImageButton downloadButton;

        public ChapterViewHolder(View chapterView) {
            super(chapterView);
            numberView = (TextView) chapterView.findViewById(R.id.chapter_number);
            dateView = (TextView) chapterView.findViewById(R.id.chapter_date);
            downloadProgress = (DonutProgress) chapterView.findViewById(R.id.chapter_download_progress);
            downloadButton = (ImageButton) chapterView.findViewById(R.id.chapter_download_button);
        }
    }
}
