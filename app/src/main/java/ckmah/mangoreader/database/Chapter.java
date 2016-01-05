package ckmah.mangoreader.database;

import java.io.Serializable;

public class Chapter implements Serializable {
    public String id;
    public String title;
    public String number; //LOL yes, number is a string. something to do with titles.
    public double date;

    public boolean read;
    public int mostRecentPage;

    public Chapter() {
    }
}
