package com.java.team17.data;

/**
 * Created by YU_Jason on 2017/9/5.
 */

public interface NewsList {
    /**
     * @param index The index of the requested news item in the list.
     * @return The NewsCursor of the requested news item.
     * */
    NewsCursor getCursor(int index);
    NewsCursor getHeadCursor();
    int getLength();

    void refresh();
    void close();

}
