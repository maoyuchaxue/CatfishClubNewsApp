package com.maoyuchaxue.catfishclubnewsapp.data;

import com.maoyuchaxue.catfishclubnewsapp.data.exceptions.NewsSourceException;

/**
 * Created by YU_Jason on 2017/9/7.
 */

public interface NewsContentCache extends NewsContentSource {
    /**
     * Loads the content of a piece of news with the specified ID from the cache if possible.
     *
     * @param id The ID of the news.
     * @return The content of the news. If the news content has not been cached before, the return value
     * would be <code>null</code>.
     * */
    NewsContent getNewsContentFromCache(String id) throws NewsSourceException;

    /**
     * Caches the news content. If the old cache exists, it would be substituted by the newly fetched
     * record.
     * @param id The ID of the news.
     * @return The updated content of the news.
     * */
    NewsContent cacheNewsContent(String id) throws NewsSourceException;

    /**
     * Flushes the possible buffer of the cache, which results in
     * an instant update in the medium where the cache is maintained.
     *
     * */
    void flush();
}
