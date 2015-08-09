package com.william.mangoreader.adapter;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.widget.CardView;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.william.mangoreader.R;
import com.william.mangoreader.daogen.DaoMaster;
import com.william.mangoreader.daogen.DaoSession;
import com.william.mangoreader.model.MangaCardItem;

import java.util.ArrayList;

/**
 * Layout adapter for adding cards
 */
public class CardLayoutAdapter extends RecyclerView.Adapter<RecyclerViewHolder> {

    private ArrayList<MangaCardItem> mangaCardItems;
    private Activity activity;
    private boolean browseFlag; //right now, this specifies whether we're in a browsemangafragment or mylibraryfragment

    private SQLiteDatabase dbase;
    private DaoMaster daoMaster;
    private DaoSession daoSession;

    private Cursor cursor;

    public CardLayoutAdapter(Activity activity, boolean browseFlag) {
        mangaCardItems = new ArrayList<>();
        this.activity = activity;
        this.browseFlag = browseFlag;
        // Pass context or other static stuff that will be needed.
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder viewHolder, int position) {
        viewHolder.title.setText(mangaCardItems.get(position).title);
        viewHolder.subtitle.setText("Placeholder");
    }

    @Deprecated
    public void updateList(ArrayList<MangaCardItem> data) {
        mangaCardItems = data;
        notifyDataSetChanged();
    }

    private void addToList(int pos) {
        Toast.makeText(activity, "\"" + mangaCardItems.get(pos).title + "\" added to your library.", Toast.LENGTH_SHORT).show();
    }

    private void removeFromList(int pos) {
        Toast.makeText(activity, "\"" + mangaCardItems.get(pos).title + "\" removed from your library.", Toast.LENGTH_SHORT).show();

        // needed to update UI without reading in entire database
        mangaCardItems.remove(pos);
        notifyItemRemoved(pos);
        notifyItemRangeChanged(pos, mangaCardItems.size());
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(final ViewGroup viewGroup, int position) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View itemView = inflater.inflate(R.layout.manga_card, viewGroup, false);
        final RecyclerViewHolder holder = new RecyclerViewHolder(itemView);

        final CardView cardView = (CardView) itemView.findViewById(R.id.card_view);

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
        return mangaCardItems.size();
    }

    public void addItem(MangaCardItem m) {
        mangaCardItems.add(m);
        notifyItemInserted(mangaCardItems.size());

    }


}
