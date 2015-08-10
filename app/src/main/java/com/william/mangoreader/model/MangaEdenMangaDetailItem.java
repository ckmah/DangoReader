package com.william.mangoreader.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

@JsonIgnoreProperties({"aka", "aka-alias", "alias", "artist", "artist_kw", "author_kw", "autoManga", "baka", "chapters",
        "created", "hits", "imageUrl", "language", "last_chapter_date", "released", "startsWith", "status", "title_kw",
        "type", "updatedKeywords", "url"})
public class MangaEdenMangaDetailItem {
    @JsonProperty("author")
    public String author;

    @JsonProperty("categories")
    public ArrayList<String> categories;

//    @JsonProperty("chapters")
//    public ArrayList<Object> chapters; //TODO make chapter object

    @JsonProperty("chapters_len")
    public int numChapters;

//    @JsonProperty("created")
//    public float dateCreated;

    @JsonProperty("description")
    public String description;

    @JsonProperty("title")
    public String title;

    @JsonProperty("image")
    public String imageUrl;


}
