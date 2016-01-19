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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ckmah.mangoreader.UserLibraryHelper;
import ckmah.mangoreader.activity.MangaItemActivity;
import ckmah.mangoreader.database.Chapter;
import ckmah.mangoreader.database.Manga;
import ckmah.mangoreader.model.MangaEdenImageItem;
import ckmah.mangoreader.model.MangaEdenMangaChapterItem;
import ckmah.mangoreader.model.MangaEdenMangaDetailItem;
import ckmah.mangoreader.model.MangaEdenMangaListItem;
import io.paperdb.Paper;
import retrofit.Call;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import retrofit.http.GET;
import retrofit.http.Path;

public class MangaEden {
    public static final String MANGAEDEN_IMAGE_CDN = "https://cdn.mangaeden.com/mangasimg/";
    private static MangaEdenService service, serviceNoCache;

    // Default is to rely on cache when possible
    public static MangaEdenService getMangaEdenService(Context context) {
        return getMangaEdenService(context, false);
    }

    public static MangaEdenService getMangaEdenService(Context context, boolean skipCache) {
        if (skipCache) {
            if (serviceNoCache == null) {
                Log.d("MANGAEDEN", "Service without cache");

                // "no-cache" flag forces the service to check for updates
                // See https://developers.google.com/web/fundamentals/performance/optimizing-content-efficiency/http-caching?hl=en#cache-control
                serviceNoCache = buildService(context, "public, no-cache");
            }
            return serviceNoCache;
        } else {
            if (service == null) {
                Log.d("MANGAEDEN", "Creating MangaEdenService");

                // Tolerate 1 day stale
                String cacheControl = "public, max-age=" + 60 * 60 * 24;
                service = buildService(context, cacheControl);
            }
            return service;
        }
    }

    private static MangaEdenService buildService(Context context, final String cacheControl) {
        OkHttpClient okHttpClient = new OkHttpClient();
        // TODO try wiping cache to see what happens
        File httpCacheDirectory = new File(context.getCacheDir(), "responses");
        Cache cache = new Cache(httpCacheDirectory, 20 * 1024 * 1024);
        okHttpClient.setCache(cache);
        okHttpClient.interceptors().add(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Response response = chain.proceed(chain.request());
                response.newBuilder().addHeader("Cache-Control", cacheControl);
                return response;
            }
        });

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(MangaEdenMangaChapterItem.class, new MangaEdenMangaChapterItem.ChapterDeserializer())
                .registerTypeAdapter(MangaEdenImageItem.class, new MangaEdenImageItem.ImageDeserializer())
                .create();

        return new Retrofit.Builder()
                .baseUrl("https://www.mangaeden.com/")
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
                .create(MangaEdenService.class);
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

    public static List<Manga> convertMangaListItemsToManga(List<MangaEdenMangaListItem> mangaListItems) {
        Log.d("MangaEden", "Converting manga models...");
        Manga manga;
        List<Manga> result = new ArrayList<>();
        for (MangaEdenMangaListItem mangaListItem : mangaListItems) {
            manga = Paper.book(UserLibraryHelper.USER_LIBRARY_DB).read(mangaListItem.id);
            if (manga == null) {
                manga = new Manga();
                manga.id = mangaListItem.id;
                manga.title = mangaListItem.title;
                manga.imageSrc = mangaListItem.imageUrl;
                manga.lastChapterDate = mangaListItem.lastChapterDate;
                manga.genres = mangaListItem.genres;
                manga.status = Integer.parseInt(mangaListItem.status);
                manga.hits = mangaListItem.hits;
            }
            result.add(manga);
        }
        Log.d("MangaEden", "Finished converting manga models...");
        return result;
    }

    public static List<Chapter> convertChapterItemstoChapters(List<MangaEdenMangaChapterItem> c) {
        List<Chapter> chapters = new ArrayList<Chapter>();
        for (MangaEdenMangaChapterItem ch : c) {
            Chapter temp = new Chapter();
            temp.id = ch.getId();
            temp.title = ch.getTitle();
            temp.number = ch.getNumber();
            temp.date = ch.getDate();
            temp.read = false;
            temp.mostRecentPage = -1;
            chapters.add(temp);
        }
        Collections.reverse(chapters);
        return chapters;
    }

    public interface MangaEdenService {
        @GET("/api/list/0")
        Call<MangaEdenList> listAllManga();

        @GET("/api/manga/{id}")
        Call<MangaEdenMangaDetailItem> getMangaDetails(@Path("id") String mangaId);


        @GET("/api/chapter/{id}")
        Call<MangaEdenChapter> getMangaImages(@Path("id") String mangaId);
    }

    public class MangaEdenList {
        public List<MangaEdenMangaListItem> manga;
    }

    public class MangaEdenChapter {
        public List<MangaEdenImageItem> images;
    }

}
