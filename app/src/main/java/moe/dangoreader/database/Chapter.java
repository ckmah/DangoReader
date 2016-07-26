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
     * 0 = not downloaded, 1 = queued, 2 = downloading, 3 = downloaded
     */
    private int dlStatus;
    private int dlProgress;
    private int numPages;

    public Chapter() {
    }

    @Override
    public boolean equals(Object o) {
        // Two chapters are equal if they share the same id
        return (o instanceof Chapter) && id.equals(((Chapter) o).id);
    }

    public int getDlStatus() {
        return dlStatus;
    }

    public void setDlStatus(int dlStatus) {
        this.dlStatus = dlStatus;
    }

    public int getDlProgress() {
        return dlProgress;
    }

    public void setDlProgress(int dlProgress) {
        this.dlProgress = dlProgress;
    }

    public int getNumPages() {
        return numPages;
    }

    public void setNumPages(int numPages) {
        this.numPages = numPages;
    }
}
