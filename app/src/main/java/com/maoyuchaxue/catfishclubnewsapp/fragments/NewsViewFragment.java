package com.maoyuchaxue.catfishclubnewsapp.fragments;

import android.content.Context;
import android.support.constraint.solver.Cache;
import android.support.v4.content.Loader;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SynthesizerListener;
import com.maoyuchaxue.catfishclubnewsapp.R;
import com.maoyuchaxue.catfishclubnewsapp.activities.NewsViewActivity;
import com.maoyuchaxue.catfishclubnewsapp.controller.NewsContentAndImageAdapter;
import com.maoyuchaxue.catfishclubnewsapp.controller.NewsContentLoader;
import com.maoyuchaxue.catfishclubnewsapp.data.BookmarkManager;
import com.maoyuchaxue.catfishclubnewsapp.data.DatabaseNewsContentCache;
import com.maoyuchaxue.catfishclubnewsapp.data.HistoryManager;
import com.maoyuchaxue.catfishclubnewsapp.data.NewsContent;
import com.maoyuchaxue.catfishclubnewsapp.data.NewsContentSource;
import com.maoyuchaxue.catfishclubnewsapp.data.NewsMetaInfo;
import com.maoyuchaxue.catfishclubnewsapp.data.WebNewsContentSource;
import com.maoyuchaxue.catfishclubnewsapp.data.db.CacheDBOpenHelper;

import java.net.URL;
import java.util.Arrays;
import java.util.List;

import cn.sharesdk.onekeyshare.OnekeyShare;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NewsViewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NewsViewFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<NewsContent> {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_NEWS_ID = "news_id";
    private static final String ARG_TITLE = "title";
    private View homeView;
    private String newsID;
    private String title;
    private NewsMetaInfo metaInfo;
    private NewsContentSource contentSource;
    private ListView mListView;
    private SpeechSynthesizer speechSynthesizer;
    private String speakContent = null;
    private NewsContentAndImageAdapter mAdapter;
    Loader<NewsContent> mLoader;

    public final static int NEWS_CONTENT_LOADER_ID = 0;
    public final static int NEWS_RESOURCE_LOADER_ID = 1;


//    private OnFragmentInteractionListener mListener;

    public NewsViewFragment() {
        // Required empty public constructor

    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param newsID ID of the news shown.
     * @return A new instance of fragment NewsViewFragment.
     */

    public static NewsViewFragment newInstance(String newsID, String title) {
        NewsViewFragment fragment = new NewsViewFragment();
        Bundle args = new Bundle();
        args.putString(ARG_NEWS_ID, newsID);
        args.putString(ARG_TITLE, title);
        fragment.setArguments(args);
        return fragment;
    }

    public static NewsViewFragment newInstance(SpeechSynthesizer speechSynthesizer, NewsMetaInfo metaInfo){
        NewsViewFragment instance = newInstance(metaInfo.getId(), metaInfo.getTitle());

        instance.metaInfo = metaInfo;
        instance.speechSynthesizer = speechSynthesizer;
        return instance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        contentSource = new WebNewsContentSource("http://166.111.68.66:2042/news/action/query/detail");
//        contentSource = new DatabaseNewsContentCache(
//                CacheDBOpenHelper.getInstance(getContext().getApplicationContext())
//               , new WebNewsContentSource("http://166.111.68.66:2042/news/action/query/detail")
//        );
        contentSource = HistoryManager.getInstance(CacheDBOpenHelper.
                getInstance(getContext().getApplicationContext())).getNewsContentSource(
               new WebNewsContentSource("http://166.111.68.66:2042/news/action/query/detail")
        );
        if (getArguments() != null) {
            newsID = getArguments().getString(ARG_NEWS_ID);
            title = getArguments().getString(ARG_TITLE);
        }

        mAdapter = new NewsContentAndImageAdapter(getContext(), getLoaderManager());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        homeView = inflater.inflate(R.layout.fragment_news_view, container, false);

        mListView = (ListView) homeView.findViewById(R.id.news_view_content);
        mListView.setAdapter(mAdapter);

        Bundle args = new Bundle();
        mLoader = getLoaderManager().initLoader(NEWS_CONTENT_LOADER_ID, args, this);
        mLoader.forceLoad();

        TextView idTextView = (TextView) homeView.findViewById(R.id.news_view_id);
        TextView titleTextView = (TextView) homeView.findViewById(R.id.news_view_title);

        idTextView.setText(newsID);
        titleTextView.setText(title);
        return homeView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


    @Override
    public Loader<NewsContent> onCreateLoader(int id, Bundle args) {
        return new NewsContentLoader(this.getContext(), newsID, contentSource);
    }

    @Override
    public void onLoadFinished(Loader<NewsContent> loader, NewsContent data) {

        TextView authorTextView = (TextView) homeView.findViewById(R.id.news_view_author);
        authorTextView.setText(data.getJournalist());
        String content = data.getContentStr();

        URL urls[] = metaInfo.getPictures();
        String splitContents[] = content.split("</p>");

        Log.i("madapter", "input data length: " + urls.length + " " + splitContents.length);
        mAdapter.resetData(splitContents, urls);

        Spanned spannedContent;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            spannedContent = Html.fromHtml(content, Html.FROM_HTML_MODE_LEGACY);
        } else {
            spannedContent = Html.fromHtml(content);
        }

        speakContent = metaInfo.getTitle() + " " + spannedContent.toString();

        // add to history
        HistoryManager.getInstance(CacheDBOpenHelper.getInstance(getContext().getApplicationContext())).
            add(metaInfo, data);
    }

    @Override
    public void onLoaderReset(Loader<NewsContent> loader) {}

    public void addCurrentNewsToBookmark(BookmarkManager bookmarkManager) {
        bookmarkManager.add(metaInfo);
    }

    public void removeCurrentNewsFromBookmark(BookmarkManager bookmarkManager) {
//        TODO: remove!
    }

    public void startSpeaking() {
        if (speakContent == null) {
            return;
        }

        speechSynthesizer.startSpeaking(speakContent, new SynthesizerListener() {
            @Override
            public void onSpeakBegin() {

            }

            @Override
            public void onBufferProgress(int i, int i1, int i2, String s) {

            }

            @Override
            public void onSpeakPaused() {

            }

            @Override
            public void onSpeakResumed() {

            }

            @Override
            public void onSpeakProgress(int i, int i1, int i2) {

            }

            @Override
            public void onCompleted(SpeechError speechError) {

            }

            @Override
            public void onEvent(int i, int i1, int i2, Bundle bundle) {

            }
        });
    }


    public void stopSpeaking() {
        if (speechSynthesizer.isSpeaking()) {
            speechSynthesizer.stopSpeaking();
        }
    }

}
