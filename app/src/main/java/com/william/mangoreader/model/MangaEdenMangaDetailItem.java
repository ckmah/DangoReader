package com.william.mangoreader.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.ArrayList;

@JsonIgnoreProperties({"aka", "aka-alias", "alias", "artist", "artist_kw", "author_kw",
        "autoManga", "baka", "imageUrl", "released", "startsWith", "title_kw",
        "type", "updatedKeywords", "url"})
public class MangaEdenMangaDetailItem implements Serializable {
    @JsonProperty("author")
    private String author;

    @JsonProperty("title")
    private String title;

    @JsonProperty("description")
    private String description;

    @JsonProperty("image")
    private String imageUrl;

    @JsonProperty("chapters_len")
    private int numChapters;

    @JsonProperty("created")
    private long dateCreated;

    @JsonProperty("hits")
    private int hits;

    @JsonProperty("language")
    private int language;

    @JsonProperty("last_chapter_date")
    private long lastChapterDate;

    @JsonProperty("status")
    private int status;

    @JsonProperty("chapters")
    private ArrayList<MangaEdenMangaChapterItem> chapters;

    @JsonProperty("categories")
    private ArrayList<String> categories;


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
