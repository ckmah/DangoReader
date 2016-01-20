package moe.dangoreader;

import java.util.List;

public class SortEvent {
    public int sortOrder;
    public boolean reverse;
    public List<Integer> genres;

    public SortEvent(int sortOrder, boolean reverse, List<Integer> genres) {
        this.sortOrder = sortOrder;
        this.reverse = reverse;
        this.genres = genres;
    }
}
