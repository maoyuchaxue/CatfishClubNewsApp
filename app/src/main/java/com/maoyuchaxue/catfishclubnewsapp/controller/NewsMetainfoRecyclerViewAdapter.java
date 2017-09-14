package com.maoyuchaxue.catfishclubnewsapp.controller;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.maoyuchaxue.catfishclubnewsapp.R;
import com.maoyuchaxue.catfishclubnewsapp.data.DatabaseResourceCache;
import com.maoyuchaxue.catfishclubnewsapp.data.HistoryManager;
import com.maoyuchaxue.catfishclubnewsapp.data.NewsCursor;
import com.maoyuchaxue.catfishclubnewsapp.data.NewsMetaInfo;
import com.maoyuchaxue.catfishclubnewsapp.data.WebResourceSource;
import com.maoyuchaxue.catfishclubnewsapp.data.db.CacheDBOpenHelper;
import com.maoyuchaxue.catfishclubnewsapp.fragments.NewsListFragment;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by catfish on 17/9/6.
 */



public class NewsMetainfoRecyclerViewAdapter
        extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements View.OnClickListener {

    private Context context;
    private LoaderManager loaderManager;
    private ArrayList<NewsCursor> cursors;
    private HashMap<String, Integer> IDToPosition;

    public NewsMetainfoRecyclerViewAdapter(Context context, LoaderManager loaderManager) {
        this.context = context;
        this.loaderManager = loaderManager;
        this.IDToPosition = new HashMap<String, Integer>();
        cursors = new ArrayList<NewsCursor>();
    }

    private static final int PICS_VIEW = 0;
    private static final int TEXT_ONLY_VIEW = 1;
    private static final int FOOTER_VIEW = 2;

    public static interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, NewsCursor cursor);
    }

    private OnRecyclerViewItemClickListener onRecyclerViewItemClickListener = null;

    public void setOnRecyclerViewItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.onRecyclerViewItemClickListener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = null;
        RecyclerView.ViewHolder viewHolder = null;
        switch (viewType) {
            case PICS_VIEW:
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.news_info_unit_with_pics_layout, null);
                viewHolder = new PicsViewHolder(view, context);
                break;
            case TEXT_ONLY_VIEW:
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.news_into_unit_layout, null);
                viewHolder = new TextViewHolder(view);
                break;
            case FOOTER_VIEW:
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.footer_unit_layout, null);
                viewHolder = new FooterViewHolder(view);
                break;
        }
        if (view != null) {
            view.setOnClickListener(this);
            view.setLayoutParams(new RecyclerView.LayoutParams(
                    RecyclerView.LayoutParams.MATCH_PARENT,
                    RecyclerView.LayoutParams.WRAP_CONTENT
            ));
        }

        return viewHolder;
    }

    @Override
    public int getItemViewType(int position) {
        if (position >= cursors.size()) {
            return FOOTER_VIEW;
        }

        URL[] urls = cursors.get(position).getNewsMetaInfo().getPictures();

        Boolean isTextOnly = PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean("text_only_mode", false);
        if (urls == null || urls.length == 0 || isTextOnly) {
            return TEXT_ONLY_VIEW;
        } else {
            return PICS_VIEW;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        Log.i("madapter", "bind on " + position);
        if (viewHolder instanceof PicsViewHolder) {
            PicsViewHolder picsViewHolder= (PicsViewHolder) viewHolder;
            View view = picsViewHolder.view;
            NewsCursor cursor = cursors.get(position);
            NewsMetaInfo info = cursor.getNewsMetaInfo();

            TextView titleView = (TextView) view.findViewById(R.id.news_unit_pics_title);
            TextView introView = (TextView) view.findViewById(R.id.news_unit_pics_intro);
            TextView sourceView = (TextView) view.findViewById(R.id.news_unit_pics_source);

            titleView.setText(info.getTitle());

            String intro = info.getIntro();
            Spanned spannedContent;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                spannedContent = Html.fromHtml(intro, Html.FROM_HTML_MODE_LEGACY);
            } else {
                spannedContent = Html.fromHtml(intro);
            }
            introView.setText(spannedContent);

            String src = info.getSrcSite();
            String author = info.getAuthor();

            if (src.isEmpty()) {
                src = "匿名来源";
            }

            if (author.isEmpty()) {
                author = "匿名作者";
            }

            sourceView.setText(src+ "  " + author);

            String id = info.getId();
            int color;
            if (HistoryManager.getInstance(CacheDBOpenHelper.getInstance(context.getApplicationContext())).isInHistory(id)) {
                Log.i("madapter", "has read " + id);
                color = ContextCompat.getColor(context, R.color.colorHasReadText);
            } else {
                color = ContextCompat.getColor(context, R.color.colorBasicText);
            }
            titleView.setTextColor(color);
            introView.setTextColor(color);
            sourceView.setTextColor(color);

            picsViewHolder.setSummaryPicURL(info.getPictures()[0]);

            viewHolder.itemView.setTag(cursors.get(position));

            int loaderID = NewsListFragment.IMAGE_LOADER_ID + position;
            Loader loader = loaderManager.getLoader(loaderID);
            if (loader != null && loader.isReset()) {
                loaderManager.restartLoader(loaderID, null, picsViewHolder).forceLoad();
            } else
                loaderManager.initLoader(loaderID, null, picsViewHolder).forceLoad();


            viewHolder.itemView.setTag(cursors.get(position));

        } else if (viewHolder instanceof TextViewHolder) {
            TextViewHolder textViewHolder = (TextViewHolder) viewHolder;
            View view = textViewHolder.view;
            NewsCursor cursor = cursors.get(position);
            NewsMetaInfo info = cursor.getNewsMetaInfo();

            TextView titleView = (TextView) view.findViewById(R.id.news_unit_title);
            TextView introView = (TextView) view.findViewById(R.id.news_unit_intro);
            TextView sourceView = (TextView) view.findViewById(R.id.news_unit_source);
            titleView.setText(info.getTitle());

            String intro = info.getIntro();
            String spannedContent = intro.replaceAll("<[^<>]*>", "");
            introView.setText(spannedContent);

            String src = info.getSrcSite();
            String author = info.getAuthor();

            if (src.isEmpty()) {
                src = "匿名来源";
            }

            if (author.isEmpty()) {
                author = "匿名作者";
            }

            sourceView.setText(src+ "  " + author);

            String id = info.getId();
            int color;
            if (HistoryManager.getInstance(CacheDBOpenHelper.getInstance(context.getApplicationContext())).isInHistory(id)) {
                Log.i("madapter", "has read " + id);
                color = ContextCompat.getColor(context, R.color.colorHasReadText);
            } else {
                color = ContextCompat.getColor(context, R.color.colorBasicText);
            }
            titleView.setTextColor(color);
            introView.setTextColor(color);
            sourceView.setTextColor(color);

            viewHolder.itemView.setTag(cursors.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return cursors.size() + 1;
    }

    @Override
    public void onClick(View v) {

        int color = ContextCompat.getColor(context, R.color.colorHasReadText);
        TextView titleView = (TextView) v.findViewById(R.id.news_unit_title);
        TextView introView = null;
        TextView sourceView = null;
        if (titleView != null) {
            introView = (TextView) v.findViewById(R.id.news_unit_intro);
            sourceView = (TextView) v.findViewById(R.id.news_unit_source);
        } else {
            titleView = (TextView) v.findViewById(R.id.news_unit_pics_title);
            if (titleView != null) {
                introView = (TextView) v.findViewById(R.id.news_unit_pics_intro);
                sourceView = (TextView) v.findViewById(R.id.news_unit_pics_source);
            } else {
//                is footer view
                return;
            }
        }

        try {
            titleView.setTextColor(color);
            introView.setTextColor(color);
            sourceView.setTextColor(color);
        } catch (Exception e) {}

        if (onRecyclerViewItemClickListener != null) {
            onRecyclerViewItemClickListener.onItemClick(v, (NewsCursor) v.getTag());
        }
    }

    public void addItem(NewsCursor data) {
        cursors.add(data);
        notifyItemInserted(cursors.size());
        IDToPosition.put(data.getNewsMetaInfo().getId(), cursors.size());
    }

    public void clear() {
        for (int i = 0; i < cursors.size(); i++) {
            NewsCursor cursor = cursors.get(i);
            if (cursor != null) {
                cursor.close();
            }
        }
        cursors = new ArrayList<NewsCursor>();
        IDToPosition = new HashMap<String, Integer>();
        notifyDataSetChanged();
    }

    private static class PicsViewHolder extends RecyclerView.ViewHolder
        implements LoaderManager.LoaderCallbacks<Bitmap> {
        public View view;
        private ImageView imageView;
        private Context context;
        private URL summaryPicURL;

        PicsViewHolder(View view, Context context) {
            super(view);
            this.context = context;
            this.view = view;
        }

        void setSummaryPicURL(URL summaryPicURL) {
            this.summaryPicURL = summaryPicURL;
        }

        @Override
        public Loader<Bitmap> onCreateLoader(int id, Bundle args) {
            imageView = (ImageView) view.findViewById(R.id.news_unit_pics_image);
            imageView.setImageResource(R.mipmap.ic_placeholder);
            Loader<Bitmap> loader = new ResourceLoader(context, summaryPicURL,
                    new DatabaseResourceCache(CacheDBOpenHelper.getInstance(context.getApplicationContext()),
                            new WebResourceSource(200, 200, 1000, 1000)), true);

            return loader;
        }

        @Override
        public void onLoadFinished(Loader<Bitmap> loader, Bitmap data) {
            Log.i("loaded_pic", "on load finished");
            URL urls[] = ((NewsCursor) itemView.getTag()).getNewsMetaInfo().getPictures();
            if (urls == null || urls.length == 0) {
                Log.i("loaded_pic", "tag got no urls " + ((NewsCursor) itemView.getTag()).getNewsMetaInfo().getTitle());
                return;
            }

            URL url = urls[0];
            URL resourceUrl = ((ResourceLoader) loader).getUrl();

            if (!url.equals(resourceUrl)) {
                Log.i("recommend", "url:" + url.toString());
                Log.i("recommend", "res url:" + resourceUrl.toString());
                return;
            }

            if (data == null || data.getByteCount() == 0 || data.getHeight() == 0) {
                imageView.setImageResource(R.mipmap.ic_placeholder);
            } else {
                imageView.setImageBitmap(data);
            }
        }

        @Override
        public void onLoaderReset(Loader<Bitmap> loader) {}

    }

    private static class TextViewHolder extends RecyclerView.ViewHolder {
        public View view;
        TextViewHolder(View view) {
            super(view);
            this.view = view;
        }
    }

    private static class FooterViewHolder extends RecyclerView.ViewHolder {
        public View view;
        FooterViewHolder(View view) {
            super(view);
            this.view = view;
        }
    }

}

