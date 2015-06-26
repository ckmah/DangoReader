package com.william.mangoreader.model;

import java.util.ArrayList;

/**
 * Manga card element model, for use in browseMangaFragment and maybe MyLibraryFragment
 */
public class MangaCardItem {
    private String id;

    private String title;

    private String imageUrl;

    private ArrayList<String> categories;

    private long lastChapterDate;

    private int hits;

    public MangaCardItem(String id){
        this.id = id;
        categories = new ArrayList<>();
    }

}
