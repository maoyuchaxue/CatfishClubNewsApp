package com.maoyuchaxue.catfishclubnewsapp.data;

/**
 * @deprecated Obsolete for preference for {@link SourceNewsList}.
 * Created by YU_Jason on 2017/9/5.
 */

public class CachedSourceNewsList implements NewsList {


    private class CachedWebNewsCursor implements NewsCursor{

        @Override
        public NewsMetaInfo getNewsMetaInfo() {
            return null;
        }

        @Override
        public NewsContent getNewsContent() {
            return null;
        }

        @Override
        public NewsCursor next() {
            return null;
        }

        @Override
        public NewsCursor previous() {
            return null;
        }

//        @Override
//        public boolean moveToNext() {
//            return false;
//        }
//
//        @Override
//        public boolean moveToPrevious() {
//            return false;
//        }
//
//        @Override
//        public boolean existing() {
//            return false;
//        }

        @Override
        public int getIndex() {
            return 0;
        }

        @Override
        public int getIndexInList() {
            return 0;
        }

        @Override
        public void close() {

        }
    }

    @Override
    public NewsCursor getCursor(int index) {
        return null;
    }

    @Override
    public NewsCursor getHeadCursor() {
        return null;
    }

    @Override
    public int getLength() {
        return 0;
    }

    @Override
    public void close() {

    }


}
