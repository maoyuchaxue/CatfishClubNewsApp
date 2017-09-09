package com.maoyuchaxue.catfishclubnewsapp.controller;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.maoyuchaxue.catfishclubnewsapp.data.ResourceSource;

import java.io.IOException;
import java.net.URL;

/**
 * Created by catfish on 17/9/8.
 */

public class ResourceLoader extends AsyncTaskLoader<Bitmap> {
    private ResourceSource resourceSource;
    private URL url;

    public ResourceLoader(Context context, URL url, ResourceSource resourceSource) {
        super(context);
        this.url = url;
        this.resourceSource = resourceSource;
    }

    @Override
    public Bitmap loadInBackground() {
        Bitmap resource = null;
        try {
            if (url != null) {
                resource = resourceSource.getAsBitmap(url);
            }
        } catch (IOException e) {
        }
        return resource;
    }

}

