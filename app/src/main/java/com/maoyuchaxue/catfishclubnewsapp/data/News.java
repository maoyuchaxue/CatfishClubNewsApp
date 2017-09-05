package com.maoyuchaxue.catfishclubnewsapp.data;

/**
 * An entity of a piece of news.
 * A News is in essence composed of two chunks of data: {@link com.maoyuchaxue.catfishclubnewsapp.data.NewsMetaInfo}
 * and {@link com.maoyuchaxue.catfishclubnewsapp.data.NewsContent}.
 * Practically, MetaInfo refers to those that can be acquired in a news list, while Content refers to
 * the information retrieved from a query on a single piece of news.
 * @deprecated The functionality has been completely replaced by {@link NewsCursor}.
 *
 * Created by YU_Jason on 2017/9/5.
 */

interface News {

    NewsContent getContent();

    NewsMetaInfo getMetaInfo();
}
