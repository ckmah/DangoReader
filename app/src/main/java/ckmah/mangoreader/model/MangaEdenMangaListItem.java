package ckmah.mangoreader.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.List;

/**
 * Manga card element model, for use in browseMangaFragment and maybe MyLibraryFragment
 */
@JsonIgnoreProperties({"a", "s"})
public class MangaEdenMangaListItem implements Serializable {
    @JsonProperty("i") public String id;
    @JsonProperty("t") public String title;
    @JsonProperty("s") public String status;
    @JsonProperty("im") public String imageUrl;
    @JsonProperty("c") public List<String> genres;
    @JsonProperty("ld") public long lastChapterDate;
    @JsonProperty("h") public int hits;
}
