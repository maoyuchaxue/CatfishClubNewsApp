package com.maoyuchaxue.catfishclubnewsapp.data;


import android.util.SparseArray;

import com.maoyuchaxue.catfishclubnewsapp.data.util.Pair;

import java.util.Random;

/**
 * Created by YU_Jason on 2017/9/5.
 */

public class SourceNewsList implements NewsList {
    private static final int LOOKUP_LIM = 3;
    private boolean sourceReversed;
    private int nextD, previousD, type;


    private class SourceNewsCursor implements NewsCursor{
        private NewsMetaInfo metaInfo;
        // the index in the real list (stable)
        private int indexInList, oPage;
        private boolean closed;

        public SourceNewsCursor(int indexInList, NewsMetaInfo metaInfo, int oPage){
            this.indexInList = indexInList;
            this.metaInfo = metaInfo;
            this.oPage = oPage;

            closed = true;
        }

        @Override
        public NewsMetaInfo getNewsMetaInfo() {
            if(closed)
                throw new NewsCursorClosedException();
            return metaInfo;
        }

        @Override
        public NewsContent getNewsContent() {
            if(closed)
                throw new NewsCursorClosedException();
            try {
                return contentSource.getNewsContent(metaInfo);
            } catch(Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public NewsCursor next() {
            if(closed)
                throw new NewsCursorClosedException();
            return getCursorAt(indexInList + nextD, oPage, true);
        }

        @Override
        public NewsCursor previous() {
            if(closed)
                throw new NewsCursorClosedException();
            return getCursorAt(indexInList + previousD, oPage, false);
        }

        @Override
        public int getIndex() {
            if(closed)
                throw new NewsCursorClosedException();
            return 0;
        }

        @Override
        public int getIndexInList(){
            if(closed)
                throw new NewsCursorClosedException();
            return indexInList;
        }

        private void shiftHigherInArray(SparseArray<SourceNewsCursor> arr, int ind){
            int sz = arr.size();
            for(int i = 0; i < sz; i ++){
                if(arr.keyAt(i) > ind){
                    SourceNewsCursor cursor = arr.valueAt(i);
                    -- cursor.indexInList;
                    arr.put(cursor.indexInList, cursor);
                }
            }
        }

        @Override
        public void dismiss() {
            //TODO: dismiss in SourceNewsList
            if(closed)
                throw new NewsCursorClosedException();
            int ind = indexInList;

            buffer.remove(ind);
            allocated.remove(ind);

            if(metaInfoSource.remove(metaInfo.getId())){
                // if successfully removed from the source,
                // the index needs updating
                shiftHigherInArray(buffer, ind);
                shiftHigherInArray(allocated, ind);
            }
            closed = true;
        }

        @Override
        protected void finalize() throws Throwable {
            super.finalize();
        }

        public void close() {
            deallocate(this);
//            closed = true;
        }

        public boolean isClosed(){
            return closed;
        }

        void setIndexInList(int index){
            this.indexInList = index;
        }
    }

    private NewsMetaInfoListSource metaInfoSource;
    private NewsContentSource contentSource;
    private String keyword;
    private NewsCategoryTag categoryTag;
    private Random random;


    // triple: (index, cursor);
//    private HashMap<Integer, SourceNewsCursor> buffer = new HashMap<Integer, SourceNewsCursor>();
    private final int BUFFER_SIZE = 100;
    private SparseArray<SourceNewsCursor> buffer = new SparseArray<>(),
        allocated = new SparseArray<>();
//    private LinkedList<Integer> queue = new LinkedList<>();
//    private int allocatedCursorCnt;

    private void addToBuffer(SourceNewsCursor cursor){
        int ind = cursor.indexInList;
        SourceNewsCursor res = buffer.get(ind);
        if(res != null)
            return;
        if(buffer.size() >= BUFFER_SIZE)
            buffer.removeAt(random.nextBoolean() ? 0 : (buffer.size() - 1));
        buffer.put(ind, cursor);
    }



    public SourceNewsList(NewsMetaInfoListSource metaInfoSource,
                          NewsContentSource contentSource){
        this(metaInfoSource, contentSource, null, null, -1);
//        this.metaInfoSource = metaInfoSource;
//        this.contentSource = contentSource;
    }

    public SourceNewsList(NewsMetaInfoListSource metaInfoSource,
                          NewsContentSource contentSource,
                          String keyword, NewsCategoryTag newsCategoryTag, int type){

        this.type = type;
        this.metaInfoSource = metaInfoSource;
        this.contentSource = contentSource;
        this.keyword = keyword;
        this.categoryTag = newsCategoryTag;


        random = new Random();

        sourceReversed = metaInfoSource.isReversed();
        previousD = sourceReversed ? 1 : -1;
        nextD = sourceReversed ? -1 : 1;
    }



    /**
     *
     *
     * */
    @Override
    public NewsCursor getCursor(int index) {
        return getCursorAt(index, 1, true);
    }

    private void addToAllocated(SourceNewsCursor cursor){
        int ind = cursor.indexInList;
        allocated.put(ind, cursor);
    }

    private void allocate(SourceNewsCursor cursor){
        int ind = cursor.indexInList;
        buffer.remove(ind);
        addToAllocated(cursor);

        cursor.closed = false;
    }

    private void deallocate(SourceNewsCursor cursor){
        int ind = cursor.indexInList;
        allocated.remove(ind);
        addToBuffer(cursor);

        cursor.closed = true;
    }

    private void bufferCursor(SourceNewsCursor cursor){
//        int ind = cursor.indexInList;
//        SourceNewsCursor res = allocated.get(ind);
//        if(res == null)
        if(cursor.closed)
            addToBuffer(cursor);
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
                    metaInfoSource.getNewsMetaInfoListByPageNo(1, keyword, categoryTag, type);
            for(int i = 0, cur = res.second; i < res.first.length; i ++, cur += nextD) {
                SourceNewsCursor cursor = new SourceNewsCursor(cur, res.first[i], 1);
                if(i == 0)
                    allocate(cursor);
                else
                    bufferCursor(cursor);
            }
            return allocated.get(res.second);
        } catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    private NewsCursor getCursorAt(int index, int oPage, boolean next){
        SourceNewsCursor cursor = buffer.get(index);
        if(cursor != null) {  // already in buffer
            allocate(cursor);
            return cursor;
        }
        cursor = allocated.get(index);
        if(cursor != null)
            return cursor;

        // not in buffer
        // the expected new page
        int nPage = next ? (oPage + 1) : (oPage - 1);
        try{
            Pair<NewsMetaInfo[], Integer> res = null;
            for(int t = 0; t < LOOKUP_LIM; t ++) {
                res =
                        metaInfoSource.getNewsMetaInfoListByPageNo(nPage, keyword, categoryTag, type);
                // compute the index range of the page
                int lo = res.second + nextD * res.first.length;
                int hi = res.second;
                if((index > lo && index <= hi) ||
                        (index < lo && index >= hi)) // if it is on the page
                    break;
                if((index - lo) * nextD >= 0){
                    nPage += ((index - lo) * nextD + 1) / metaInfoSource.getPageSize();
                    if(((index - lo) * nextD + 1) % metaInfoSource.getPageSize() != 0)
                        ++ nPage;
                } else{
                    nPage -= (index - hi) * previousD / metaInfoSource.getPageSize();
                    if((index - hi) * previousD % metaInfoSource.getPageSize() != 0)
                        -- nPage;
                }
            }
            for(int i = 0, cur = res.second; i < res.first.length; i ++, cur += nextD) {
                cursor = new SourceNewsCursor(cur, res.first[i], nPage);
                if(cur == index)
                    allocate(cursor);
                else
                    bufferCursor(cursor);
            }
            return allocated.get(index);
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
    public void refresh() {
        metaInfoSource.refresh();
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
