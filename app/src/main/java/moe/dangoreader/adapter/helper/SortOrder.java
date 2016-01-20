package moe.dangoreader.adapter.helper;

/**
 * Indicates the sorting order for CardLayoutAdapter
 */
public enum SortOrder {
    NONE,
    POPULARITY,
    LAST_UPDATED,
    ALPHABETICAL;

    // Needs to match R.array.sort_items
    public static SortOrder fromIndex(int index) {
        switch (index) {
            case 0:
                return POPULARITY;
            case 1:
                return LAST_UPDATED;
            case 2:
                return ALPHABETICAL;
            default:
                return NONE;
        }
    }
}
