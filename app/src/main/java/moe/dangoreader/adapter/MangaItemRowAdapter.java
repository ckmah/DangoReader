package moe.dangoreader.adapter;

import android.app.Activity;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import moe.dangoreader.R;
import moe.dangoreader.activity.MangaViewerActivity;
import moe.dangoreader.database.Chapter;

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
        Chapter chapterItem = chapters.get(position);

        // set chapter number
        chapterHolder.numberView.setText(String.format("%s%s", CHAPTER_PREFIX, chapterItem.number));

        // set chapter title
//        if (chapterItem.title.compareTo(chapterItem.number) != 0) {
//        chapterHolder.titleView.setText(String.format("%s%s", " — ", chapterItem.title));
//        }

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
        chapterHolder.chapterIndex = position;
    }

    @Override
    public int getItemCount() {
        return chapters.size();
    }

    public class ChapterViewHolder extends RecyclerView.ViewHolder {

        public TextView numberView;
        public TextView titleView;
        public TextView dateView;

        public int chapterIndex;

        public ChapterViewHolder(View chapterView) {
            super(chapterView);
            numberView = (TextView) chapterView.findViewById(R.id.chapter_number);
            titleView = (TextView) chapterView.findViewById(R.id.chapter_title);
            dateView = (TextView) chapterView.findViewById(R.id.chapter_date);
        }
    }
}
