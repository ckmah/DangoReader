package moe.dangoreader.adapter;

import android.app.Activity;
import android.content.Intent;
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import moe.dangoreader.R;
import moe.dangoreader.UserLibraryHelper;
import moe.dangoreader.activity.MangaItemActivity;
import moe.dangoreader.adapter.helper.ItemTouchHelperAdapter;
import moe.dangoreader.adapter.helper.SortOrder;
import moe.dangoreader.database.Chapter;
import moe.dangoreader.database.Manga;
import moe.dangoreader.parse.MangaEden;

/**
 * Layout adapter for adding cards
 */
public class CardLayoutAdapter extends RecyclerView.Adapter<CardLayoutAdapter.CardViewHolder> implements ItemTouchHelperAdapter, Filterable {

    public final boolean isBrowsing;
    private final boolean useMiniCards;
    public List<Manga> filteredManga;
    public Activity activity;
    private List<Manga> allManga;

    public CardLayoutAdapter(Activity activity, boolean isBrowsing, boolean useMiniCards) {
        filteredManga = new ArrayList<>();
        this.activity = activity;
        this.isBrowsing = isBrowsing;
        this.useMiniCards = useMiniCards;
    }

    public void setAllManga(List<Manga> allManga) {
        this.allManga = allManga;
    }

    @Override
    public void onBindViewHolder(final CardViewHolder viewHolder, final int position) {
        Manga manga = filteredManga.get(position);
        viewHolder.manga = manga;
        viewHolder.title.setText(manga.title);
        MangaEden.setThumbnail(manga.imageSrc, activity.getApplicationContext(), viewHolder.thumbnail);

        if (!isBrowsing) {
            // Only show update indicators in My Library
            initUpdateIndicator(manga, viewHolder);
        }

        viewHolder.bookmarkToggle.setSelected(viewHolder.manga.favorite);
        viewHolder.bookmarkToggle.setImageResource(R.drawable.favorite_toggle);

        final CardLayoutAdapter adapter = this;
        // add/remove methods take care of toggling bookmark icon
        viewHolder.bookmarkToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View button) {
                if (button.isSelected()) {
                    UserLibraryHelper.removeFromLibrary(viewHolder.manga, button, activity, true, adapter, position);
                } else {
                    UserLibraryHelper.addToFavorites(viewHolder.manga, button, activity, true, adapter, position);
                }
            }
        });
    }

    private void initUpdateIndicator(Manga manga, CardViewHolder viewHolder) {
        int unreadCount = 0;
        // count unread chaptersList
        for (Chapter c : manga.chaptersList) {
            unreadCount += c.read ? 0 : 1;
        }

        // Display number of unread chaptersList
        if (unreadCount > 0) {
            viewHolder.indicatorView.setVisibility(View.VISIBLE);
            if (unreadCount >= 99) { // Gets cut off if too long... precision not needed anyway
                unreadCount = 99;
                viewHolder.indicatorText.setText(String.format("%d+", unreadCount));
            } else {
                viewHolder.indicatorText.setText(String.format("%d", unreadCount));
            }
        } else {
            viewHolder.indicatorView.setVisibility(View.GONE);
        }
    }

    @Override
    public CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView;
        if (useMiniCards) {
            itemView = inflater.inflate(R.layout.manga_card_mini, parent, false);
        } else {
            itemView = inflater.inflate(R.layout.manga_card, parent, false);

        }
        final CardViewHolder holder = new CardViewHolder(itemView);

        final CardView cardView = (CardView) itemView.findViewById(R.id.card_view);

        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, MangaItemActivity.class);
                intent.putExtra("mangaId", holder.manga.id);
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
        return getFilter(SortOrder.NONE);
    }

    // mimics filter
    public Filter getFilter(SortOrder sortOrder) {
        return getFilter(sortOrder, false, Collections.<Integer>emptyList());
    }

    /**
     * @param sortOrder      0 = popularity, 1 = recently updated, 2 = alphabetically
     * @param isReverseOrder true = reversed, false = as is
     * @param selectedGenres indices of genres to filter for (intersection)
     * @return
     */
    public Filter getFilter(SortOrder sortOrder, boolean isReverseOrder, List<Integer> selectedGenres) {
        return new CardLayoutFilter(sortOrder, isReverseOrder, selectedGenres);
    }

    public static class CardViewHolder extends RecyclerView.ViewHolder {

        public TextView title;
        public TextView subtitle;
        public ImageView thumbnail;
        public Manga manga;
        public ImageButton bookmarkToggle;
        public View indicatorView;
        public TextView indicatorText;

        public CardViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.card_title);
            subtitle = (TextView) itemView.findViewById(R.id.card_subtitle);
            thumbnail = (ImageView) itemView.findViewById(R.id.card_thumbnail);
            bookmarkToggle = (ImageButton) itemView.findViewById(R.id.card_bookmark_toggle);
            indicatorView = itemView.findViewById(R.id.updated_indicator);
            indicatorText = (TextView) itemView.findViewById(R.id.indicator_number);
        }
    }

    /**
     * Performs the filtering of the cards by the query term
     */
    private class CardLayoutFilter extends Filter {
        private final SortOrder sortOrder;
        private final boolean isReverseOrder;
        private final List<Integer> selectedGenresIndices;
        private final List<Manga> matches = new ArrayList<>();

        public CardLayoutFilter(SortOrder sortOrder, boolean isReverseOrder, List<Integer> selectedGenresIndices) {
            this.sortOrder = sortOrder;
            this.isReverseOrder = isReverseOrder;
            this.selectedGenresIndices = selectedGenresIndices;
        }

        @Override
        protected FilterResults performFiltering(CharSequence query) {
            // Done in background thread.
            // Find all matches for these genres and query, in a particular sort order
            matchGenres();
            matchQuery(query);
            sortMatches();

            // Return the matches to the parent CardLayoutAdapter
            FilterResults results = new FilterResults();
            results.values = matches;
            results.count = matches.size();
            return results;
        }

        /**
         * Only keep manga matching specified genres (if any)
         */
        private void matchGenres() {
            String[] allGenres = activity.getResources().getStringArray(R.array.genre_list);
            Collection<String> selectedGenres = new ArrayList<>();

            if (selectedGenresIndices.size() > 0) {
                // retrieve genre names to filter by
                for (Integer index : selectedGenresIndices) {
                    selectedGenres.add(allGenres[index].toLowerCase());
                    Log.d("SORTING", "selected genre: " + allGenres[index].toLowerCase());
                }

                // filter genres first
                for (Manga manga : allManga) {
                    Collection<String> genres = new ArrayList<>();

                    for (String genre : manga.genres)
                        genres.add(genre.toLowerCase());

                    genres.retainAll(selectedGenres); // removes genres not found in selected
                    if (genres.size() > 0) {
                        matches.add(manga);
                    }
                }
            } else {
                // No genres selected, skip filtering
                matches.addAll(allManga);
            }
        }

        /**
         *  Sort matches by specified sortOrder
         */
        private void sortMatches() {
            switch (sortOrder) {
                case POPULARITY: // sort by popularity
                    Collections.sort(matches, new Comparator<Manga>() {
                        @Override
                        public int compare(Manga lhs, Manga rhs) {
                            return ((Integer) rhs.hits).compareTo(lhs.hits);
                        }
                    });
                    break;
                case LAST_UPDATED: // sort by recently updated
                    Collections.sort(matches, new Comparator<Manga>() {
                        @Override
                        public int compare(Manga lhs, Manga rhs) {
                            return ((Long) rhs.lastChapterDate).compareTo(lhs.lastChapterDate);
                        }
                    });
                    break;
                case ALPHABETICAL: // sort alphabetically
                    Collections.sort(matches, new Comparator<Manga>() {
                        @Override // reverse comparison b/c default is Z to A
                        public int compare(Manga lhs, Manga rhs) {
                            return lhs.title.compareTo(rhs.title);
                        }
                    });
                    break;
                default: // NONE
                    Log.d("SORTING", "Did not dialog sort by genres properly.");
            }

            // reverse list
            if (isReverseOrder) {
                Collections.reverse(matches);
            }
        }

        /**
         * Only keep manga whose title matches query
         */
        private void matchQuery(CharSequence query) {
            // TODO replace with better fuzzy match algorithm
            String filterPattern = query.toString().toLowerCase().trim();
            for (Iterator<Manga> iterator = matches.iterator(); iterator.hasNext(); ) {
                Manga manga = iterator.next();
                if (!manga.title.toLowerCase().contains(filterPattern)) {
                    // Remove the current element from the iterator and the list.
                    iterator.remove();
                }
            }
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            // Tell CardLayoutAdapter to show the results of the filtering in the main UI thread
            Log.d("SORTING", "Published Results.");
            filteredManga = (List<Manga>) results.values;
            notifyDataSetChanged();
        }
    }
}
