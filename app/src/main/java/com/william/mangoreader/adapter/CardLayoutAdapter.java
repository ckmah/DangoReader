package com.william.mangoreader.adapter;

import android.app.Activity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.william.mangoreader.R;
import com.william.mangoreader.db.EntriesDataSource;
import com.william.mangoreader.model.MangaCardItem;

import java.util.ArrayList;

/**
 * Created by Clarence on 7/20/2015.
 */
public class CardLayoutAdapter extends RecyclerView.Adapter<RecyclerViewHolder> {

    private ArrayList<MangaCardItem> mData;
    private EntriesDataSource db;
    private Activity activity;
    private boolean browseFlag;

    public CardLayoutAdapter(EntriesDataSource db, Activity activity, boolean browseFlag) {
        mData = new ArrayList<MangaCardItem>();
        this.db = db;
        this.activity = activity;
        this.browseFlag = browseFlag;
        // Pass context or other static stuff that will be needed.
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder viewHolder, int position) {
        viewHolder.title.setText(mData.get(position).title);
        viewHolder.subtitle.setText("Placeholder");
    }

    public void updateList(ArrayList<MangaCardItem> data) {
        mData = data;
        notifyDataSetChanged();
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
                                    System.out.println("position: " + pos);
                                    db.createEntry(mData.get(pos));
                                    Toast.makeText(activity, "\"" + mData.get(pos).title + "\" added to your library.", Toast.LENGTH_SHORT).show();
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
                                    System.out.println("position: " + pos);
                                    db.deleteEntry(mData.get(pos));
                                    Toast.makeText(activity, "\"" + mData.get(pos).title + "\" removed from your library.", Toast.LENGTH_SHORT).show();
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
        return mData.size();
    }

    public void addItem(MangaCardItem m) {
        mData.add(m);
        notifyItemInserted(mData.size());
    }


}
