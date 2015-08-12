package com.william.mangoreader.adapter;

import android.app.Activity;
import android.content.Intent;
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
import com.william.mangoreader.activity.MangaItemActivity;
import com.william.mangoreader.daogen.DaoMaster;
import com.william.mangoreader.daogen.DaoSession;
import com.william.mangoreader.model.MangaEdenMangaListItem;
import com.william.mangoreader.parse.MangaEden;

import java.util.ArrayList;

/**
 * Layout adapter for adding cards
 */
public class CardLayoutAdapter extends RecyclerView.Adapter<RecyclerViewCardHolder>  {

    private ArrayList<MangaEdenMangaListItem> mangaEdenMangaListItems;
    private Activity activity;
    private boolean browseFlag; //right now, this specifies whether we're in a browsemangafragment or mylibraryfragment

    private SQLiteDatabase dbase;
    private DaoMaster daoMaster;
    private DaoSession daoSession;

    private Cursor cursor;

    public CardLayoutAdapter(Activity activity, boolean browseFlag) {
        mangaEdenMangaListItems = new ArrayList<>();
        this.activity = activity;
        this.browseFlag = browseFlag;
        // Pass context or other static stuff that will be needed.
    }

    @Override
    public void onBindViewHolder(RecyclerViewCardHolder viewHolder, int position) {
        viewHolder.setTitle(mangaEdenMangaListItems.get(position).getTitle());
        viewHolder.setSubtitle("Placeholder");
        MangaEden.setThumbnail(mangaEdenMangaListItems.get(position).getImageUrl(), activity.getApplicationContext(), viewHolder.getThumbnail());
        viewHolder.setMangaEdenId(mangaEdenMangaListItems.get(position).getId());
    }

    public void clearList() {
        mangaEdenMangaListItems.clear();
        notifyDataSetChanged();
    }

    private void addToList(int pos) {
        Toast.makeText(activity, "\"" + mangaEdenMangaListItems.get(pos).getTitle() + "\" added to your library.", Toast.LENGTH_SHORT).show();
    }

    private void removeFromList(int pos) {
        Toast.makeText(activity, "\"" + mangaEdenMangaListItems.get(pos).getTitle() + "\" removed from your library.", Toast.LENGTH_SHORT).show();

        // needed to update UI without reading in entire database
        mangaEdenMangaListItems.remove(pos);
        notifyItemRemoved(pos);
        notifyItemRangeChanged(pos, mangaEdenMangaListItems.size());
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
        return mangaEdenMangaListItems.size();
    }

    public void addItem(MangaEdenMangaListItem m) {
        mangaEdenMangaListItems.add(m);
        notifyItemInserted(mangaEdenMangaListItems.size());

    }


}
