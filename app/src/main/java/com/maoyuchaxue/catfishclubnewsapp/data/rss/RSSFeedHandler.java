package com.maoyuchaxue.catfishclubnewsapp.data.rss;

import com.maoyuchaxue.catfishclubnewsapp.data.NewsMetaInfo;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Created by YU_Jason on 2017/9/11.
 */

class RSSFeedHandler extends DefaultHandler {
    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes){

    }

    @Override
    public void endElement(String uri, String localName, String qName){

    }

    @Override
    public void characters(char[] ch, int start, int length) {

    }

    public NewsMetaInfo[] getNewsMetaInfoList(){
        return null;
    }

    public ChannelMetaInfo getChannelMetaInfo(){
        return null;
    }
}
