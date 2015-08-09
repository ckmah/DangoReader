package com.william.mangoreader.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.william.mangoreader.R;

/**
 * Provide a reference to the views for each manga
 * Complex data items may need more than one view per item, and
 * you provide access to all the views for a data item in a view holder
 */
public class RecyclerViewHolder extends RecyclerView.ViewHolder {

    public TextView title;
    public TextView subtitle;
    public ImageView icon;

    public RecyclerViewHolder(View itemView) {
        super(itemView);
        title = (TextView) itemView.findViewById(R.id.card_title);
        subtitle = (TextView) itemView.findViewById(R.id.card_subtitle);
    }

}
