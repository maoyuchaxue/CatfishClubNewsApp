package com.maoyuchaxue.catfishclubnewsapp.data;


import com.maoyuchaxue.catfishclubnewsapp.data.util.Pair;
import java.util.HashMap;

/**
 * Created by YU_Jason on 2017/9/5.
 */

public class SourceNewsList implements NewsList {
    private class SourceNewsCursor implements NewsCursor{
        private NewsMetaInfo metaInfo;
        // the index in the real list (stable)
        private int indexInList, oPage;

        public SourceNewsCursor(int indexInList, NewsMetaInfo metaInfo, int oPage){
            this.indexInList = indexInList;
            this.metaInfo = metaInfo;
            this.oPage = oPage;
        }

        @Override
        public NewsMetaInfo getNewsMetaInfo() {
            return metaInfo;
        }

        @Override
        public NewsContent getNewsContent() {
            try {
                return contentSource.getNewsContent(metaInfo.getId());
            } catch(Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public NewsCursor next() {
            return getCursorAt(indexInList - 1, oPage, true);
        }

        @Override
        public NewsCursor previous() {
            return getCursorAt(indexInList + 1, oPage, false);
        }

        @Override
        public int getIndex() {
            return 0;
        }

        @Override
        public int getIndexInList(){
            return indexInList;
        }

        @Override
        protected void finalize() throws Throwable {
            super.finalize();
            close();
        }

        public void close() {
        }
    }

    private NewsMetaInfoListSource metaInfoSource;
    private NewsContentSource contentSource;
    private String keyword;
    private NewsCategoryTag categoryTag;

    // triple: (index, cursor);
    private HashMap<Integer, SourceNewsCursor> buffer = new HashMap<Integer, SourceNewsCursor>();


    public SourceNewsList(NewsMetaInfoListSource metaInfoSource,
                          NewsContentSource contentSource){
        this(metaInfoSource, contentSource, null, null);
//        this.metaInfoSource = metaInfoSource;
//        this.contentSource = contentSource;
    }

    public SourceNewsList(NewsMetaInfoListSource metaInfoSource,
                          NewsContentSource contentSource,
                          String keyword, NewsCategoryTag newsCategoryTag){
        this.metaInfoSource = metaInfoSource;
        this.contentSource = contentSource;
        this.keyword = keyword;
        this.categoryTag = newsCategoryTag;
    }



    /**
     *
     *
     * */
    @Override
    public NewsCursor getCursor(int index) {
        return getCursorAt(index, 1, true);
    }

    private void addToBuffer(SourceNewsCursor cursor){
//        if(buffer.containsKey(cursor.getIndexInList()))
//            return;
        buffer.put(cursor.getIndexInList(), cursor);
    }


    /**
     *
     *
     *
     * */
    @Override
    public NewsCursor getHeadCursor() {
        try {
            Pair<NewsMetaInfo[], Integer> res =
                    metaInfoSource.getNewsMetaInfoListByPageNo(1, keyword, categoryTag);
            for(int i = 0; i < res.first.length; i ++)
                addToBuffer(new SourceNewsCursor(res.second - i, res.first[i], 1));

            return buffer.get(res.second);
        } catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    private NewsCursor getCursorAt(int index, int oPage, boolean next){
        NewsCursor cursor = buffer.get(index);
        if(cursor != null)  // already in buffer
            return cursor;

        // not in buffer
        // the expected new page
        int nPage = next ? (oPage + 1) : (oPage - 1);
        try{
            Pair<NewsMetaInfo[], Integer> res;
            while(true){
                res =
                        metaInfoSource.getNewsMetaInfoListByPageNo(nPage, keyword, categoryTag);
                // compute the index range of the page
                int lo = res.second - res.first.length;
                int hi = res.second;
                if(index > lo && index <= hi) // if it is on the page
                    break;
                if(index <= lo){
                    nPage += (lo - index + 1) / metaInfoSource.getPageSize();
                    if((lo - index + 1) % metaInfoSource.getPageSize() != 0)
                        ++ nPage;
                } else{
                    nPage -= (index - hi) / metaInfoSource.getPageSize();
                    if((index - hi) % metaInfoSource.getPageSize() != 0)
                        -- nPage;
                }
            }
            for(int i = 0; i < res.first.length; i ++)
                addToBuffer(new SourceNewsCursor(res.second - i, res.first[i], nPage));
            return buffer.get(index);
        } catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     *
     * */
    @Override
    public int getLength() {
        //TODO: getLength() of SourceNewsList
        return 0;
    }

    @Override
    public void close() {

    }

    // only for testing
    public static void main(String args[]) throws Exception{
        SourceNewsList newsList = new SourceNewsList(
                new WebNewsMetaInfoListSource("http://166.111.68.66:2042/news/action/query/latest"),
                new WebNewsContentSource("http://166.111.68.66:2042/news/action/query/detail")
        );
        NewsCursor cursor = newsList.getHeadCursor();
        for(int i = 0; i < 50; i ++){
            System.out.println(cursor.getNewsMetaInfo().getTitle());
            cursor = cursor.next();
        }
        cursor = newsList.getCursor(1373727 - 100);
        System.out.print("The 100: ");
        System.out.println(cursor.getNewsMetaInfo().getTitle());
    }
}
