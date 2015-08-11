package com.william.mangoreader.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

@JsonIgnoreProperties({"aka", "aka-alias", "alias", "artist", "artist_kw", "author_kw", "autoManga", "baka", "chapters",
        "created", "hits", "imageUrl", "language", "last_chapter_date", "released", "startsWith", "status", "title_kw",
        "type", "updatedKeywords", "url"})
public class MangaEdenMangaDetailItem {
    @JsonProperty("author")
    private String author;

    @JsonProperty("categories")
    private ArrayList<String> categories;

    @JsonProperty("chapters")
    private ArrayList<MangaEdenMangaChapterItem> chapters; //TODO make chapter object

    @JsonProperty("chapters_len")
    private int numChapters;

    @JsonProperty("created")
    private float dateCreated;

    @JsonProperty("description")
    private String description;

    @JsonProperty("title")
    private String title;

    @JsonProperty("image")
    private String imageUrl;

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public ArrayList<String> getCategories() {
        return categories;
    }

    public void setCategories(ArrayList<String> categories) {
        this.categories = categories;
    }

    public ArrayList<MangaEdenMangaChapterItem> getChapters() {
        return chapters;
    }

    public void setChapters(ArrayList<MangaEdenMangaChapterItem> chapters) {
        this.chapters = chapters;
    }

    public int getNumChapters() {
        return numChapters;
    }

    public void setNumChapters(int numChapters) {
        this.numChapters = numChapters;
    }

    public String getDescription() {
        return description;
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
