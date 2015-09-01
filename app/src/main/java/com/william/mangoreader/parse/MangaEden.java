package com.william.mangoreader.parse;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.widget.ImageView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.Cache;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.william.mangoreader.R;
import com.william.mangoreader.activity.MangaItemActivity;
import com.william.mangoreader.model.MangaEdenImageItem;
import com.william.mangoreader.model.MangaEdenMangaChapterItem;
import com.william.mangoreader.model.MangaEdenMangaDetailItem;
import com.william.mangoreader.model.MangaEdenMangaListItem;

import java.io.File;
import java.io.IOException;
import java.util.List;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.converter.GsonConverter;
import retrofit.http.GET;
import retrofit.http.Path;


public class MangaEden {

    public static final String MANGAEDEN_MANGALIST = "https://www.mangaeden.com/api/list/0";

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



    public class MangaEdenList {
        public List<MangaEdenMangaListItem> manga;
    }

    public class MangaEdenChapter {
        public List<MangaEdenImageItem> images;
    }

    public interface MangaEdenService {
        @GET("/list/0")
        void listAllManga(retrofit.Callback<MangaEdenList> callback);

        @GET("/manga/{id}")
        void getMangaDetails(@Path("id") String mangaId, retrofit.Callback<MangaEdenMangaDetailItem> callback);

        @GET("/chapter/{id}")
        void getMangaImages(@Path("id") String mangaId, retrofit.Callback<MangaEdenChapter> callback);
    }

    private static MangaEdenService service;

    public static MangaEdenService getMangaEdenService(Context context) {
        if (service == null) {
            Log.d("SORTING", "CREATING NEW CACHE");

            OkHttpClient okHttpClient = new OkHttpClient();
            try {
                // TODO try wiping cache to see what happens
                File httpCacheDirectory = new File(context.getCacheDir(), "responses");
                Cache cache = new Cache(httpCacheDirectory, 20 * 1024 * 1024);
                okHttpClient.setCache(cache);
            } catch(IOException e) {
                Log.e("OKHttp", "Could not create http cache", e);
            }

            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(MangaEdenMangaChapterItem.class, new MangaEdenMangaChapterItem.ChapterDeserializer())
                    .registerTypeAdapter(MangaEdenImageItem.class, new MangaEdenImageItem.ImageDeserializer())
                    .create();

            service = new RestAdapter.Builder()
                    .setEndpoint("https://www.mangaeden.com/api/")
                    .setClient(new OkClient(okHttpClient))
                    .setRequestInterceptor(new RequestInterceptor() {
                        @Override
                        public void intercept(RequestFacade request) {
                            // TODO test to make sure it does get cached i.e. doesn't use internet for the next day
                            int maxStale = 60 * 60 * 24; // tolerate 1 day stale
                            request.addHeader("Cache-Control", "public, max-age=" + maxStale);
                        }
                    })
                    .setConverter(new GsonConverter(gson))
                    .build()
                    .create(MangaEdenService.class);
        }
        return service;
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
