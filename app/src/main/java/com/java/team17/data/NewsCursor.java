package com.java.team17.data;

/**
 * A cursor that contains news, which could be move around.
 * This is the interface for all implementations of cursors.
 * Created by YU_Jason on 2017/9/5.
 */

/**
 * NewsCursor is not responsible for loading images. Only urls of the images
 * are present in a NewsCursor.<br>
 * Loading images should be delegated to ResourceSource.
 * */
public interface NewsCursor {

    NewsMetaInfo getNewsMetaInfo();
    NewsContent getNewsContent();
//    boolean moveToNext();
//    boolean moveToPrevious();
    //boolean existing();

    NewsCursor next();
    NewsCursor previous();

    int getIndex();
    int getIndexInList();

    void dismiss();

    boolean isClosed();
    void close();

    class NewsCursorClosedException extends RuntimeException{
        //TODO: something more in this exception
    }
}
