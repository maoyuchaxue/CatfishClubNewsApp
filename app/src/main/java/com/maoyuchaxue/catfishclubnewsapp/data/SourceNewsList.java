package com.maoyuchaxue.catfishclubnewsapp.data;


import android.util.SparseArray;

import com.maoyuchaxue.catfishclubnewsapp.data.util.Pair;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Queue;
import java.util.Random;

import javax.xml.transform.Source;

/**
 * Created by YU_Jason on 2017/9/5.
 */

public class SourceNewsList implements NewsList {
    private class SourceNewsCursor implements NewsCursor{
        private NewsMetaInfo metaInfo;
        // the index in the real list (stable)
        private int indexInList, oPage;
        private boolean closed;

        public SourceNewsCursor(int indexInList, NewsMetaInfo metaInfo, int oPage){
            this.indexInList = indexInList;
            this.metaInfo = metaInfo;
            this.oPage = oPage;

            closed = false;
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
                return contentSource.getNewsContent(metaInfo.getId());
            } catch(Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public NewsCursor next() {
            if(closed)
                throw new NewsCursorClosedException();
            return getCursorAt(indexInList - 1, oPage, true);
        }

        @Override
        public NewsCursor previous() {
            if(closed)
                throw new NewsCursorClosedException();
            return getCursorAt(indexInList + 1, oPage, false);
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
            close();
        }

        public void close() {
            closed = true;
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
        int ind = cursor.getIndexInList();
        SourceNewsCursor res = buffer.get(ind);
        if(res != null)
            return;
        if(buffer.size() >= BUFFER_SIZE)
            buffer.removeAt(random.nextBoolean() ? 0 : (buffer.size() - 1));
        buffer.put(ind, cursor);
    }



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

        random = new Random();
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
        int ind = cursor.getIndexInList();
        allocated.put(ind, cursor);
    }

    private void allocate(SourceNewsCursor cursor){
        int ind = cursor.getIndexInList();
        buffer.remove(ind);
        addToAllocated(cursor);
    }

    private void deallocate(SourceNewsCursor cursor){
        int ind = cursor.getIndexInList();
        allocated.remove(ind);
        addToBuffer(cursor);
    }

    private void bufferCursor(SourceNewsCursor cursor){
        int ind = cursor.getIndexInList();
        SourceNewsCursor res = allocated.get(ind);
        if(res == null)
            addToBuffer(res);
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
            for(int i = 0; i < res.first.length; i ++) {
                SourceNewsCursor cursor = new SourceNewsCursor(res.second - i, res.first[i], 1);
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
        if(cursor != null)  // already in buffer
        {
            cursor = allocated.get(index);
            if(cursor != null)
                return cursor;
        }

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
            for(int i = 0; i < res.first.length; i ++) {
                cursor = new SourceNewsCursor(res.second - i, res.first[i], nPage);
                if(res.second - i == index)
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
