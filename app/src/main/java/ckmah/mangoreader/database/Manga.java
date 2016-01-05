package ckmah.mangoreader.database;

import java.io.Serializable;
import java.util.List;

public class Manga implements Serializable{
    public String id;
    public String title;
    public String imageSrc;
    public long lastChapterDate;
    public List<String> genres;
    public int hits;
    public int status;

    public String author;
    public long dateCreated;
    public String description;
    public int language;
    public int numChapters;

    public boolean favorite = false;
    public String offlineLocation;
    public List<Chapter> chaptersList;

    public Manga() {
    }
}
