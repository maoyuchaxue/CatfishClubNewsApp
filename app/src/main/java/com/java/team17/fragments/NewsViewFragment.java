package com.java.team17.fragments;

import android.content.Context;
import android.preference.PreferenceManager;
import android.support.v4.content.Loader;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.iflytek.cloud.SpeechSynthesizer;
import com.java.team17.R;
import com.java.team17.activities.NewsViewActivity;
import com.java.team17.controller.BufferedSpeechSynthesizer;
import com.java.team17.controller.NewsContentAndImageAdapter;
import com.java.team17.controller.NewsContentLoader;
import com.java.team17.data.BookmarkManager;
import com.java.team17.data.HistoryManager;
import com.java.team17.data.HybridNewsContentSource;
import com.java.team17.data.KeywordHistoryManager;
import com.java.team17.data.NewsContent;
import com.java.team17.data.NewsContentSource;
import com.java.team17.data.NewsCursor;
import com.java.team17.data.NewsMetaInfo;
import com.java.team17.data.WebNewsContentSource;
import com.java.team17.data.db.CacheDBOpenHelper;
import com.java.team17.data.rss.WebPageNewsContentSource;

import java.net.URL;

import cn.sharesdk.onekeyshare.OnekeyShare;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NewsViewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NewsViewFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<NewsContent>,
        NewsContentAndImageAdapter.OnClickNewsListener {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_NEWS_META_INFO = "metaInfo";
    private static final String ARG_TITLE = "title";

    private NewsViewActivity activity;
    private View homeView;
    private String newsID;
    private String title;
    private NewsMetaInfo metaInfo;
    private NewsContentSource contentSource;
    private ListView mListView;
    private BufferedSpeechSynthesizer speechSynthesizer;
    private String[] speakContent = null;
    private NewsContentAndImageAdapter mAdapter;
    Loader<NewsContent> mLoader;

    public final static int NEWS_CONTENT_LOADER_ID = 0;
    public final static int NEWS_RECOMMEND_LOADER_ID = 1;
    public final static int NEWS_RESOURCE_LOADER_ID = 2;


//    private OnFragmentInteractionListener mListener;

    public NewsViewFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param newsMetaInfo meta info of the news shown.
     * @return A new instance of fragment NewsViewFragment.
     */

    public static NewsViewFragment newInstance(NewsMetaInfo newsMetaInfo, String title) {
        NewsViewFragment fragment = new NewsViewFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_NEWS_META_INFO, newsMetaInfo);
        args.putString(ARG_TITLE, title);
        fragment.setArguments(args);
        return fragment;
    }

    public static NewsViewFragment newInstance(NewsViewActivity activity, SpeechSynthesizer speechSynthesizer, NewsMetaInfo metaInfo){
        NewsViewFragment instance = newInstance(metaInfo, metaInfo.getTitle());
        instance.activity = activity;
        instance.metaInfo = metaInfo;
        instance.speechSynthesizer = new BufferedSpeechSynthesizer(speechSynthesizer);
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

        NewsContentSource webSource = null, webPageSource = null;
        Boolean isOfflineMode = PreferenceManager.getDefaultSharedPreferences(getContext())
                .getBoolean("offline_mode", false);

//        if offline, front src should be set to null to prevent network connection
        if (!isOfflineMode) {
            webSource = new WebNewsContentSource("http://166.111.68.66:2042/news/action/query/detail");
            webPageSource = new WebPageNewsContentSource();
        }

        contentSource = HistoryManager.getInstance(CacheDBOpenHelper.
                getInstance(getContext().getApplicationContext())).getNewsContentSource(
                new HybridNewsContentSource(webSource, webPageSource));
//        contentSource = HistoryManager.getInstance(CacheDBOpenHelper.
//                getInstance(getContext().getApplicationContext())).getNewsContentSource(
//               new WebNewsContentSource("http://166.111.68.66:2042/news/action/query/detail")
//        );
        if (getArguments() != null) {
            metaInfo = (NewsMetaInfo)getArguments().getSerializable(ARG_NEWS_META_INFO);
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
        return new NewsContentLoader(this.getContext(), metaInfo, contentSource);
    }

    @Override
    public void onLoadFinished(Loader<NewsContent> loader, NewsContent data) {

        if (data == null) {
            mAdapter.setUnavailable();
            return;
        }

        TextView authorTextView = (TextView) homeView.findViewById(R.id.news_view_author);
        authorTextView.setText(data.getJournalist());
        String content = data.getContentStr();

        URL urls[] = null;
        Boolean isTextOnly = PreferenceManager.getDefaultSharedPreferences(getContext())
                .getBoolean("text_only_mode", false);
        if (isTextOnly) {
            urls = new URL[0];
        } else {
            urls = metaInfo.getPictures();
        }

        String splitContents[] = content.split("</p>");

        for (int i = 0; i < splitContents.length; i++) {
            String s = splitContents[i];
            if (!s.isEmpty()) {
                splitContents[i] = s + "</p>";
            }
        }

        Log.i("madapter", "input data length: " + urls.length + " " + splitContents.length);
        mAdapter.resetData(splitContents, urls);



        speakContent = new String[splitContents.length + 1];
        speakContent[0] = metaInfo.getTitle();
        for (int i = 0; i < splitContents.length; i++) {
            Spanned spannedContent;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                spannedContent = Html.fromHtml(splitContents[i], Html.FROM_HTML_MODE_LEGACY);
            } else {
                spannedContent = Html.fromHtml(splitContents[i]);
            }
            speakContent[i+1] = spannedContent.toString();
        }

        String recommendLimit = PreferenceManager.getDefaultSharedPreferences(getContext())
                .getString("recommend_limit", "5");
        int recommend = metaInfo.getType() == 1 ? 0 :
                Integer.parseInt(recommendLimit);
        if (recommend > 0) {
            mAdapter.startRecommendLoading(data, recommend);
            mListView.setOnItemClickListener(mAdapter);
            mAdapter.setOnClickNewsListener(this);
        }

        // add to history
        HistoryManager.getInstance(CacheDBOpenHelper.getInstance(getContext().getApplicationContext())).
            add(metaInfo, data);
        KeywordHistoryManager.getInstance(CacheDBOpenHelper.getInstance(getContext().getApplicationContext())).
                addKeys(data.getKeywords());

    }

    @Override
    public void onLoaderReset(Loader<NewsContent> loader) {}

    public void addCurrentNewsToBookmark(BookmarkManager bookmarkManager) {
//        DO NOTHING TILL USER LEAVE THIS ACTIVITY
    }

    public void removeCurrentNewsFromBookmark(BookmarkManager bookmarkManager) {
//        DO NOTHING TILL USER LEAVE THIS ACTIVITY
    }

    public boolean shareContent(Context context) {
        if (speakContent == null) {
            return false;
        } else {

            OnekeyShare oks = new OnekeyShare();
            //关闭sso授权
            oks.disableSSOWhenAuthorize();

            // 分享时Notification的图标和文字  2.5.9以后的版本不     调用此方法
            //oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
            // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用


            oks.setTitle(metaInfo.getTitle() + " -- from CCNAPP");
            // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
//            oks.setTitleUrl(metaInfo.getUrl().toString());
            // text是分享文本，所有平台都需要这个字段
            oks.setText("分享新闻: " + metaInfo.getTitle() + " 点击查看：" + metaInfo.getUrl().toString() + " -- from CCNAPP");

            URL pictures[] = metaInfo.getPictures();
            if (pictures.length > 0) {
                oks.setImageUrl(pictures[0].toString());
            }
            else
            {
                oks.setImageUrl("http://wx2.sinaimg.cn/mw690/005VL8g3ly1fjidoye9iuj30710703yg.jpg");
            }

            oks.setComment("输入评论：");
            // site是分享此内容的网站名称，仅在QQ空间使用
            oks.setSite(metaInfo.getUrl().toString());
            // siteUrl是分享此内容的网站地址，仅在QQ空间使用
            oks.setSiteUrl(metaInfo.getUrl().toString());

            oks.show(context);

//          TODO: optimize share URL
            return true;
        }
    }

    public void startSpeaking() {
        if (speakContent == null) {
            return;
        }
        speechSynthesizer.startSpeaking(speakContent);
    }


    public void stopSpeaking() {
        speechSynthesizer.stopSpeaking();
    }

    @Override
    public void onClickNews(NewsCursor cursor) {
        activity.onClickNews(cursor);
    }

    public NewsMetaInfo getNewsMetaInfo(){
        return metaInfo;
    }

}
