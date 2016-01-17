package ckmah.mangoreader.database;

public class Chapter {
    public String id;
    public String title;
    public String number; //LOL yes, number is a string. something to do with titles.
    public long date;

    public boolean read;
    public int mostRecentPage;

    public Chapter() {
    }
}
