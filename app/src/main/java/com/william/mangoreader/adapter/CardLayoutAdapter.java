package com.william.mangoreader.adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.Toast;

import com.william.mangoreader.R;
import com.william.mangoreader.activity.MangaItemActivity;
import com.william.mangoreader.model.MangaEdenMangaListItem;
import com.william.mangoreader.parse.MangaEden;

import java.util.ArrayList;
import java.util.List;

/**
 * Layout adapter for adding cards
 */
public class CardLayoutAdapter extends RecyclerView.Adapter<RecyclerViewCardHolder> implements Filterable {

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
    public void onBindViewHolder(RecyclerViewCardHolder viewHolder, int position) {
        viewHolder.setTitle(filteredManga.get(position).getTitle());
        viewHolder.setSubtitle("Placeholder");
        MangaEden.setThumbnail(filteredManga.get(position).getImageUrl(), activity.getApplicationContext(), viewHolder.getThumbnail());
        viewHolder.setMangaEdenId(filteredManga.get(position).getId());
    }

    public void clearList() {
        filteredManga.clear();
        notifyDataSetChanged();
    }

    private void addToList(int pos) {
        Toast.makeText(activity, "\"" + filteredManga.get(pos).getTitle() + "\" added to your library.", Toast.LENGTH_SHORT).show();
    }

    private void removeFromList(int pos) {
        Toast.makeText(activity, "\"" + filteredManga.get(pos).getTitle() + "\" removed from your library.", Toast.LENGTH_SHORT).show();

        // needed to update UI without reading in entire database
        filteredManga.remove(pos);
        notifyItemRemoved(pos);
        notifyItemRangeChanged(pos, filteredManga.size());
    }

    @Override
    public RecyclerViewCardHolder onCreateViewHolder(final ViewGroup viewGroup, int position) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View itemView = inflater.inflate(R.layout.manga_card, viewGroup, false);
        final RecyclerViewCardHolder holder = new RecyclerViewCardHolder(itemView);

        final CardView cardView = (CardView) itemView.findViewById(R.id.card_view);

        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, MangaItemActivity.class);
                intent.putExtra("mangaId", holder.getMangaEdenId());
                activity.startActivity(intent);
            }
        });

        int cardPosition = holder.getAdapterPosition();

        cardView.findViewById(R.id.card_menu_more).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                PopupMenu popupMenu = new PopupMenu(viewGroup.getContext(), view);

                // RecyclerView does not rebind item, so position is obsolete
                final int pos = holder.getAdapterPosition();

                // create a different PopupMenu depending whether created in library/browse
                if (browseFlag) {
                    popupMenu.inflate(R.menu.menu_card_browse);
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.menu_add_lib:
                                    addToList(pos);
                                    return true;
                                default:
                                    return false;
                            }
                        }
                    });
                } else {
                    popupMenu.inflate(R.menu.menu_card_library);
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.menu_remove_lib:
                                    removeFromList(pos);
                                    return true;
                                default:
                                    return false;
                            }
                        }
                    });
                }
                popupMenu.show();
            }
        });
        return holder;
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
                    if (item.getTitle().toLowerCase().contains(filterPattern)) {
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
}
