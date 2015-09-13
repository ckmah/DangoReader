package com.william.mangoreader.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Manga card element model, for use in browseMangaFragment and maybe MyLibraryFragment
 */
public class MangaEdenMangaListItem implements Serializable {
    @SerializedName("i") public String id;
    @SerializedName("t") public String title;
    @SerializedName("s") public String status;
    @SerializedName("im") public String imageUrl;
    @SerializedName("c") public List<String> genres;
    @SerializedName("ld") public long lastChapterDate;
    @SerializedName("h") public int hits;
}
