package com.maoyuchaxue.catfishclubnewsapp.data;

/**
 * A cursor that contains news, which could be move around.
 * This is the interface for all implementations of cursors.
 * Created by YU_Jason on 2017/9/5.
 */

public interface NewsCursor {
    News getNews();
    boolean moveToNext();
    boolean moveToPrevious();
    boolean existing();
    int getIndex();
}
