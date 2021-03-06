package com.java.team17.fragments;

import android.content.Context;
import android.os.Bundle;
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

import com.java.team17.R;
import com.java.team17.controller.NewsListRecyclerViewListener;
import com.java.team17.controller.NewsMetainfoLoader;
import com.java.team17.controller.NewsMetainfoRecyclerViewAdapter;
import com.java.team17.data.BookmarkManager;
import com.java.team17.data.HistoryManager;
import com.java.team17.data.HybridNewsContentSource;
import com.java.team17.data.KeywordHistoryManager;
import com.java.team17.data.NewsCategoryTag;
import com.java.team17.data.NewsContentSource;
import com.java.team17.data.NewsCursor;
import com.java.team17.data.NewsList;
import com.java.team17.data.NewsMetaInfoListSource;
import com.java.team17.data.SourceNewsList;
import com.java.team17.data.WebNewsContentSource;
import com.java.team17.data.WebNewsMetaInfoListSource;
import com.java.team17.data.db.CacheDBOpenHelper;
import com.java.team17.data.rss.RSSManager;
import com.java.team17.data.rss.WebPageNewsContentSource;

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
    private static final String ARG_FRAGMENT_TYPE = "type";

    public static final int NEWS_CURSOR_LOADER_ID = 1;
    public static final int IMAGE_LOADER_ID = 2;

    public static final int WEB_FRAGMENT = 0;
    public static final int DATABASE_FRAGMENT = 1;
    public static final int BOOKMARK_FRAGMENT = 2;
    public static final int RSS_FRAGMENT = 3;
    public static final int RECOMMEND_FRAGMENT = 4;

    private String category;
    private String keyword;
    private int fragmentType;

    private OnFragmentInteractionListener mListener;
    private NewsMetainfoRecyclerViewAdapter mAdapter;
    private NewsContentSource mNewsContentSource;
    private NewsMetaInfoListSource mMetaInfoListSource;
    private NewsListRecyclerViewListener mNewsListRecyclerViewListener;
    private RecyclerView mRecyclerView;
    private LinearLayout curView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private Loader<List<NewsCursor>> mLoader;
    private NewsList newsList;
    private NewsCategoryTag tag;
    private int type;
    private NewsCursor mCursor;
    private boolean isRecommended = false;

    private View mView = null;

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

    public static NewsListFragment newInstance(String category, String keyword, int fragmentType) {
        NewsListFragment fragment = new NewsListFragment();
        Bundle args = new Bundle();

        args.putString(ARG_CATEGORY, category);
        args.putString(ARG_KEYWORD, keyword);
        args.putInt(ARG_FRAGMENT_TYPE, fragmentType);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setRetainInstance(true);
        if (getArguments() != null) {
            category = getArguments().getString(ARG_CATEGORY);
            keyword = getArguments().getString(ARG_KEYWORD);
            fragmentType = getArguments().getInt(ARG_FRAGMENT_TYPE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mView != null) {
            ((ViewGroup) mView.getParent()).removeView(mView);
        }
    }

    private void initWebFragment() {
        type = 0;
        tag = NewsCategoryTag.getCategoryByTitleEN(category);
        if (keyword != null) {
            mMetaInfoListSource = new WebNewsMetaInfoListSource("http://166.111.68.66:2042/news/action/query/search");
        } else {
            mMetaInfoListSource = new WebNewsMetaInfoListSource("http://166.111.68.66:2042/news/action/query/latest");
        }
    }

    private void initDatabaseFragment() {
        if(category.equals("rss"))
            type = 1;
        else
            type = -1;
        tag = NewsCategoryTag.getCategoryByTitleEN(category);
        HistoryManager historyManager = HistoryManager.getInstance(CacheDBOpenHelper
                .getInstance(getContext().getApplicationContext()));
        mMetaInfoListSource = historyManager.getNewsMetaInfoListSource(type);
    }

    private void initBookmarkFragment() {
        type = -1;
        mMetaInfoListSource = BookmarkManager.getInstance(CacheDBOpenHelper.
                getInstance(getContext().getApplicationContext())).getNewsMetaInfoListSource();
        Log.i("bookmark", "bookmarkInited");
    }

    private void initRSSFragment(){
        type = 0;
        try {
            mMetaInfoListSource = RSSManager.
                    getInstance(CacheDBOpenHelper.getInstance(getContext().
                            getApplicationContext()))
                    .getNewsMetaInfoListSource();
            Log.i("rss", "fragment initialised");
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    private void initRecommendFragment() {
        type = 0;
        mMetaInfoListSource = new WebNewsMetaInfoListSource("http://166.111.68.66:2042/news/action/query/search");
        isRecommended = true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (mView != null) {
            return mView;
        }

        curView = (LinearLayout) inflater.inflate(R.layout.fragment_news_list, container, false);

        mRecyclerView = (RecyclerView) curView.findViewById(R.id.news_info_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(container.getContext()));


        mAdapter = new NewsMetainfoRecyclerViewAdapter(getContext(), getLoaderManager());
        mAdapter.setOnRecyclerViewItemClickListener(this);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setHasFixedSize(true);

        mNewsListRecyclerViewListener = new NewsListRecyclerViewListener(this);
        mRecyclerView.addOnScrollListener(mNewsListRecyclerViewListener);

        mNewsContentSource = HistoryManager.getInstance(CacheDBOpenHelper.
                getInstance(getContext().getApplicationContext())).getNewsContentSource(
                new HybridNewsContentSource(new WebNewsContentSource("http://166.111.68.66:2042/news/action/query/detail"),
                        new WebPageNewsContentSource()));

        switch (fragmentType) {
            case WEB_FRAGMENT:
                initWebFragment();
                break;
            case DATABASE_FRAGMENT:
                initDatabaseFragment();
                break;
            case BOOKMARK_FRAGMENT:
                initBookmarkFragment();
                break;
            case RSS_FRAGMENT:
                initRSSFragment();
                break;
            case RECOMMEND_FRAGMENT:
                initRecommendFragment();
                break;
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
    public void onStart() {
        super.onStart();

        mView = curView;

        if (mAdapter.getItemCount() <= 1) {
            // contains footer
            mSwipeRefreshLayout.setRefreshing(true);
            reloadFromBeginning();
        }
    }

    public void onItemClick(View view, NewsCursor cursor) {
        if (mListener != null) {
            mListener.onFragmentInteraction(cursor);
        }
    }

    private void resetNewsList() {
        if(isRecommended)
            newsList = KeywordHistoryManager.getInstance(CacheDBOpenHelper.getInstance(getContext().
                    getApplicationContext())).getRecommendedNewsList(mMetaInfoListSource, mNewsContentSource);
        else
            newsList = new SourceNewsList(mMetaInfoListSource, mNewsContentSource, keyword, tag, type);
        mNewsListRecyclerViewListener.setFinished(false);
        mNewsListRecyclerViewListener.setFirstBatchLoaded(false);
        mNewsListRecyclerViewListener.resetLastLoad();
        mAdapter.clear();
    }

    public void reloadFromBeginning() {
        mCursor = null;
        resetNewsList();
        Bundle args = new Bundle();
        Loader<List<NewsCursor> > loader = getLoaderManager().getLoader(NEWS_CURSOR_LOADER_ID);
        if (loader != null) {
            getLoaderManager().destroyLoader(NEWS_CURSOR_LOADER_ID);
        }
        mLoader = getLoaderManager().initLoader(NEWS_CURSOR_LOADER_ID, args, this);

        mLoader.forceLoad();
    }

    private void loadNextData() {
        if (((NewsMetainfoLoader) mLoader).isFinished()) {
            mNewsListRecyclerViewListener.setFinished(true);
        } else {
            mNewsListRecyclerViewListener.setFinished(false);
            mLoader.forceLoad();
        }
    }


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
        Log.i("load_process", "load finished, data size: " + data.size());
        NewsMetainfoLoader metainfoLoader = (NewsMetainfoLoader) loader;
        mNewsListRecyclerViewListener.setFirstBatchLoaded(true);
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
        Log.i("load_process", "load more");
        loadNextData();
    }

    @Override
    public void onDestroy() {
//        if (mAdapter != null) {
//            mAdapter.clear();
//        }
        super.onDestroy();
    }
}
