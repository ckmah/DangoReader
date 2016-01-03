package ckmah.mangoreader.parse;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.Cache;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Response;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.william.mangoreader.R;

import java.io.File;
import java.io.IOException;
import java.util.List;

import ckmah.mangoreader.activity.MangaItemActivity;
import ckmah.mangoreader.model.MangaEdenImageItem;
import ckmah.mangoreader.model.MangaEdenMangaChapterItem;
import ckmah.mangoreader.model.MangaEdenMangaDetailItem;
import ckmah.mangoreader.model.MangaEdenMangaListItem;
import retrofit.Call;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import retrofit.http.GET;
import retrofit.http.Path;

public class MangaEden {
    public static final String MANGAEDEN_IMAGE_CDN = "https://cdn.mangaeden.com/mangasimg/";

    public class MangaEdenList {
        public List<MangaEdenMangaListItem> manga;
    }

    public class MangaEdenChapter {
        public List<MangaEdenImageItem> images;
    }

    public interface MangaEdenService {
        @GET("/api/list/0")
        Call<MangaEdenList> listAllManga();

        @GET("/api/manga/{id}")
        Call<MangaEdenMangaDetailItem> getMangaDetails(@Path("id") String mangaId);


        @GET("/api/chapter/{id}")
        Call<MangaEdenChapter> getMangaImages(@Path("id") String mangaId);
    }

    private static MangaEdenService service;

    public static MangaEdenService getMangaEdenService(Context context) {
        if (service == null) {
            Log.d("SORTING", "CREATING NEW CACHE");

            OkHttpClient okHttpClient = new OkHttpClient();
            // TODO try wiping cache to see what happens
            File httpCacheDirectory = new File(context.getCacheDir(), "responses");
            Cache cache = new Cache(httpCacheDirectory, 20 * 1024 * 1024);
            okHttpClient.setCache(cache);
            okHttpClient.interceptors().add(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    int maxStale = 60 * 60 * 24; // tolerate 1 day stale

                    Response response = chain.proceed(chain.request());
                    response.newBuilder().addHeader("Cache-Control", "public, max-age=" + maxStale);
                    return response;
                }
            });

            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(MangaEdenMangaChapterItem.class, new MangaEdenMangaChapterItem.ChapterDeserializer())
                    .registerTypeAdapter(MangaEdenImageItem.class, new MangaEdenImageItem.ImageDeserializer())
                    .create();

            service = new Retrofit.Builder()
                    .baseUrl("https://www.mangaeden.com/")
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create(gson))
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
