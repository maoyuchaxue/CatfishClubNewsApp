package com.maoyuchaxue.catfishclubnewsapp.data.rss;

import android.database.sqlite.SQLiteDatabase;

import com.maoyuchaxue.catfishclubnewsapp.data.DatabaseNewsContentCache;
import com.maoyuchaxue.catfishclubnewsapp.data.NewsContent;
import com.maoyuchaxue.catfishclubnewsapp.data.NewsContentSource;
import com.maoyuchaxue.catfishclubnewsapp.data.NewsMetaInfoListSource;
import com.maoyuchaxue.catfishclubnewsapp.data.db.CacheDBOpenHelper;
import com.maoyuchaxue.catfishclubnewsapp.data.exceptions.NewsSourceException;

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

    protected void synchronise(URL rssUrl) throws IOException{
        HttpURLConnection con = (HttpURLConnection)rssUrl.openConnection();
        con.setRequestMethod("GET");
        con.connect();


        // TODO: 2017/9/11 Parsing the rss document
//        xmlParser.parse(con.getInputStream(), n);

        con.disconnect();
    }

    /**
     * Synchronises the news list.
     *
     * */
    public void synchronise() throws IOException{
        for(URL rssUrl : rssUrls)
            synchronise(rssUrl);
    }


    public NewsMetaInfoListSource getNewsMetaInfoListSource(){
        return null;
    }
    public NewsContentSource getNewsContentSource() {
        return new RSSNewsContentSource();
    }
}

