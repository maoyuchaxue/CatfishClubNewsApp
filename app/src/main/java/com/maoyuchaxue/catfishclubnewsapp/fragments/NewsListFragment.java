package com.maoyuchaxue.catfishclubnewsapp.fragments;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.maoyuchaxue.catfishclubnewsapp.R;
import com.maoyuchaxue.catfishclubnewsapp.activities.MainActivity;
import com.maoyuchaxue.catfishclubnewsapp.activities.NewsViewActivity;
import com.maoyuchaxue.catfishclubnewsapp.controller.NewsListRecyclerViewListener;
import com.maoyuchaxue.catfishclubnewsapp.controller.NewsMetainfoLoader;
import com.maoyuchaxue.catfishclubnewsapp.controller.NewsMetainfoRecyclerViewAdapter;
import com.maoyuchaxue.catfishclubnewsapp.data.DatabaseNewsContentCache;
import com.maoyuchaxue.catfishclubnewsapp.data.DatabaseNewsMetaInfoListCache;
import com.maoyuchaxue.catfishclubnewsapp.data.NewsCategoryTag;
import com.maoyuchaxue.catfishclubnewsapp.data.NewsContentSource;
import com.maoyuchaxue.catfishclubnewsapp.data.NewsCursor;
import com.maoyuchaxue.catfishclubnewsapp.data.NewsList;
import com.maoyuchaxue.catfishclubnewsapp.data.NewsMetaInfoListSource;
import com.maoyuchaxue.catfishclubnewsapp.data.SourceNewsList;
import com.maoyuchaxue.catfishclubnewsapp.data.WebNewsContentSource;
import com.maoyuchaxue.catfishclubnewsapp.data.WebNewsMetaInfoListSource;
import com.maoyuchaxue.catfishclubnewsapp.data.db.CacheDBOpenHelper;

import org.w3c.dom.Text;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link NewsListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link NewsListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NewsListFragment extends Fragment
        implements NewsMetainfoRecyclerViewAdapter.OnRecyclerViewItemClickListener,
        LoaderManager.LoaderCallbacks<List<NewsCursor> >,
        NewsListRecyclerViewListener.OnLoadMoreListener {

    private static final String ARG_CATEGORY = "category";
    private static final String ARG_KEYWORD = "keyword";
    private static final String ARG_IS_LOCAL = "is_local";

    private static final int NEWS_CURSOR_LOADER_ID = 1;

    private String category;
    private String keyword;
    private boolean isLocal;

    private OnFragmentInteractionListener mListener;
    private NewsMetainfoRecyclerViewAdapter mAdapter;
    private NewsContentSource mNewsContentSource;
    private NewsMetaInfoListSource mMetaInfoListSource;
    private NewsListRecyclerViewListener mNewsListRecyclerViewListener;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private Loader<List<NewsCursor>> mLoader;
    private NewsList newsList;
    private NewsCategoryTag tag;
    private NewsCursor mCursor;


    public NewsListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param category Parameter 1.
     * @param keyword Parameter 2.
     * @return A new instance of fragment NewsListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NewsListFragment newInstance(String category, String keyword, boolean isLocal) {
        NewsListFragment fragment = new NewsListFragment();
        Bundle args = new Bundle();

        args.putString(ARG_CATEGORY, category);
        args.putString(ARG_KEYWORD, keyword);
        args.putBoolean(ARG_IS_LOCAL, isLocal);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            category = getArguments().getString(ARG_CATEGORY);
            keyword = getArguments().getString(ARG_KEYWORD);
            isLocal = getArguments().getBoolean(ARG_IS_LOCAL);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        LinearLayout curView = (LinearLayout) inflater.inflate(R.layout.fragment_news_list, container, false);

        RecyclerView recyclerView = (RecyclerView) curView.findViewById(R.id.news_info_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(container.getContext()));

        mAdapter= new NewsMetainfoRecyclerViewAdapter();
        mAdapter.setOnRecyclerViewItemClickListener(this);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setHasFixedSize(true);

        mNewsListRecyclerViewListener = new NewsListRecyclerViewListener(this);
        recyclerView.addOnScrollListener(mNewsListRecyclerViewListener);

        mNewsContentSource = new DatabaseNewsContentCache(new CacheDBOpenHelper(getContext()),
                new WebNewsContentSource("http://166.111.68.66:2042/news/action/query/detail"));

        tag = NewsCategoryTag.getCategoryByTitleEN(category);
        if (keyword != null) {
            mMetaInfoListSource = new DatabaseNewsMetaInfoListCache(new CacheDBOpenHelper(getContext()),
                    new WebNewsMetaInfoListSource("http://166.111.68.66:2042/news/action/query/search"));
        } else {
            mMetaInfoListSource = new DatabaseNewsMetaInfoListCache(new CacheDBOpenHelper(getContext()),
                    new WebNewsMetaInfoListSource("http://166.111.68.66:2042/news/action/query/latest"));
//            mMetaInfoListSource = new WebNewsMetaInfoListSource("http://166.111.68.66:2042/news/action/query/latest");
        }

        mCursor = null;

        mSwipeRefreshLayout = (SwipeRefreshLayout) curView.findViewById(R.id.news_info_swipe_refresh);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRefreshLayout.setRefreshing(true);
                reloadFromBeginning();
            }
        });

        return curView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        reloadFromBeginning();
    }

    public void onItemClick(View view, NewsCursor cursor) {
        if (mListener != null) {
            mListener.onFragmentInteraction(cursor);
        }
    }

    private void resetNewsList() {
        newsList = new SourceNewsList(mMetaInfoListSource, mNewsContentSource, keyword, tag);
    }

    private void reloadFromBeginning() {
        mCursor = null;
        resetNewsList();
        Bundle args = new Bundle();
        Loader<List<NewsCursor> > loader = getLoaderManager().getLoader(NEWS_CURSOR_LOADER_ID);
        if (loader != null && loader.isReset()) {
            getLoaderManager().destroyLoader(NEWS_CURSOR_LOADER_ID);
        } else {
            mLoader = getLoaderManager().initLoader(NEWS_CURSOR_LOADER_ID, args, this);
        }
        mLoader.forceLoad();
    }

    private void loadNextData() {
        mLoader.forceLoad();
    }

//    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.onFragmentInteraction(uri);
//        }
//    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(NewsCursor cursor);
    }

    @Override
    public Loader<List<NewsCursor> > onCreateLoader(int id, Bundle args) {
        return new NewsMetainfoLoader(this.getContext(), 10, mCursor, newsList);
    }

    @Override
    public void onLoadFinished(Loader<List<NewsCursor>> loader, List<NewsCursor> data) {
        NewsMetainfoLoader metainfoLoader = (NewsMetainfoLoader) loader;
        mCursor = metainfoLoader.getCurrentCursor();
        for (int i = 0; i < data.size(); i++) {
            mAdapter.addItem(data.get(i));
        }
        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onLoaderReset(Loader<List<NewsCursor>> loader) {}

    @Override
    public void onLoadMore() {
        loadNextData();
    }
}
