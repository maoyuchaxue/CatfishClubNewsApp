package com.maoyuchaxue.catfishclubnewsapp.data;


import android.graphics.Bitmap;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by YU_Jason on 2017/9/7.
 */

public class WebResourceSource extends ResourceSource {
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

        return byteArray.toByteArray();
    }
}
