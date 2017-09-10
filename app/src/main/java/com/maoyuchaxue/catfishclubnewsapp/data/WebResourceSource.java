package com.maoyuchaxue.catfishclubnewsapp.data;


import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by YU_Jason on 2017/9/7.
 */

public class WebResourceSource extends ResourceSource {
    private int tnHSize, tnWSize, bmHSize, bmWSize;
    public WebResourceSource(int tnHSize, int tnWSize, int bmHSize, int bmWSize){
        this.tnHSize = tnHSize;
        this.tnWSize = tnWSize;
        this.bmHSize = bmHSize;
        this.bmWSize = bmWSize;
    }

    @Override
    public byte[] getAsBlob(URL url) throws IOException{
        HttpURLConnection con = (HttpURLConnection)url.openConnection();
        con.setRequestMethod("GET");

        BufferedInputStream inputStream = new BufferedInputStream(con.getInputStream());
        ByteArrayOutputStream byteArray = new ByteArrayOutputStream();

        while(true) {
            int c = inputStream.read();
            if(c == -1)
                break;
            byteArray.write(c);
        }
        inputStream.close();
        byteArray.close();

        con.connect();
        con.disconnect();

        Log.i("catclub", url.toString() + byteArray.size());
        return byteArray.toByteArray();
    }

    private Bitmap scaleToFit(Bitmap in, int hsize, int wsize){
        if(in == null)
            return null;
        Matrix matrix = new Matrix();
        float s = Math.min((float)hsize / in.getHeight(),
                (float)wsize / in.getWidth());
        matrix.setScale(s, s);
        return Bitmap.createBitmap(in, 0, 0, in.getWidth(), in.getHeight(), matrix, false);
    }

    @Override
    protected Bitmap filterThumbnail(Bitmap in) {
        return scaleToFit(in, tnHSize, tnWSize);
    }

    @Override
    protected Bitmap filterBitmap(Bitmap in) {
        return scaleToFit(in, bmHSize, bmWSize);
    }
}
