package com.maoyuchaxue.catfishclubnewsapp.data.rss;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.constraint.solver.Cache;

import com.maoyuchaxue.catfishclubnewsapp.data.DatabaseNewsContentCache;
import com.maoyuchaxue.catfishclubnewsapp.data.DatabaseNewsMetaInfoListSource;
import com.maoyuchaxue.catfishclubnewsapp.data.NewsCategoryTag;
import com.maoyuchaxue.catfishclubnewsapp.data.NewsContent;
import com.maoyuchaxue.catfishclubnewsapp.data.NewsContentSource;
import com.maoyuchaxue.catfishclubnewsapp.data.NewsMetaInfo;
import com.maoyuchaxue.catfishclubnewsapp.data.NewsMetaInfoListSource;
import com.maoyuchaxue.catfishclubnewsapp.data.db.CacheDBOpenHelper;
import com.maoyuchaxue.catfishclubnewsapp.data.exceptions.NewsSourceException;
import com.maoyuchaxue.catfishclubnewsapp.data.util.Pair;

import org.xml.sax.SAXException;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;

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

    private SAXParser xmlParser;
    private ArrayList<Pair<URL, ChannelMetaInfo>> feeds;
    private ArrayList<NewsMetaInfo> newsMetaInfos;
    private HashSet<String> idSet;

    private static RSSManager instance;

    public synchronized static RSSManager getInstance(CacheDBOpenHelper openHelper) throws
        SAXException, ParserConfigurationException{
        if(instance == null)
            instance = new RSSManager(openHelper);
        return instance;
    }
//
//    private class RSSNewsContentSource implements NewsContentSource{
//        @Override
//        public void close() throws NewsSourceException {
//        }
//
//        @Override
//        public NewsContent getNewsContent(NewsMetaInfo id) throws NewsSourceException {
//            SQLiteDatabase db = openHelper.getReadableDatabase();
////            db.query(false, )
//            return null;
//        }
//
//    }

    private class RSSNewsMetaInfoListSource implements NewsMetaInfoListSource{
        private static final int PAGE_SIZE = 20;
        @Override
        public Pair<NewsMetaInfo[], Integer> getNewsMetaInfoListByPageNo(int pageNo, String keyword, NewsCategoryTag category) throws NewsSourceException {
            return getNewsMetaInfoListByIndex(newsMetaInfos.size() - PAGE_SIZE * (pageNo - 1) - 1,
                    keyword,
                    category);
        }

        @Override
        public Pair<NewsMetaInfo[], Integer> getNewsMetaInfoListByIndex(int index, String keyword, NewsCategoryTag category) throws NewsSourceException {
            ArrayList<NewsMetaInfo> res = new ArrayList<>();

            for(int i = 0, p = index; i < PAGE_SIZE && p >= 0; i ++, p --)
                res.add(newsMetaInfos.get(i));
            return new Pair<>(res.toArray(new NewsMetaInfo[0]),
                    index);
        }

        @Override
        public boolean isReversed() {
            return true;
        }

        @Override
        public int getPageSize() {
            return PAGE_SIZE;
        }

        @Override
        public int getCapacity() {
            return 0;
        }

        @Override
        public void refresh() {
            try {
                RSSManager.this.synchronise();
            } catch(Exception e){
                e.printStackTrace();
            }
        }

        @Override
        public boolean remove(String id) {
            return false;
        }

        @Override
        public void close() throws NewsSourceException {

        }
    }

    private RSSManager(CacheDBOpenHelper openHelper) throws SAXException, ParserConfigurationException{
        this.openHelper = openHelper;
        feeds = new ArrayList<>();
        newsMetaInfos = new ArrayList<>();
        idSet = new HashSet<>();

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

//        SQLiteDatabase db = openHelper.getWritableDatabase();

        for(NewsMetaInfo metaInfo : handler.getNewsMetaInfoList()){
            if(metaInfo.getId() == null)
                metaInfo.setId(metaInfo.getUrl().toString());
            metaInfo.setType(1);

            String id = metaInfo.getId();
            if(idSet.contains(id))
                continue;
            idSet.add(id);
            newsMetaInfos.add(metaInfo);
        }

//        for(NewsMetaInfo metaInfo : handler.getNewsMetaInfoList()){
//            ContentValues change = new ContentValues();
//            change.put(CacheDBOpenHelper.FIELD_INTRO, metaInfo.getIntro());
//            change.put(CacheDBOpenHelper.FIELD_AUTHOR, metaInfo.getAuthor());
//            change.put(CacheDBOpenHelper.FIELD_TITLE, metaInfo.getTitle());
//            try {
//                change.put(CacheDBOpenHelper.FIELD_CATEGORY_TAG, metaInfo.getCategoryTag().getIndex());
//            } catch(NullPointerException e){
//                e.printStackTrace();
//            }
//            change.put(CacheDBOpenHelper.FIELD_URL, metaInfo.getUrl().toString());
//
//            StringBuilder pictureStr = new StringBuilder();
//
//            boolean first = true;
//            for(URL url : metaInfo.getPictures()) {
//                if (first) {
//                    first = false;
//                    pictureStr.append(url.toString());
//                } else {
//                    pictureStr.append(";" + url.toString());
//                }
//            }
//
//            change.put(CacheDBOpenHelper.FIELD_PICTURES, pictureStr.toString());
//
//            change.put(CacheDBOpenHelper.FIELD_VIDEO, metaInfo.getVideo() == null ? "" : metaInfo.getVideo().toString());
//            change.put(CacheDBOpenHelper.FIELD_LANG, metaInfo.getLang());
//            change.put(CacheDBOpenHelper.FIELD_SRC, metaInfo.getSrcSite());
//
//            change.put(CacheDBOpenHelper.FIELD_ID, metaInfo.getId());
//
//            long res = db.insert(CacheDBOpenHelper.BOOKMARK_TABLE_NAME, null, change);
//        }

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

    public void addRSSFeed(URL url){
//        ChannelMetaInfo channelMetaInfo = synchronise(url);
//        feeds.add(new Pair<>(url, channelMetaInfo));
        feeds.add(new Pair<URL, ChannelMetaInfo>(url, null));

        SQLiteDatabase db = openHelper.getWritableDatabase();
        ContentValues change = new ContentValues();
        change.put(CacheDBOpenHelper.FIELD_URL, url.toString());
//        change.put(CacheDBOpenHelper.FIELD_TITLE, channelMetaInfo.getTitle());
//        change.put(CacheDBOpenHelper.FIELD_LINK, channelMetaInfo.getLink());
//        change.put(CacheDBOpenHelper.FIELD_DESC, channelMetaInfo.getDescription());

        db.insert(CacheDBOpenHelper.RSS_TABLE_NAME, null, change);
    }


    public NewsMetaInfoListSource getNewsMetaInfoListSource(){
        return new RSSNewsMetaInfoListSource();
    }
//    public NewsContentSource getNewsContentSource() {
//        return new RSSNewsContentSource();
//    }
}

