package ckmah.mangoreader.adapter;

import android.app.Activity;
import android.support.v4.app.Fragment;
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

    public Fragment fragment;
    List<Chapter> chapters = null;
    private Activity activity;
    private static final String CHAPTER_PREFIX = "Chapter ";
    // In-memory list of ids and titles so we can go onto next/prev chapters
    private ArrayList<String> chapterIds = new ArrayList<>();
    private ArrayList<String> chapterNumbers = new ArrayList<>();
    private String mangaTitle;


    public MangaItemRowAdapter(Activity activity, Fragment fragment, ArrayList<Chapter> chapters, String mangaTitle) {
        this.activity = activity;
        this.fragment = fragment;
        this.chapters = chapters;
        this.mangaTitle = mangaTitle;

        // Parse chapters to get chapterIds and chapterNumbers
        chapterIds.clear();
        chapterNumbers.clear();
        for (Chapter item : chapters) {
            chapterIds.add(item.id);
            chapterNumbers.add(item.number);
        }
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View chapterView = inflater.inflate(R.layout.chapter_row, parent, false);
        final ChapterViewHolder chapterHolder = new ChapterViewHolder(chapterView);

        chapterView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int chapterIndex = chapterIds.indexOf(chapterHolder.mangaEdenChapterId);
                MangaViewerActivity.start(activity, mangaTitle, chapterIds, chapterNumbers, chapterIndex);
            }
        });
        return chapterHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

            ChapterViewHolder chapterHolder = (ChapterViewHolder) holder;
            Chapter chapterItem = chapters.get(position);
            chapterHolder.titleView.setText(chapterItem.title);
            chapterHolder.numberView.setText(CHAPTER_PREFIX + chapterItem.number);
            chapterHolder.mangaEdenChapterId = chapterItem.id;
    }

    @Override
    public int getItemCount() {
        return chapters.size();
    }

    public class ChapterViewHolder extends RecyclerView.ViewHolder {

        public TextView numberView;
        public TextView titleView;
        public String mangaEdenChapterId;

        public ChapterViewHolder(View chapterView) {
            super(chapterView);
            numberView = (TextView) chapterView.findViewById(R.id.chapter_number);
            titleView = (TextView) chapterView.findViewById(R.id.toolbar_chapter_title);
        }
    }
}
