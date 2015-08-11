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
public class RecyclerViewCardHolder extends RecyclerView.ViewHolder {

    private TextView title;
    private TextView subtitle;
    private ImageView thumbnail;
    private String mangaEdenId; //TODO what do in future?

    public RecyclerViewCardHolder(View itemView) {
        super(itemView);
        title = (TextView) itemView.findViewById(R.id.card_title);
        subtitle = (TextView) itemView.findViewById(R.id.card_subtitle);
        thumbnail = (ImageView) itemView.findViewById(R.id.card_thumbnail);

    }

    public void setTitle(String title) {
        this.title.setText(title);
    }

    public void setSubtitle(String subtitle) {
        this.subtitle.setText(subtitle);
    }

    public ImageView getThumbnail(){
        return thumbnail;
    }

    public void setMangaEdenId(String mangaEdenId) {
        this.mangaEdenId = mangaEdenId;
    }

    public String getMangaEdenId() {
        return mangaEdenId;
    }


}
