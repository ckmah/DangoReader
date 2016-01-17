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

    @Override
    public boolean equals(Object o) {
        // Two chapters are equal if they share the same id
        return (o instanceof Chapter) && id.equals(((Chapter) o).id);
    }
}
