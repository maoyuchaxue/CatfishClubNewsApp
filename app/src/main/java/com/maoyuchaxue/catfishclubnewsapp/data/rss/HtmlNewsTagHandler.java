package com.maoyuchaxue.catfishclubnewsapp.data.rss;

import android.text.Editable;
import android.text.Html;

import org.xml.sax.XMLReader;

/**
 * Created by YU_Jason on 2017/9/11.
 */

class HtmlNewsTagHandler implements Html.TagHandler {

    private boolean inParam;
    private StringBuilder curParam;


    @Override
    public void handleTag(boolean opening, String tag, Editable output, XMLReader xmlReader) {
        if(tag.equals("p")){
            if(opening){
                inParam = true;
            } else{
                inParam = false;
                //// TODO: 2017/9/11  
            }
        }
    }
}
