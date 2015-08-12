package com.william.mangoreader.parse;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.graphics.Palette;
import android.text.Html;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.william.mangoreader.R;
import com.william.mangoreader.model.MangaEdenMangaChapterItem;
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

    private static final int MANGA_DETAIL_NUMBER_INDEX = 0;
    private static final int MANGA_DETAIL_DATE_INDEX = 1;
    private static final int MANGA_DETAIL_TITLE_INDEX = 2;
    private static final int MANGA_DETAIL_ID_INDEX = 3;

    private static int mainColor;

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

            // map chapter listing
            ArrayList<MangaEdenMangaChapterItem> chapterList = new ArrayList<>();
            JsonNode chapterListNode = root.withArray("chapters");
            for (JsonNode node : chapterListNode) {
                MangaEdenMangaChapterItem chapterItem = new MangaEdenMangaChapterItem();
                if (node.isArray()) {
                    chapterItem.setNumber(node.get(MANGA_DETAIL_NUMBER_INDEX).asInt());
                    chapterItem.setDate(node.get(MANGA_DETAIL_DATE_INDEX).asLong());
                    chapterItem.setTitle(node.get(MANGA_DETAIL_TITLE_INDEX).asText());
                    chapterItem.setId(node.get(MANGA_DETAIL_ID_INDEX).asText());
                }
                chapterList.add(chapterItem);
            }

            item.setAuthor(author);
            item.setDescription(Html.fromHtml(description).toString());
            item.setImageUrl(imageUrl);
            item.setTitle(title);
            item.setChapters(chapterList);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return item;
    }

    static public void setThumbnail(String url, Context ctx, final ImageView imageView) {
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

    static public void setMangaArt(String url, Context ctx, final ImageView imageView, final Window window, final CollapsingToolbarLayout collapsingToolbarLayout) {
        if (url == null) {
            imageView.setImageResource(R.drawable.manga3);
        } else {
            url = MangaEden.MANGAEDEN_IMAGE_CDN + url;
            ImageRequest request = new ImageRequest(url,
                    new Response.Listener<Bitmap>() {
                        @Override
                        public void onResponse(Bitmap bitmap) {

                            Palette palette = Palette.from(bitmap).generate();

                            // use most occuring color as main color
                            int most = 0;
                            int mainColor = 0;

                            for (Palette.Swatch swatch : palette.getSwatches()) {
                                int population = swatch.getPopulation();
                                Log.d("POPULATION", "" + population);
                                if (population > most) {
                                    most = population;
                                    mainColor = swatch.getRgb();
                                }
                            }

                            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                            Log.d("STATUS_BAR_COLOR", "" + mainColor);
                            window.setStatusBarColor(mainColor);

                            collapsingToolbarLayout.setContentScrimColor(mainColor);
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
//    static public int getMainColor() {
//        return mainColor;
//    }
}
