package moe.dangoreader.model;

import android.text.Html;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class MangaEdenMangaDetailItem implements Serializable {
    @SerializedName("author")
    private String author;
    @SerializedName("categories")
    private List<String> categories;
    @SerializedName("chapters")
    private List<MangaEdenMangaChapterItem> chapters;
    @SerializedName("chapters_len")
    private int numChapters;
    @SerializedName("created")
    private long dateCreated;
    @SerializedName("description")
    private String description;
    @SerializedName("hits")
    private int hits;
    @SerializedName("title")
    private String title;
    @SerializedName("language")
    private int language;
    @SerializedName("image")
    private String imageUrl;
    @SerializedName("last_chapter_date")
    private long lastChapterDate;
    @SerializedName("status")
    private int status;

    public String getAuthor() {
        return author;
    }

    public List<String> getCategories() {
        return categories;
    }

    public List<MangaEdenMangaChapterItem> getChapters() {
        return chapters;
    }

    public int getNumChapters() {
        return numChapters;
    }

    public String getDescription() {
        // Decodes any html escaping in description
        return Html.fromHtml(description).toString();
    }

    public String getTitle() {
        return title;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public long getDateCreated() {
        return dateCreated;
    }

    public int getHits() {
        return hits;
    }

    public int getLanguage() {
        return language;
    }

    public long getLastChapterDate() {
        return lastChapterDate;
    }

    public int getStatus() {
        return status;
    }

}
