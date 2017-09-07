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

    public NewsListRecyclerViewListener(OnLoadMoreListener listener) {
        onLoadMoreListener = listener;
    }

    public interface OnLoadMoreListener {
        public void onLoadMore();
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
            Log.i("catclub",String.format("yes?"));
            mLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

            mItemCount = mLayoutManager.getItemCount();
            mLastCompletely = mLayoutManager.findLastCompletelyVisibleItemPosition();
        } else {
            return;
        }

        if (mLastLoad != mItemCount && mItemCount <= mLastCompletely + 3) {

            mLastLoad = mItemCount;
            onLoadMoreListener.onLoadMore();
        }
    }
}
