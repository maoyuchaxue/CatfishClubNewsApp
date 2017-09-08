package com.maoyuchaxue.catfishclubnewsapp.controller;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.maoyuchaxue.catfishclubnewsapp.R;
import com.maoyuchaxue.catfishclubnewsapp.data.NewsCursor;
import com.maoyuchaxue.catfishclubnewsapp.data.NewsMetaInfo;
import com.maoyuchaxue.catfishclubnewsapp.data.WebResourceSource;
import com.maoyuchaxue.catfishclubnewsapp.fragments.NewsListFragment;

import java.net.URL;
import java.util.ArrayList;
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
    public NewsMetainfoRecyclerViewAdapter(Context context, LoaderManager loaderManager) {
        this.context = context;
        this.loaderManager = loaderManager;
        cursors = new ArrayList<NewsCursor>();
    }

    private static final int PICS_VIEW = 0;
    private static final int TEXT_ONLY_VIEW = 1;

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
        }
        if (view != null) {
            view.setOnClickListener(this);
        }

        return viewHolder;
    }

    @Override
    public int getItemViewType(int position) {
        URL[] urls = cursors.get(position).getNewsMetaInfo().getPictures();
        if (urls == null || urls.length == 0) {
            return TEXT_ONLY_VIEW;
        } else {
            return PICS_VIEW;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        if (viewHolder instanceof PicsViewHolder) {
            PicsViewHolder picsViewHolder= (PicsViewHolder) viewHolder;
            View view = picsViewHolder.view;
            NewsCursor cursor = cursors.get(position);
            NewsMetaInfo info = cursor.getNewsMetaInfo();

            TextView titleView = (TextView) view.findViewById(R.id.news_unit_pics_title);
            TextView introView = (TextView) view.findViewById(R.id.news_unit_pics_intro);
            TextView sourceView = (TextView) view.findViewById(R.id.news_unit_pics_source);

            titleView.setText(info.getTitle());
            introView.setText(info.getIntro());
            sourceView.setText(info.getSrcSite() + "  " + info.getAuthor());

            picsViewHolder.setSummaryPicURL(info.getPictures()[0]);
            viewHolder.itemView.setTag(cursors.get(position));
            loaderManager.initLoader(NewsListFragment.IMAGE_LOADER_ID + position , null, picsViewHolder).forceLoad();

        } else if (viewHolder instanceof TextViewHolder) {
            TextViewHolder textViewHolder = (TextViewHolder) viewHolder;
            View view = textViewHolder.view;
            NewsCursor cursor = cursors.get(position);
            NewsMetaInfo info = cursor.getNewsMetaInfo();
            TextView titleView = (TextView) view.findViewById(R.id.news_unit_title);
            TextView introView = (TextView) view.findViewById(R.id.news_unit_intro);
            TextView sourceView = (TextView) view.findViewById(R.id.news_unit_source);
            titleView.setText(info.getTitle());
            introView.setText(info.getIntro());
            sourceView.setText(info.getSrcSite() + "  " + info.getAuthor());

            viewHolder.itemView.setTag(cursors.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return cursors.size();
    }

    @Override
    public void onClick(View v) {
        if (onRecyclerViewItemClickListener != null) {
            onRecyclerViewItemClickListener.onItemClick(v, (NewsCursor) v.getTag());
        }
    }

    public void addItem(NewsCursor data) {
        cursors.add(data);
        notifyItemInserted(cursors.size());
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

        public void setSummaryPicURL(URL summaryPicURL) {
            this.summaryPicURL = summaryPicURL;
        }

        @Override
        public Loader<Bitmap> onCreateLoader(int id, Bundle args) {
            imageView = (ImageView) view.findViewById(R.id.news_unit_pics_image);
            return new ResourceLoader(context, summaryPicURL, new WebResourceSource());
        }

        @Override
        public void onLoadFinished(Loader<Bitmap> loader, Bitmap data) {
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

}

