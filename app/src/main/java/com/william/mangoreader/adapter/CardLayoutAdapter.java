package com.william.mangoreader.adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import com.william.mangoreader.UserLibraryHelper;
import com.william.mangoreader.activity.MangaItemActivity;
import com.william.mangoreader.adapter.helper.ItemTouchHelperAdapter;
import com.william.mangoreader.model.MangaEdenMangaListItem;
import com.william.mangoreader.parse.MangaEden;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Layout adapter for adding cards
 */
public class CardLayoutAdapter extends RecyclerView.Adapter<CardLayoutAdapter.CardViewHolder> implements ItemTouchHelperAdapter, Filterable, Serializable {

    public Fragment fragment;
    private List<MangaEdenMangaListItem> allManga;
    public List<MangaEdenMangaListItem> filteredManga;
    public Activity activity;

    public CardLayoutAdapter(Activity activity, Fragment fragment) {
        filteredManga = new ArrayList<>();
        this.activity = activity;
        this.fragment = fragment;
        // Pass context or other static stuff that will be needed.
    }

    public void setAllManga(List<MangaEdenMangaListItem> allManga) {
        this.allManga = allManga;
    }

    @Override
    public void onBindViewHolder(final CardViewHolder viewHolder, final int position) {
        viewHolder.title.setText(filteredManga.get(position).title);
        viewHolder.subtitle.setText("Placeholder");
        MangaEden.setThumbnail(filteredManga.get(position).imageUrl, activity.getApplicationContext(), viewHolder.thumbnail);
        viewHolder.manga = filteredManga.get(position);
        viewHolder.bookmarkToggle.setSelected(UserLibraryHelper.findMangaInLibrary(viewHolder.manga).size() > 0);
        viewHolder.bookmarkToggle.setImageResource(R.drawable.bookmark_toggle);

        final CardLayoutAdapter adapter = this;
        // add/remove methods take care of toggling bookmark icon
        viewHolder.bookmarkToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View button) {
                if (button.isSelected()) {
                    UserLibraryHelper.removeFromLibrary(viewHolder.manga, button, activity, true, adapter, position);
                } else {
                    UserLibraryHelper.addToLibrary(viewHolder.manga, button, activity, adapter, position);
                }
            }
        });
    }

    public void clearList() {
        filteredManga.clear();
        notifyDataSetChanged();
    }

    @Override
    public CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.manga_card, parent, false);
        final CardViewHolder holder = new CardViewHolder(itemView);

        final CardView cardView = (CardView) itemView.findViewById(R.id.card_view);

        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, MangaItemActivity.class);
                intent.putExtra("mangaListItem", holder.manga);
                activity.startActivity(intent);
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

    // mimics filter
    public Filter getFilter(int sortOptionIndex, boolean isReverseOrder, List<Integer> selectedGenres) {
        return new CardLayoutFilter(sortOptionIndex, isReverseOrder, selectedGenres);
    }

    /**
     * Performs the filtering of the cards by the query term
     */
    private class CardLayoutFilter extends Filter {
        private final int sortOptionIndex;
        private final boolean isReverseOrder;
        private final List<Integer> selectedGenresIndices;

        public CardLayoutFilter() {
            sortOptionIndex = -1;
            isReverseOrder = false;
            selectedGenresIndices = null;
        }

        public CardLayoutFilter(int sortOptionIndex, boolean isReverseOrder, List<Integer> selectedGenresIndices) {
            this.sortOptionIndex = sortOptionIndex;
            this.isReverseOrder = isReverseOrder;
            this.selectedGenresIndices = selectedGenresIndices;
        }

        @Override
        protected FilterResults performFiltering(CharSequence query) {
            filteredManga.clear();
            FilterResults results = new FilterResults();

            // dialogFragment sorting
            if (sortOptionIndex != -1) {
                dialogFilter();
            } else if (query.length() == 0) {
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

        private void dialogFilter() {
            String[] allGenres = activity.getResources().getStringArray(R.array.genre_list);
            Collection<String> selectedGenres = new ArrayList<>();

            // no genres selected, skip filtering
            if (selectedGenresIndices.size() > 0) {
                // retrieve genre names
                for (Integer index : selectedGenresIndices) {
                    selectedGenres.add(allGenres[index].toLowerCase());
                    Log.d("SORTING", "selected genre: " + allGenres[index].toLowerCase());
                }

                // filter genres first
                for (MangaEdenMangaListItem manga : allManga) {
                    Collection<String> genres = new ArrayList<>();

                    for (String genre : manga.genres)
                        genres.add(genre.toLowerCase());

                    genres.retainAll(selectedGenres); // removes genres not found in selected
                    if (genres.size() > 0) {
                        filteredManga.add(manga);
                    }
                }
            }

            // sort by specified order
            switch (sortOptionIndex) {
                case 0: // sort by recently updated
                    Collections.sort(allManga, new Comparator<MangaEdenMangaListItem>() {
                        @Override
                        public int compare(MangaEdenMangaListItem lhs, MangaEdenMangaListItem rhs) {
                            return ((Long) rhs.lastChapterDate).compareTo(lhs.lastChapterDate);
                        }
                    });
                    break;
                case 1: // sort by popularity
                    Collections.sort(allManga, new Comparator<MangaEdenMangaListItem>() {
                        @Override
                        public int compare(MangaEdenMangaListItem lhs, MangaEdenMangaListItem rhs) {
                            return ((Integer) rhs.hits).compareTo(lhs.hits);
                        }
                    });
                    break;
                case 2: // sort alphabetically
                    Collections.sort(allManga, new Comparator<MangaEdenMangaListItem>() {
                        @Override // reverse comparison b/c default is Z to A
                        public int compare(MangaEdenMangaListItem lhs, MangaEdenMangaListItem rhs) {
                            return lhs.title.compareTo(rhs.title);
                        }
                    });

                    break;
                default:
                    Log.d("SORTING", "Did not dialog sort by genres properly.");
            }

            // reverse list
            if (isReverseOrder) {
                Collections.reverse(allManga);
            }

            filteredManga.addAll(allManga);
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
        public MangaEdenMangaListItem manga;
        public ImageButton bookmarkToggle;

        public CardViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.card_title);
            subtitle = (TextView) itemView.findViewById(R.id.card_subtitle);
            thumbnail = (ImageView) itemView.findViewById(R.id.card_thumbnail);
            bookmarkToggle = (ImageButton) itemView.findViewById(R.id.card_bookmark_toggle);
        }
    }
}
