package com.william.mangoreader.adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.william.mangoreader.R;
import com.william.mangoreader.activity.MangaViewerActivity;
import com.william.mangoreader.model.MangaEdenMangaChapterItem;
import com.william.mangoreader.model.MangaEdenMangaDetailItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Layout adapter for adding chapters
 */
public class MangaItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Activity activity;
    private List<Object> data;


    private static final String CHAPTER_PREFIX = "Chapter ";

    public MangaItemAdapter(Activity activity, MangaEdenMangaDetailItem manga) {
        this.activity = activity;
        data = new ArrayList<>();

        if (manga.getTitle() != null) {
            data.add(manga);
            data.addAll(manga.getChapters());
        }
    }

    public void loadMangaInfo(MangaEdenMangaDetailItem manga) {
        data.clear();
        data.add(manga);
        data.addAll(manga.getChapters());
        notifyDataSetChanged();
    }

    @Override
    /**
     * Returns 0 for details, 1 for chapter.
     */
    public int getItemViewType(int position) {
        return data.get(position) instanceof MangaEdenMangaChapterItem ? 1 : 0;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case 0: // DetailsViewHolder
                View detailsView = inflater.inflate(R.layout.details_view, parent, false);

                return new DetailsViewHolder(detailsView);
            default: // ChapterViewHolder
                View chapterView = inflater.inflate(R.layout.chapter_row, parent, false);
                final ChapterViewHolder chapterHolder = new ChapterViewHolder(chapterView);

                chapterView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(activity, MangaViewerActivity.class);
                        intent.putExtra("chapterId", chapterHolder.mangaEdenChapterId);
                        activity.startActivity(intent);
                    }
                });

                return chapterHolder;
        }

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case 0:
                DetailsViewHolder detailsHolder = (DetailsViewHolder) holder;
                MangaEdenMangaDetailItem detailsItem = (MangaEdenMangaDetailItem) data.get(position);
                detailsHolder.titleView.setText(detailsItem.getTitle());
                detailsHolder.subtitleView.setText(detailsItem.getAuthor());
                detailsHolder.descriptionView.setText(detailsItem.getDescription());
                break;
            case 1:
                ChapterViewHolder chapterHolder = (ChapterViewHolder) holder;
                MangaEdenMangaChapterItem chapterItem = (MangaEdenMangaChapterItem) data.get(position);
                chapterHolder.titleView.setText(chapterItem.getTitle());
                chapterHolder.numberView.setText(CHAPTER_PREFIX + chapterItem.getNumber());
                chapterHolder.mangaEdenChapterId = chapterItem.getId();
                break;
            default:
        }


    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class DetailsViewHolder extends RecyclerView.ViewHolder {

        public TextView titleView;
        public TextView subtitleView;
        public TextView descriptionView;

        public DetailsViewHolder(View detailsView) {
            super(detailsView);
            titleView = (TextView) detailsView.findViewById(R.id.manga_item_title);
            subtitleView = (TextView) detailsView.findViewById(R.id.manga_item_subtitle);
            descriptionView = (TextView) detailsView.findViewById(R.id.manga_item_description);
        }
    }

    public class ChapterViewHolder extends RecyclerView.ViewHolder {

        public TextView numberView;
        public TextView titleView;
        public String mangaEdenChapterId;

        public ChapterViewHolder(View chapterView) {
            super(chapterView);
            numberView = (TextView) chapterView.findViewById(R.id.chapter_number);
            titleView = (TextView) chapterView.findViewById(R.id.chapter_title);
        }
    }
}
