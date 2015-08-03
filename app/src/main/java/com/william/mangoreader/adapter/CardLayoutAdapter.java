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

    public CardLayoutAdapter(EntriesDataSource db, Activity activity) {
        mData = new ArrayList<MangaCardItem>();
        this.db = db;
        this.activity = activity;
        // Pass context or other static stuff that will be needed.
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder viewHolder, int position) {
        viewHolder.title.setText(mData.get(position).title);
        viewHolder.subtitle.setText("Placeholder");
//        viewHolder.icon.setBackgroundColor(Color.parseColor());
    }

    public void updateList(ArrayList<MangaCardItem> data) {
        mData = data;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(final ViewGroup viewGroup, final int position) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View itemView = inflater.inflate(R.layout.manga_card, viewGroup, false);

        CardView cardView = (CardView) itemView.findViewById(R.id.card_view);

        cardView.findViewById(R.id.card_menu_more).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                PopupMenu popupMenu = new PopupMenu(viewGroup.getContext(), view);
                popupMenu.inflate(R.menu.menu_card);

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.menu_add_lib:
                                db.createEntry(mData.get(position));
                                Toast.makeText(activity, "Keyword!", Toast.LENGTH_SHORT).show();
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                popupMenu.show();
            }
        });

        return new RecyclerViewHolder(itemView);
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
