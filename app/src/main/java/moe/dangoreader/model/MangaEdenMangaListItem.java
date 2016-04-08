package moe.dangoreader.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Manga card element model, for use in browseMangaFragment and maybe MyLibraryFragment
 */
public class MangaEdenMangaListItem implements Serializable {
    @SerializedName("i")
    private String id;
    @SerializedName("t")
    private String title;
    @SerializedName("s")
    private String status;
    @SerializedName("im")
    private String imageUrl;
    @SerializedName("c")
    private List<String> genres;
    @SerializedName("ld")
    private long lastChapterDate;
    @SerializedName("h")
    private int hits;

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public long getLastChapterDate() {
        return lastChapterDate;
    }

    public List<String> getGenres() {
        return genres;
    }

    public String getStatus() {
        return status;
    }

    public int getHits() {
        return hits;
    }
}
