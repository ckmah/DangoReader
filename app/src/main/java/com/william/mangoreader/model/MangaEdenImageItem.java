package com.william.mangoreader.model;


import java.io.Serializable;

public class MangaEdenImageItem implements Serializable {

    private int pageNumber;
    private String url;

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int page) {
        this.pageNumber = page;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
