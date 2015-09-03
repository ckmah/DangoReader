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

    @SerializedName("title")
    private String title;

    @SerializedName("image")
    private String imageUrl;

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
}
