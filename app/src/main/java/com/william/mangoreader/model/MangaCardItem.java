package com.william.mangoreader.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

/**
 * Manga card element model, for use in browseMangaFragment and maybe MyLibraryFragment
 */
@JsonIgnoreProperties({"a", "s"})
public class MangaCardItem {
    @JsonProperty("i")
    public String id;

    @JsonProperty("t")
    public String title;

    @JsonProperty("s")
    public String status;

    @JsonProperty("im")
    public String imageUrl;

    @JsonProperty("c")
    public ArrayList<String> genres;

    @JsonProperty("ld")
    public long lastChapterDate;

    @JsonProperty("h")
    public int hits;
}
