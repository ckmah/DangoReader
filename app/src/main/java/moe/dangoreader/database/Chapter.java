package moe.dangoreader.database;

public class Chapter {
    public String id;
    public String title;
    public String number; //LOL yes, number is a string. something to do with titles.
    public long date;

    public boolean read;
    public int mostRecentPage;
    public String offlineLocation;
    /**
     * 0 = not downloaded, 1 = working, 2 = downloaded
     */
    public int downloadStatus;

    public Chapter() {
    }

    @Override
    public boolean equals(Object o) {
        // Two chapters are equal if they share the same id
        return (o instanceof Chapter) && id.equals(((Chapter) o).id);
    }
}
