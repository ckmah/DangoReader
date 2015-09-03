package com.william.mangoreader.model;

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
    private List<MangaEdenMangaChapterItem> chapters; //TODO make chapter object

    @SerializedName("chapters_len")
    private int numChapters;

    @SerializedName("created")
    private float dateCreated;

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

    public void setAuthor(String author) {
        this.author = author;
    }

    public List<String> getCategories() {
        return categories;
    }

    public void setCategories(List<String> categories) {
        this.categories = categories;
    }

    public List<MangaEdenMangaChapterItem> getChapters() {
        return chapters;
    }

    public void setChapters(List<MangaEdenMangaChapterItem> chapters) {
        this.chapters = chapters;
    }

    public int getNumChapters() {
        return numChapters;
    }

    public void setNumChapters(int numChapters) {
        this.numChapters = numChapters;
    }

    public String getDescription() {
        // Decodes any html escaping in description
        return Html.fromHtml(description).toString();
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public long getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(long dateCreated) {
        this.dateCreated = dateCreated;
    }

    public int getHits() {
        return hits;
    }

    public void setHits(int hits) {
        this.hits = hits;
    }

    public int getLanguage() {
        return language;
    }

    public void setLanguage(int language) {
        this.language = language;
    }

    public long getLastChapterDate() {
        return lastChapterDate;
    }

    public void setLastChapterDate(long lastChapterDate) {
        this.lastChapterDate = lastChapterDate;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
