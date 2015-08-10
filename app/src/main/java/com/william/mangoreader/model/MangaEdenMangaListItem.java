package com.william.mangoreader.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

/**
 * Manga card element model, for use in browseMangaFragment and maybe MyLibraryFragment
 */
@JsonIgnoreProperties({"a", "s"})
public class MangaEdenMangaListItem {
    @JsonProperty("i")
    private String id;

    @JsonProperty("t")
    private String title;

    @JsonProperty("s")
    private String status;

    @JsonProperty("im")
    private String imageUrl;

    @JsonProperty("c")
    private ArrayList<String> genres;

    @JsonProperty("ld")
    private long lastChapterDate;

    @JsonProperty("h")
    private int hits;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public ArrayList<String> getGenres() {
        return genres;
    }

    public void setGenres(ArrayList<String> genres) {
        this.genres = genres;
    }

    public long getLastChapterDate() {
        return lastChapterDate;
    }

    public void setLastChapterDate(long lastChapterDate) {
        this.lastChapterDate = lastChapterDate;
    }

    public int getHits() {
        return hits;
    }

    public void setHits(int hits) {
        this.hits = hits;
    }

}
