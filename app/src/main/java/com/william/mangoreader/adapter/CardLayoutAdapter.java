package com.william.mangoreader.adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.william.mangoreader.R;
import com.william.mangoreader.activity.MangaItemActivity;
import com.william.mangoreader.adapter.helper.ItemTouchHelperAdapter;
import com.william.mangoreader.model.MangaEdenMangaListItem;
import com.william.mangoreader.parse.MangaEden;

import java.util.ArrayList;
import java.util.List;

/**
 * Layout adapter for adding cards
 */
public class CardLayoutAdapter extends RecyclerView.Adapter<CardLayoutAdapter.CardViewHolder> implements ItemTouchHelperAdapter, Filterable {

    private List<MangaEdenMangaListItem> allManga;
    private List<MangaEdenMangaListItem> filteredManga;
    private Activity activity;
    private boolean browseFlag; //right now, this specifies whether we're in a browsemangafragment or mylibraryfragment

    public CardLayoutAdapter(Activity activity, boolean browseFlag) {
        filteredManga = new ArrayList<>();
        this.activity = activity;
        this.browseFlag = browseFlag;
        // Pass context or other static stuff that will be needed.
    }

    public void setAllManga(List<MangaEdenMangaListItem> allManga) {
        this.allManga = allManga;
    }

    @Override
    public void onBindViewHolder(CardViewHolder viewHolder, int position) {
        viewHolder.title.setText(filteredManga.get(position).title);
        viewHolder.subtitle.setText("Placeholder");
        MangaEden.setThumbnail(filteredManga.get(position).imageUrl, activity.getApplicationContext(), viewHolder.thumbnail);
        viewHolder.mangaEdenId = filteredManga.get(position).id;
    }


    public void clearList() {
        filteredManga.clear();
        notifyDataSetChanged();
    }

    private void addToList(int pos) {
        Toast.makeText(activity, "\"" + filteredManga.get(pos).title + "\" added to your library.", Toast.LENGTH_SHORT).show();
    }

    private void removeFromList(int pos) {
        Toast.makeText(activity, "\"" + filteredManga.get(pos).title + "\" removed from your library.", Toast.LENGTH_SHORT).show();

        // needed to update UI without reading in entire database
//        filteredManga.remove(pos);
//        notifyItemRemoved(pos);
//        notifyItemRangeChanged(pos, filteredManga.size());
    }

    @Override
    public CardViewHolder onCreateViewHolder(final ViewGroup viewGroup, final int position) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View itemView = inflater.inflate(R.layout.manga_card, viewGroup, false);
        final CardViewHolder holder = new CardViewHolder(itemView);

        final CardView cardView = (CardView) itemView.findViewById(R.id.card_view);

        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, MangaItemActivity.class);
                intent.putExtra("mangaId", holder.mangaEdenId);
                activity.startActivity(intent);
            }
        });

        ImageButton bookmarkToggle = (ImageButton) cardView.findViewById(R.id.card_bookmark_toggle);

        bookmarkToggle.setImageResource(R.drawable.bookmark_toggle);

        bookmarkToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View button) {
                button.setSelected(!button.isSelected());

                if (button.isSelected()) {
                    addToList(position);
                } else {
                    removeFromList(position);
                }
            }
        });

        return holder;
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        //TODO implement for dragging in library
        return false;
    }

    @Override
    public void onItemDismiss(int position) {
        //TODO add to user library
        Toast.makeText(activity, "\"" + filteredManga.get(position).title + "\" added to your library.", Toast.LENGTH_SHORT).show();
        notifyItemChanged(position);
    }

    @Override
    public int getItemCount() {
        return filteredManga.size();
    }

    @Override
    public Filter getFilter() {
        return new CardLayoutFilter();
    }


    /**
     * Performs the filtering of the cards by the query term
     */
    private class CardLayoutFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence query) {
            filteredManga.clear();
            FilterResults results = new FilterResults();

            if (query.length() == 0) {
                // If search term empty, show all manga
                filteredManga.addAll(allManga);
            } else {
                // Otherwise, do a case-blind search
                // TODO replace with better fuzzy match algorithm
                String filterPattern = query.toString().toLowerCase().trim();

                for (MangaEdenMangaListItem item : allManga) {
                    if (item.title.toLowerCase().contains(filterPattern)) {
                        filteredManga.add(item);
                    }
                }
            }
            results.values = filteredManga;
            results.count = filteredManga.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            notifyDataSetChanged();
        }
    }

    public static class CardViewHolder extends RecyclerView.ViewHolder {

        public TextView title;
        public TextView subtitle;
        public ImageView thumbnail;
        public String mangaEdenId; //TODO what do in future?

        public CardViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.card_title);
            subtitle = (TextView) itemView.findViewById(R.id.card_subtitle);
            thumbnail = (ImageView) itemView.findViewById(R.id.card_thumbnail);

        }

    }
}
