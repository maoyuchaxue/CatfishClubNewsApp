package com.maoyuchaxue.catfishclubnewsapp.controller;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.content.AsyncTaskLoader;

import com.maoyuchaxue.catfishclubnewsapp.data.ResourceSource;

import java.io.IOException;
import java.net.URL;

/**
 * Created by catfish on 17/9/8.
 */

public class ResourceLoader extends AsyncTaskLoader<Bitmap> {
    private ResourceSource resourceSource;
    private URL url;
    private boolean thumbnail;

    public ResourceLoader(Context context, URL url, ResourceSource resourceSource, boolean thumbnail) {
        super(context);
        this.url = url;
        this.resourceSource = resourceSource;
        this.thumbnail = thumbnail;
    }

    @Override
    public Bitmap loadInBackground() {
        Bitmap resource = null;
        try {
            if (url != null) {
                resource = thumbnail ?
                        resourceSource.getAsThumbnail(url) :
                        resourceSource.getAsBitmap(url);
            }
        } catch (IOException e) {
        }
        return resource;
    }

}

