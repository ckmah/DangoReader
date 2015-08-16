package com.william.mangoreader.listener;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

/**
 * Created by Clarence on 8/9/2015.
 */
public abstract class BrowseMangaScrollListener extends RecyclerView.OnScrollListener {

    public int previousTotal = 0; // The total number of items in the dataset after the last load
    public static boolean loading = true; // True if we are still waiting for the last set of data to load.
    private int visibleThreshold = 5; // The minimum amount of items to have below your current scroll position before loading more.
    int firstVisibleItem, visibleItemCount, totalItemCount;

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

        visibleItemCount = recyclerView.getChildCount();
        totalItemCount = layoutManager.getItemCount();
        firstVisibleItem = layoutManager.findFirstVisibleItemPosition();

        if (loading) {
            if (totalItemCount > previousTotal) {
//                loading = false;
                previousTotal = totalItemCount;
            }
        }
        if (!loading && (totalItemCount - visibleItemCount)
                <= (firstVisibleItem + visibleThreshold)) {
            // End has been reached

            loadMore();

            loading = true;
        }
    }


    public abstract void loadMore();
}
