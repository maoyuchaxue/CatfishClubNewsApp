package com.maoyuchaxue.catfishclubnewsapp.data;


import com.maoyuchaxue.catfishclubnewsapp.data.exceptions.NewsSourceException;

/**
 * Created by YU_Jason on 2017/9/5.
 */

public interface NewsSource {
    /**
     * Queries in the source a list of news based on the specified conditions.<br>
     *     The lists are paged according to the page size returned by <code>getPageSize()</code>.
     * @param pageNo The requested page number (starting from 1).
     * @param category The category tag to be queried. If <code>null</code> is given,
     *                 no restriction in the category tag of the news shall be assumed.
     * @param keyword The keyword in news titles to be queried. If <code>null</code> is given,
     *                no restriction in news titles shall be assumed.
     * */
    NewsMetaInfo[] getNewsMetaInfoList(int pageNo, String keyword, NewsCategoryTag category) throws NewsSourceException;
    NewsContent getNewsContent(String id) throws NewsSourceException;
    int getPageSize();

    void close() throws NewsSourceException;
}
