package com.maoyuchaxue.catfishclubnewsapp.data.rss;

import android.util.Log;

import com.maoyuchaxue.catfishclubnewsapp.data.NewsMetaInfo;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import java.net.URL;
import java.util.ArrayList;

/**
 * Created by YU_Jason on 2017/9/11.
 */

class RSSFeedHandler extends DefaultHandler {
//    private boolean inElement, inTitle, inLink, inDescription;
//    private boolean inAuthor, inGUID, inCategory, inSource;
    private boolean inItem;
    private String currentElement;
    private NewsMetaInfo currentNewsMetaInfo;
    private ArrayList<NewsMetaInfo> newsMetaInfos;
    private StringBuilder content;

    private ChannelMetaInfo channelMetaInfo;

    public RSSFeedHandler(){
        newsMetaInfos = new ArrayList<>();
        channelMetaInfo = new ChannelMetaInfo();
        inItem = false;
        currentElement = "";
        content = new StringBuilder();
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes){
        if(localName.equalsIgnoreCase("item")) {
            currentNewsMetaInfo = new NewsMetaInfo();
            inItem = true;
        }
        content = new StringBuilder();
        currentElement = localName;

    }

    @Override
    public void endElement(String uri, String localName, String qName){
        String s = content.toString();
        if(inItem){

            if(currentElement.equalsIgnoreCase("title"))
                currentNewsMetaInfo.setTitle(s);
            else if(currentElement.equalsIgnoreCase("link")) {
                try {
                    currentNewsMetaInfo.setUrl(new URL(s));
                } catch(Exception e){
                    e.printStackTrace();
                }
            } else if(currentElement.equalsIgnoreCase("author"))
                currentNewsMetaInfo.setAuthor(s);
            else if(currentElement.equalsIgnoreCase("description"))
                currentNewsMetaInfo.setIntro(s);
            else if(currentElement.equalsIgnoreCase("source"))
                currentNewsMetaInfo.setSrcSite(s);
        } else{
            if(currentElement.equalsIgnoreCase("title"))
                channelMetaInfo.setTitle(s);
            else if(currentElement.equalsIgnoreCase("link"))
                channelMetaInfo.setLink(s);
            else if(currentElement.equalsIgnoreCase("description"))
                channelMetaInfo.setDescription(s);
            else if(currentElement.equalsIgnoreCase("language"))
                channelMetaInfo.setLanguage(s);
        }
        if(localName.equalsIgnoreCase("item")) {
            inItem = false;
            newsMetaInfos.add(currentNewsMetaInfo);
        }
        currentElement = "";
    }

    @Override
    public void characters(char[] ch, int start, int length) {
        String s = new String(ch, start, length);
        Log.i("Handler", s);
        content.append(s);

    }

    public NewsMetaInfo[] getNewsMetaInfoList(){
        return newsMetaInfos.toArray(new NewsMetaInfo[0]);
    }

    public ChannelMetaInfo getChannelMetaInfo(){
        return channelMetaInfo;
    }
}
