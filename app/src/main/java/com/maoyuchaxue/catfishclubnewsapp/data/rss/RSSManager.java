package com.maoyuchaxue.catfishclubnewsapp.data.rss;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.constraint.solver.Cache;

import com.maoyuchaxue.catfishclubnewsapp.data.DatabaseNewsContentCache;
import com.maoyuchaxue.catfishclubnewsapp.data.NewsContent;
import com.maoyuchaxue.catfishclubnewsapp.data.NewsContentSource;
import com.maoyuchaxue.catfishclubnewsapp.data.NewsMetaInfoListSource;
import com.maoyuchaxue.catfishclubnewsapp.data.db.CacheDBOpenHelper;
import com.maoyuchaxue.catfishclubnewsapp.data.exceptions.NewsSourceException;
import com.maoyuchaxue.catfishclubnewsapp.data.util.Pair;

import org.xml.sax.SAXException;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * Created by YU_Jason on 2017/9/11.
 */

public class RSSManager {
    private CacheDBOpenHelper openHelper;
    private static final WebPageNewsContentSource FRONT_CONTENT_SOURCE =
            new WebPageNewsContentSource();
    private static ArrayList<URL> rssUrls;

    private SAXParser xmlParser;
    private ArrayList<Pair<URL, ChannelMetaInfo>> feeds;

    private class RSSNewsContentSource implements NewsContentSource{
        @Override
        public void close() throws NewsSourceException {
        }

        @Override
        public NewsContent getNewsContent(String id) throws NewsSourceException {
            SQLiteDatabase db = openHelper.getReadableDatabase();
//            db.query(false, )
            return null;
        }

    }

    public RSSManager(CacheDBOpenHelper openHelper) throws SAXException, ParserConfigurationException{
        this.openHelper = openHelper;
        xmlParser = SAXParserFactory.newInstance().newSAXParser();
    }

    protected ChannelMetaInfo synchronise(URL rssUrl) throws IOException, SAXException{
        HttpURLConnection con = (HttpURLConnection)rssUrl.openConnection();
        con.setRequestMethod("GET");
        con.connect();


        RSSFeedHandler handler = new RSSFeedHandler();
        xmlParser.parse(con.getInputStream(), handler);
//        xmlParser.parse(con.getInputStream(), n);
        // TODO: 2017/9/12 Merging the new list and the old list

        con.disconnect();

        return handler.getChannelMetaInfo();
    }

    /**
     * Synchronises the news list.
     *
     * */
    public void synchronise() throws IOException, SAXException{
        for(Pair<URL, ChannelMetaInfo> feed : feeds)
            feed.second = synchronise(feed.first);
    }

    private void loadRSSFeeds() throws IOException{
        SQLiteDatabase db = openHelper.getReadableDatabase();
        Cursor cursor = db.query(false, CacheDBOpenHelper.RSS_TABLE_NAME, new String[]{
                CacheDBOpenHelper.FIELD_URL,
                CacheDBOpenHelper.FIELD_TITLE,
                CacheDBOpenHelper.FIELD_LINK,
                CacheDBOpenHelper.FIELD_DESC
                },
                null, null, null, null, "rowid asc", null);
        if(cursor.moveToFirst()){
            do{
                URL url = new URL(cursor.getString(0));
                ChannelMetaInfo metaInfo = new ChannelMetaInfo();
                metaInfo.setTitle(cursor.getString(1));
                metaInfo.setLink(cursor.getString(2));
                metaInfo.setDescription(cursor.getString(3));

                feeds.add(new Pair<>(url, metaInfo));
            } while(cursor.moveToNext());
        }
        cursor.close();
    }

    public void addRSSFeed(URL url) throws IOException, SAXException{
        ChannelMetaInfo channelMetaInfo = synchronise(url);
        feeds.add(new Pair<>(url, channelMetaInfo));

        SQLiteDatabase db = openHelper.getWritableDatabase();
        // TODO: 2017/9/12 Write rss feed to database
        ContentValues change = new ContentValues();
        change.put(CacheDBOpenHelper.FIELD_URL, url.toString());
        change.put(CacheDBOpenHelper.FIELD_TITLE, channelMetaInfo.getTitle());
        change.put(CacheDBOpenHelper.FIELD_LINK, channelMetaInfo.getLink());
        change.put(CacheDBOpenHelper.FIELD_DESC, channelMetaInfo.getDescription());

        db.insert(CacheDBOpenHelper.RSS_TABLE_NAME, null, change);
    }


    public NewsMetaInfoListSource getNewsMetaInfoListSource(){
        return null;
    }
    public NewsContentSource getNewsContentSource() {
        return new RSSNewsContentSource();
    }
}

