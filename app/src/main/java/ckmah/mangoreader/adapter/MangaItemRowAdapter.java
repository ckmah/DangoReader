package ckmah.mangoreader.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.william.mangoreader.R;

import java.util.ArrayList;
import java.util.List;

import ckmah.mangoreader.activity.MangaViewerActivity;
import ckmah.mangoreader.database.Chapter;

/**
 * Layout adapter for adding chapters
 */
public class MangaItemRowAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {// implements ItemTouchHelperAdapter{

    private static final String CHAPTER_PREFIX = "Chapter ";
    List<Chapter> chapters = null;
    private Activity activity;
    // In-memory list of ids and titles so we can go onto next/prev chapters
    private String mangaId;


    public MangaItemRowAdapter(Activity activity, ArrayList<Chapter> chapters, String mangaId) {
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
        int index = chapters.size() - position - 1;
        Chapter chapterItem = chapters.get(index);

        chapterHolder.titleView.setText(chapterItem.title);
        chapterHolder.numberView.setText(CHAPTER_PREFIX + chapterItem.number);
        //TODO janky and doesnt update properly
        if (chapterItem.read) {
            chapterHolder.numberView.setTextColor(activity.getResources().getColor(R.color.colorPrimary));
        }
        else {
            chapterHolder.numberView.setTextColor(activity.getResources().getColor(R.color.black));
        }
        chapterHolder.chapterIndex = index;
    }

    @Override
    public int getItemCount() {
        return chapters.size();
    }

    public class ChapterViewHolder extends RecyclerView.ViewHolder {

        public TextView numberView;
        public TextView titleView;
        public int chapterIndex;

        public ChapterViewHolder(View chapterView) {
            super(chapterView);
            numberView = (TextView) chapterView.findViewById(R.id.chapter_number);
            titleView = (TextView) chapterView.findViewById(R.id.toolbar_chapter_title);
        }
    }
}
