package ckmah.mangoreader.model;


import android.graphics.Bitmap;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.io.Serializable;
import java.lang.reflect.Type;

public class MangaEdenImageItem implements Serializable {

    private int pageNumber;
    private String url;
    private Bitmap bitmap;

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int page) {
        this.pageNumber = page;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public static class ImageDeserializer implements JsonDeserializer<MangaEdenImageItem> {
        @Override
        public MangaEdenImageItem deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            MangaEdenImageItem item = new MangaEdenImageItem();
            item.setPageNumber(json.getAsJsonArray().get(0).getAsInt());
            item.setUrl(json.getAsJsonArray().get(1).getAsString());
            return item;
        }
    }
}
