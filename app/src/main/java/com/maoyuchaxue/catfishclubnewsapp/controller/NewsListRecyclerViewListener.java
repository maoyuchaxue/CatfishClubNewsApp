package com.maoyuchaxue.catfishclubnewsapp.controller;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

/**
 * Created by catfish on 17/9/7.
 */

public class NewsListRecyclerViewListener extends RecyclerView.OnScrollListener {
    private LinearLayoutManager mLayoutManager;
    private int mItemCount, mLastCompletely, mLastLoad;
    private OnLoadMoreListener onLoadMoreListener;
    private boolean finished, firstBatchLoaded = false;

    public NewsListRecyclerViewListener(OnLoadMoreListener listener) {
        onLoadMoreListener = listener;
    }

    public interface OnLoadMoreListener {
        public void onLoadMore();
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    public void setFirstBatchLoaded(boolean firstBatchLoaded) {
        this.firstBatchLoaded = firstBatchLoaded;
    }

    public void resetLastLoad() {
        mLastLoad = 0;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
            mLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
            mItemCount = mLayoutManager.getItemCount();
            mLastCompletely = mLayoutManager.findLastCompletelyVisibleItemPosition();
        } else {
            return;
        }

        Log.i("loading", String.valueOf(firstBatchLoaded) + String.valueOf(finished));
        if (mLastLoad != mItemCount && mItemCount <= mLastCompletely + 3 && !finished && firstBatchLoaded) {
            mLastLoad = mItemCount;
            Log.i("loading","onloadmore");
            onLoadMoreListener.onLoadMore();
        }
    }
}
