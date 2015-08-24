package com.william.mangoreader.parse;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.text.Html;
import android.widget.ImageView;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.william.mangoreader.R;
import com.william.mangoreader.activity.MangaItemActivity;
import com.william.mangoreader.model.MangaEdenImageItem;
import com.william.mangoreader.model.MangaEdenMangaChapterItem;
import com.william.mangoreader.model.MangaEdenMangaDetailItem;
import com.william.mangoreader.model.MangaEdenMangaListItem;

import java.io.IOException;
import java.util.ArrayList;

public class MangaEden {

    public static final String MANGAEDEN_MANGALIST_PREFIX = "https://www.mangaeden.com/api/list/0/?p=";
    public static final String MANGAEDEN_MANGALIST_SUFFIX = "&l=25";

    public static final String MANGAEDEN_IMAGE_CDN = "https://cdn.mangaeden.com/mangasimg/";

    public static final String MANGAEDEN_MANGADETAIL_PREFIX = "https://www.mangaeden.com/api/manga/";
    public static final String MANGAEDEN_CHAPTERDETAIL_PREFIX = "https://www.mangaeden.com/api/chapter/";

    public static final int IMAGE_PAGE_NUMBER_INDEX = 0;
    public static final int IMAGE_URL_INDEX = 1;

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
                    String chapterTitle = node.get(MANGA_DETAIL_TITLE_INDEX).asText();
                    chapterItem.setTitle(chapterTitle.equals("null") ? "" : chapterTitle);
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

    static public ArrayList<MangaEdenImageItem> parseMangaEdenMangaImageResponse(String jsonString) {
        ArrayList<MangaEdenImageItem> mangaImageModels = new ArrayList<>();
        try {
            JsonNode root = mapper.readTree(jsonString);
            ArrayNode images = (ArrayNode) root.get("images");

            for (JsonNode node : images) {
                MangaEdenImageItem item = new MangaEdenImageItem();
                item.setPageNumber(node.get(IMAGE_PAGE_NUMBER_INDEX).asInt());
                item.setUrl(node.get(IMAGE_URL_INDEX).asText());

                mangaImageModels.add(item);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return mangaImageModels;
    }

    static public void setThumbnail(String url, Context context, final ImageView imageView) {
        url = MANGAEDEN_IMAGE_CDN + url;
        Picasso.with(context)
                .load(url)
                .placeholder(R.drawable.ic_image_white)
                .fit().centerCrop()
                .transform(PaletteTransformation.instance())
                .into(imageView);

    }

    static public void setMangaArt(String url, final ImageView imageView, final MangaItemActivity activity) {
        url = MANGAEDEN_IMAGE_CDN + url;

        Picasso.with(activity)
                .load(url)
                .fit().centerCrop()
                .placeholder(R.drawable.ic_image_white)
                .transform(PaletteTransformation.instance())
                .into(imageView, new Callback.EmptyCallback() {
                    @Override
                    public void onSuccess() {
                        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                        imageView.setImageBitmap(bitmap);
                        activity.extractColorPalette(bitmap);
                        activity.loadFragments();
                    }
                });
    }

    static public void setMangaImage(String url, Context context, final ImageView imageView) {
        url = MANGAEDEN_IMAGE_CDN + url;

        Picasso.with(context)
                .load(url)
                .fit().centerInside()
                .noFade()
                .placeholder(R.drawable.ic_image_white)
                .into(imageView);

    }
}
