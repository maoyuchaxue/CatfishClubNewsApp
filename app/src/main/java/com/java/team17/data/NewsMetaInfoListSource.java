package com.java.team17.data;


import com.java.team17.data.util.Pair;

import com.java.team17.data.exceptions.NewsSourceException;

/**
 * Created by YU_Jason on 2017/9/5.
 */

public interface NewsMetaInfoListSource {
    /**
     * Queries in the source a list of news based on the specified conditions.<br>
     *     The lists are paged according to the page size returned by <code>getPageSize()</code>.
     * @param pageNo The requested page number (starting from 1).
     * @param keyword The keyword in news titles to be queried. If <code>null</code> is given,
     *                no restriction in news titles shall be assumed.
     * @param category The category tag to be queried. If <code>null</code> is given,
     * @param type  */
    Pair<NewsMetaInfo[], Integer> getNewsMetaInfoListByPageNo(int pageNo, String keyword, NewsCategoryTag category, int type) throws NewsSourceException;
    Pair<NewsMetaInfo[], Integer> getNewsMetaInfoListByIndex(int index, String keyword, NewsCategoryTag category) throws NewsSourceException;
    boolean isReversed();

    int getPageSize();
    int getCapacity();
    void refresh();
    boolean remove(String id);

    void close() throws NewsSourceException;
}
