package com.maoyuchaxue.catfishclubnewsapp.controller;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.maoyuchaxue.catfishclubnewsapp.R;
import com.maoyuchaxue.catfishclubnewsapp.data.DatabaseResourceCache;
import com.maoyuchaxue.catfishclubnewsapp.data.HistoryManager;
import com.maoyuchaxue.catfishclubnewsapp.data.KeywordNewsRecommender;
import com.maoyuchaxue.catfishclubnewsapp.data.NewsContent;
import com.maoyuchaxue.catfishclubnewsapp.data.NewsContentSource;
import com.maoyuchaxue.catfishclubnewsapp.data.NewsCursor;
import com.maoyuchaxue.catfishclubnewsapp.data.NewsList;
import com.maoyuchaxue.catfishclubnewsapp.data.NewsMetaInfo;
import com.maoyuchaxue.catfishclubnewsapp.data.NewsMetaInfoListSource;
import com.maoyuchaxue.catfishclubnewsapp.data.WebNewsContentSource;
import com.maoyuchaxue.catfishclubnewsapp.data.WebNewsMetaInfoListSource;
import com.maoyuchaxue.catfishclubnewsapp.data.WebResourceSource;
import com.maoyuchaxue.catfishclubnewsapp.data.db.CacheDBOpenHelper;
import com.maoyuchaxue.catfishclubnewsapp.fragments.NewsViewFragment;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by catfish on 17/9/10.
 */

public class NewsContentAndImageAdapter extends BaseAdapter
        implements LoaderManager.LoaderCallbacks<List<NewsCursor>>,
        AdapterView.OnItemClickListener {

    private Context context;
    private LoaderManager loaderManager;
    private KeywordNewsRecommender recommender;
    private OnClickNewsListener onClickNewsListener;
    private NewsList newsList = null;

    private List<String> contents;
    private List<URL> urls;
    private List<Integer> indexes;
    private List<Boolean> isImage;

    private List<NewsCursor> cursors;

    public static interface OnClickNewsListener {
        public void onClickNews(NewsCursor cursor);
    }

    public NewsContentAndImageAdapter(Context context, LoaderManager loadManager) {
        this.context = context;
        this.indexes = null;
        this.cursors = null;
        this.loaderManager = loadManager;
    }

    public void setOnClickNewsListener(OnClickNewsListener listener) {
        onClickNewsListener = listener;
    }

    public void resetData(String[] contents, URL[] urls) {
        this.contents = new ArrayList<String>();
        this.urls = new ArrayList<URL>();

        isImage = new ArrayList<Boolean>();
        for (String s : contents) {
            if (!s.isEmpty()) {
                s = s + "</p>";
            }
            this.contents.add(s);
            isImage.add(false);
        }

        for (URL url : urls) {
            this.urls.add(url);
            isImage.add(true);
        }

        Collections.shuffle(isImage);
        int contentIndex = 0;
        int imageIndex = 0;

        this.indexes = new ArrayList<>();
        for (int i = 0; i < isImage.size(); i++) {
            if (isImage.get(i)) {
                indexes.add(imageIndex);
                imageIndex++;
            } else {
                indexes.add(contentIndex);
                contentIndex++;
            }
        }

        notifyDataSetChanged();
    }

    public void setRecommendData(List<NewsCursor> cursors) {
        this.cursors = cursors;
        notifyDataSetChanged();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getCount() {
        int count = 0;
        if (indexes != null) {
            count += indexes.size();
        }
        if (cursors != null) {
            count += cursors.size() + 1;
        }
        return count;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.i("madapter", "get view at: " + position);

        ViewHolder vh = null;

        int maxContentLength = indexes.size();
        if (position >= maxContentLength) {
            if (position == maxContentLength) {
                convertView = LayoutInflater.from(context).inflate(R.layout.recommend_intro_unit_layout, null);
                vh = new RecommendIntroViewHolder(convertView);
                convertView.setTag(vh);

            } else {
                convertView = LayoutInflater.from(context).inflate(R.layout.recommend_unit_layout, null);
                vh = new RecommendationViewHolder(convertView,
                        cursors.get(position - maxContentLength - 1));
                convertView.setTag(vh);
            }

        } else {

            boolean isImage = this.isImage.get(position);
            if (!isImage) {
                convertView = LayoutInflater.from(context).inflate(R.layout.news_view_text_unit, null);
                String content = contents.get(indexes.get(position));
                vh = new TextViewHolder(convertView, content);
                convertView.setTag(vh);

            } else {
                convertView = LayoutInflater.from(context).inflate(R.layout.news_view_image_unit, null);
                URL url = urls.get(indexes.get(position));
                ImageViewHolder ivh = new ImageViewHolder(convertView, context, url);
                vh = ivh;
                convertView.setTag(vh);

                int loaderID = NewsViewFragment.NEWS_RESOURCE_LOADER_ID + indexes.get(position);
                if (loaderManager.getLoader(loaderID) != null) {
                    loaderManager.destroyLoader(loaderID);
                }
                loaderManager.initLoader(loaderID, null, ivh).forceLoad();

            }
        }

        vh.draw();
        return convertView;
    }

    private abstract static class ViewHolder {
        public final static int TEXT_UNIT = 0;
        public final static int IMAGE_UNIT = 1;
        public View view;
        ViewHolder(View view) {
            this.view = view;
        }

        abstract void draw();
    }

    private static class TextViewHolder extends ViewHolder {
        Spanned content;
        TextViewHolder(View view, String content) {
            super(view);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                this.content = Html.fromHtml(content, Html.FROM_HTML_MODE_LEGACY);
            } else {
                this.content = Html.fromHtml(content);
            }
        }

        void draw() {
            TextView textView = (TextView) view.findViewById(R.id.news_view_text_unit_content);
            textView.setMovementMethod(LinkMovementMethod.getInstance());
            textView.setText(content);
        }
    }


    private static class RecommendIntroViewHolder extends ViewHolder {
        RecommendIntroViewHolder(View view) {
            super(view);
        }
        void draw() {}
    }

    private static class RecommendationViewHolder extends ViewHolder {
        private NewsCursor info;
        RecommendationViewHolder(View view, NewsCursor newsCursor) {
            super(view);
            this.info = newsCursor;
        }

        void draw() {
            TextView titleView = (TextView) view.findViewById(R.id.recommend_title);
            TextView introView = (TextView) view.findViewById(R.id.recommend_intro);
            TextView authorView = (TextView) view.findViewById(R.id.recommend_author);
            titleView.setText(info.getNewsMetaInfo().getTitle());
            introView.setText(info.getNewsMetaInfo().getIntro());
            authorView.setText(info.getNewsMetaInfo().getAuthor());
        }
    }

    private static class ImageViewHolder extends ViewHolder
            implements LoaderManager.LoaderCallbacks<Bitmap> {

        private ImageView imageView;
        private Context context;
        private URL url;

        ImageViewHolder(View view, Context context, URL url) {
            super(view);
            this.imageView = null;
            this.context = context;
            this.url = url;
        }

        void draw() {
            if (imageView == null) {
                imageView = (ImageView) view.findViewById(R.id.news_view_image_unit_image);
            }
        }

        @Override
        public Loader<Bitmap> onCreateLoader(int id, Bundle args) {
            imageView = (ImageView) view.findViewById(R.id.news_view_image_unit_image);
            return new ResourceLoader(context, url,
                    new DatabaseResourceCache(CacheDBOpenHelper.getInstance(context.getApplicationContext()),
                            new WebResourceSource(200, 200, 1000, 1000)), false);
        }

        @Override
        public void onLoadFinished(Loader<Bitmap> loader, Bitmap data) {
            if (data == null || data.getByteCount() == 0 || data.getHeight() == 0) {
                imageView.setImageResource(R.mipmap.ic_placeholder);
            } else {
                imageView.setImageBitmap(data);
            }
            Log.i("madapter", "load finished " + url.toString());
        }

        @Override
        public void onLoaderReset(Loader<Bitmap> loader) {}
    }

    public void startRecommendLoading(NewsContent content, int limit) {
        Boolean isOfflineMode = PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean("offline_mode", false);

        if (isOfflineMode) {
//            if offline, no recommendation is needed
            return;
        }

        Log.i("recommend", "start loading");
        NewsMetaInfoListSource listSource = new WebNewsMetaInfoListSource(
                "http://166.111.68.66:2042/news/action/query/search");

        NewsContentSource contentSource = HistoryManager.getInstance(CacheDBOpenHelper.
                getInstance(context.getApplicationContext())).getNewsContentSource(
                new WebNewsContentSource("http://166.111.68.66:2042/news/action/query/detail"));

        recommender = new KeywordNewsRecommender(listSource, contentSource);
        newsList = recommender.recommend(content, limit);

        loaderManager.initLoader(NewsViewFragment.NEWS_RECOMMEND_LOADER_ID, null, this)
                .forceLoad();
    }

    @Override
    public Loader<List<NewsCursor> > onCreateLoader(int id, Bundle args) {
        return new NewsMetainfoLoader(context, 5, null, newsList);
    }

    @Override
    public void onLoadFinished(Loader<List<NewsCursor>> loader, List<NewsCursor> data) {
        Log.i("recommend", "load finished, data size: " + data.size());
        Log.i("recommend", data.get(0).getNewsMetaInfo().getTitle());
        setRecommendData(data);
    }

    @Override
    public void onLoaderReset(Loader<List<NewsCursor>> loader) {}

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        ViewHolder vh = (ViewHolder) view.getTag();
        if (vh instanceof RecommendationViewHolder) {
            RecommendationViewHolder rvh = (RecommendationViewHolder) vh;
            if (onClickNewsListener != null) {
                onClickNewsListener.onClickNews(rvh.info);
            }
        }
    }
}
