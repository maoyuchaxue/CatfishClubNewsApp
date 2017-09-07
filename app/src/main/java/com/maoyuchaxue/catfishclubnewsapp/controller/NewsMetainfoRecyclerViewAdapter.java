package com.maoyuchaxue.catfishclubnewsapp.controller;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.maoyuchaxue.catfishclubnewsapp.R;
import com.maoyuchaxue.catfishclubnewsapp.data.NewsCursor;
import com.maoyuchaxue.catfishclubnewsapp.data.NewsMetaInfo;

import java.util.ArrayList;

/**
 * Created by catfish on 17/9/6.
 */



public class NewsMetainfoRecyclerViewAdapter
        extends RecyclerView.Adapter<NewsMetainfoRecyclerViewAdapter.ViewHolder>
        implements View.OnClickListener {
    public ArrayList<NewsCursor> cursors;
    public NewsMetainfoRecyclerViewAdapter() {
        cursors = new ArrayList<NewsCursor>();
    }

    public static interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, NewsCursor cursor);
    }

    private OnRecyclerViewItemClickListener onRecyclerViewItemClickListener = null;

    public void setOnRecyclerViewItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.onRecyclerViewItemClickListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.news_into_unit_layout, null);
        ViewHolder vh = new ViewHolder(view);
        view.setOnClickListener(this);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        View view = viewHolder.view;
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

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public View view;
        public ViewHolder(View view) {
            super(view);
            this.view = view;
        }
    }

}

