package com.william.mangoreader.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.william.mangoreader.R;
import com.william.mangoreader.parse.MangaEden;
import com.william.mangoreader.volley.VolleySingleton;

/**
 * Provide a reference to the views for each manga
 * Complex data items may need more than one view per item, and
 * you provide access to all the views for a data item in a view holder
 */
public class RecyclerViewHolder extends RecyclerView.ViewHolder {

    private TextView title;
    private TextView subtitle;
    private ImageView thumbnail;

    public RecyclerViewHolder(View itemView) {
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

    public void setThumbnail(String url, Context ctx) {
        if (url == null){
            thumbnail.setImageResource(R.drawable.manga3);
        }
        else {
            url = MangaEden.MANGAEDEN_IMAGE_CDN + url;
            ImageRequest request = new ImageRequest(url,
                    new Response.Listener<Bitmap>() {
                        @Override
                        public void onResponse(Bitmap bitmap) {
                            thumbnail.setImageBitmap(bitmap);
                        }
                    }, 0, 0, null,
                    new Response.ErrorListener() {
                        public void onErrorResponse(VolleyError error) {
                            //TODO handle
                        }
                    });
            VolleySingleton.getInstance(ctx).addToRequestQueue(request);
        }
    }

}
