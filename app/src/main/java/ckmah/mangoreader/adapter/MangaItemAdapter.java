package ckmah.mangoreader.adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.william.mangoreader.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import ckmah.mangoreader.activity.MangaItemActivity;
import ckmah.mangoreader.activity.MangaViewerActivity;
import ckmah.mangoreader.model.MangaEdenMangaChapterItem;
import ckmah.mangoreader.model.MangaEdenMangaDetailItem;
import ckmah.mangoreader.parse.MangaEden;

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
        data.add(manga);
    }

    // In-memory list of chapterIds so we can go onto next/prev chapters
    private ArrayList<String> chapterIds = new ArrayList<>();

    public void loadMangaInfo(MangaEdenMangaDetailItem manga) {
        List<MangaEdenMangaChapterItem> chaptersCopy = new ArrayList<>(manga.getChapters());
        Collections.reverse(chaptersCopy);

        chapterIds.clear();
        for (MangaEdenMangaChapterItem item : chaptersCopy) {
            chapterIds.add(item.getId());
        }

        data.clear();
        data.add(manga);
        data.addAll(chaptersCopy);
        notifyDataSetChanged();
    }

    @Override
    /**
     * Returns 0 for details, 1 for chapter.
     */
    public int getItemViewType(int position) {
        // data not retrieved yet
        if (((MangaEdenMangaDetailItem) data.get(0)).getTitle() == null)
            return -1;
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
                        intent.putExtra("chapterIds", chapterIds);
                        intent.putExtra("chapterIndex", chapterIds.indexOf(chapterHolder.mangaEdenChapterId));
//                        intent.putExtra("chapterId", chapterHolder.mangaEdenChapterId);
                        intent.putExtra("chapterTitle", chapterHolder.titleView.getText().toString());
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
                MangaEden.setMangaArt(detailsItem.getImageUrl(), detailsHolder.imageView, (MangaItemActivity) activity);
//                detailsHolder.titleView.setText(detailsItem.getTitle());
//                detailsHolder.authorView.setText(detailsItem.getAuthor());

                String categories = "";
                for (String c : detailsItem.getCategories())
                    categories += c + " / ";

                if (!categories.isEmpty())
                    detailsHolder.categoryView.setText(categories.substring(0, categories.length() - 2));

                detailsHolder.hitsView.setText("" + detailsItem.getHits());
                detailsHolder.languageView.setText((detailsItem.getLanguage() == 0) ? "English" : "Italian");

                Date lastChapterDate = new Date(detailsItem.getLastChapterDate() * 1000L);
                DateFormat sdf = SimpleDateFormat.getDateInstance();
                sdf.setTimeZone(TimeZone.getTimeZone("GMT-8"));
                detailsHolder.lastChapterDateView.setText(sdf.format(lastChapterDate));

                String status = "";
                switch (detailsItem.getStatus()) {
                    case 0:
                        status = "Suspended";
                        break;
                    case 1:
                        status = "Ongoing";
                        break;
                    case 2:
                        status = "Completed";
                    default:
                        break;
                }
                detailsHolder.statusView.setText(status);

                Date dateCreated = new Date(detailsItem.getDateCreated() * 1000L);
                detailsHolder.createdView.setText(sdf.format(dateCreated));
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
                // initialize without data
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class DetailsViewHolder extends RecyclerView.ViewHolder {

        public ImageView imageView;
        public TextView titleView;
        public TextView authorView;
        public TextView categoryView;
        public TextView hitsView;
        public TextView languageView;
        public TextView lastChapterDateView;
        public TextView statusView;
        public TextView createdView;
        public TextView descriptionView;

        public DetailsViewHolder(View detailsView) {
            super(detailsView);
            imageView = (ImageView) detailsView.findViewById(R.id.manga_item_image_view);
//            titleView = (TextView) detailsView.findViewById(R.id.manga_item_title);
//            authorView = (TextView) detailsView.findViewById(R.id.manga_item_author);
            categoryView = (TextView) detailsView.findViewById(R.id.manga_item_categories);
            hitsView = (TextView) detailsView.findViewById(R.id.manga_item_hits);
            languageView = (TextView) detailsView.findViewById(R.id.manga_item_language);
            lastChapterDateView = (TextView) detailsView.findViewById(R.id.manga_item_last_chapter_date);
            statusView = (TextView) detailsView.findViewById(R.id.manga_item_status);
            createdView = (TextView) detailsView.findViewById(R.id.manga_item_created);
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
