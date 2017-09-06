package com.maoyuchaxue.catfishclubnewsapp.data;

/**
 * @deprecated Caching is now performed spontaneously by {@link NewsMetaInfoListCache}s, which is much
 * more convenient than distributing the caching task to different places such as a piece of news itself.
 * Created by YU_Jason on 2017/9/5.
 */

public class ContentCachedNews implements News {
    @Override
    public NewsContent getContent() {
        return null;
    }

    @Override
    public NewsMetaInfo getMetaInfo() {
        return null;
    }
}
