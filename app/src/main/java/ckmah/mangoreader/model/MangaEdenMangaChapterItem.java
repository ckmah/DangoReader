package ckmah.mangoreader.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.io.Serializable;
import java.lang.reflect.Type;

public class MangaEdenMangaChapterItem implements Serializable {

    private double number;
    private long date;
    private String title;
    private String id;

    public String getNumber() {
        // Prevent trailing zeros in chapter number; see http://stackoverflow.com/a/14126736
        if (number == (long) number)
            return String.format("%d", (long)number);
        else
            return String.format("%s", number);
    }

    public void setNumber(double number) {
        this.number = number;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public static class ChapterDeserializer implements JsonDeserializer<MangaEdenMangaChapterItem> {
        @Override
        public MangaEdenMangaChapterItem deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            MangaEdenMangaChapterItem chapter = new MangaEdenMangaChapterItem();
            JsonArray array = json.getAsJsonArray();
            chapter.setNumber(array.get(0).getAsDouble());
            chapter.setDate(array.get(1).getAsLong());
            chapter.setTitle(array.get(2).isJsonNull() ? "" : array.get(2).getAsString());
            chapter.setId(array.get(3).getAsString());
            return chapter;
        }
    }
}
