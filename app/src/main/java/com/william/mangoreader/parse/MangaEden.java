package com.william.mangoreader.parse;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.william.mangoreader.R;
import com.william.mangoreader.model.MangaEdenMangaDetailItem;
import com.william.mangoreader.model.MangaEdenMangaListItem;
import com.william.mangoreader.volley.VolleySingleton;

import java.io.IOException;
import java.util.ArrayList;

public class MangaEden {

    public static final String MANGAEDEN_MANGALIST_PREFIX = "https://www.mangaeden.com/api/list/0/?p=";
    public static final String MANGAEDEN_MANGALIST_SUFFIX = "&l=25";

    public static final String MANGAEDEN_IMAGE_CDN = "https://cdn.mangaeden.com/mangasimg/";

    public static final String MANGAEDEN_MANGADETAIL_PREFIX = "https://www.mangaeden.com/api/manga/";

    public static final ObjectMapper mapper = new ObjectMapper(); // create once, reuse

    static public ArrayList<MangaEdenMangaListItem> parseMangaEdenMangaListResponse(String jsonString) {

        ArrayList<MangaEdenMangaListItem> mangaCardModels = new ArrayList<>();

        try {
            JsonNode root = mapper.readTree(jsonString);
            ArrayNode mangas = (ArrayNode) root.get("manga");

            for (int i = 0; i < mangas.size(); i++) {
                JsonNode m = mangas.get(i);
                MangaEdenMangaListItem item = mapper.treeToValue(m, MangaEdenMangaListItem.class);
                mangaCardModels.add(item);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return mangaCardModels;
    }

    static public MangaEdenMangaDetailItem parseMangaEdenMangaDetailResponse(String jsonString) {
        MangaEdenMangaDetailItem item = new MangaEdenMangaDetailItem();
        try {
            JsonNode root = mapper.readTree(jsonString);
            String author = root.get("author").asText();
            String title = root.get("title").asText();
            String description = root.get("description").asText();
            String imageUrl = root.get("image").asText();

            item.author = author;
            item.description = description;
            item.imageUrl = imageUrl;
            item.title = title;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return item;
    }

    static public void setImage(String url, Context ctx, final ImageView imageView) {
        if (url == null) {
            imageView.setImageResource(R.drawable.manga3);
        } else {
            url = MangaEden.MANGAEDEN_IMAGE_CDN + url;
            ImageRequest request = new ImageRequest(url,
                    new Response.Listener<Bitmap>() {
                        @Override
                        public void onResponse(Bitmap bitmap) {
                            imageView.setImageBitmap(bitmap);
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
