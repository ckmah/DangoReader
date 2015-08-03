package com.william.mangoreader.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.william.mangoreader.R;

/**
 * Created by Clarence on 7/20/2015.
 */
public class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {

    public TextView title;
    public TextView subtitle;
    public ImageView icon;

    public RecyclerViewHolder(View itemView) {
        super(itemView);
        title = (TextView) itemView.findViewById(R.id.card_title);
        subtitle = (TextView) itemView.findViewById(R.id.card_subtitle);
        itemView.setOnCreateContextMenuListener(this);

//        icon = (ImageView) itemView.findViewById(R.id.icon);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
        menu.setHeaderTitle("Select The Action");
        menu.add(0, view.getId(), 0, "Call");//groupId, itemId, order, title
        menu.add(0, view.getId(), 0, "SMS");
    }
}
