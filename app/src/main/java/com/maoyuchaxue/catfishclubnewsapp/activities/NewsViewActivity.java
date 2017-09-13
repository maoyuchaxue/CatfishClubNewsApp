package com.maoyuchaxue.catfishclubnewsapp.activities;

import android.app.Activity;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.maoyuchaxue.catfishclubnewsapp.R;
import com.maoyuchaxue.catfishclubnewsapp.data.BookmarkManager;
import com.maoyuchaxue.catfishclubnewsapp.data.HistoryManager;
import com.maoyuchaxue.catfishclubnewsapp.data.NewsContent;
import com.maoyuchaxue.catfishclubnewsapp.data.NewsCursor;
import com.maoyuchaxue.catfishclubnewsapp.data.NewsMetaInfo;
import com.maoyuchaxue.catfishclubnewsapp.data.db.CacheDBOpenHelper;
import com.maoyuchaxue.catfishclubnewsapp.fragments.NewsViewFragment;

import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SynthesizerListener;
import com.iflytek.cloud.SpeechError;

import cn.pedant.SweetAlert.SweetAlertDialog;
import cn.sharesdk.onekeyshare.OnekeyShare;

public class NewsViewActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int NEWS_VIEW_ACTIVITY = 0;

    private SpeechSynthesizer mTts;
    private NewsViewFragment newsViewFragment;

    private BookmarkManager bookmarkManager;
    private Toolbar toolbar;
    private boolean isInBookmark, initiallyIsInBookmark;

    private Intent resultIntent;

    private boolean isEnglish(){
        return newsViewFragment.getNewsMetaInfo().getLang().toLowerCase().contains("en");
    }

    private void initSynthesizer() {
        String speaker = null;
        if (isEnglish()) {
            speaker = PreferenceManager.getDefaultSharedPreferences(NewsViewActivity.this)
                    .getString("speech_en", "henry");
        } else {
            speaker = PreferenceManager.getDefaultSharedPreferences(NewsViewActivity.this)
                    .getString("speech_zh", "xiaoyan");
        }

        mTts.setParameter(SpeechConstant.VOICE_NAME, speaker);//设置发音人
        mTts.setParameter(SpeechConstant.SPEED, "50");//设置语速
        mTts.setParameter(SpeechConstant.VOLUME, "80");//设置音量，范围0~100
        mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD); //设置云端
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SpeechUtility.createUtility(NewsViewActivity.this, SpeechConstant.APPID +"=59b0c3fb");
        mTts = SpeechSynthesizer.createSynthesizer(NewsViewActivity.this, ini);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        resultIntent = new Intent();
        resultIntent.putExtra("is_bookmark_modified", false);


        setContentView(R.layout.activity_news_view);

        Intent intent = getIntent();
        NewsMetaInfo metaInfo = (NewsMetaInfo)intent.getSerializableExtra("meta_info");

        newsViewFragment = NewsViewFragment.newInstance(this, mTts, metaInfo);

        transaction.replace(R.id.news_view, newsViewFragment);
        transaction.commit();

        bookmarkManager = BookmarkManager.getInstance(CacheDBOpenHelper.getInstance(getApplicationContext()));
        isInBookmark = bookmarkManager.isBookmarked(metaInfo.getId());
        initiallyIsInBookmark = isInBookmark;

        resultIntent.putExtra("id", metaInfo.getId());
        resultIntent.putExtra("meta_info", metaInfo);

        setResult(Activity.RESULT_OK, resultIntent);

        toolbar = (Toolbar)findViewById(R.id.news_view_menu_toolbar);
        toolbar.setTitle("新闻详细");
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);

        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(this);

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.news_view_menu_share:
                        newsViewFragment.shareContent(NewsViewActivity.this);
                        break;
                    case R.id.news_view_menu_voice:
                        if (mTts.isSpeaking()) {
                            newsViewFragment.stopSpeaking();
                        } else {
                            newsViewFragment.startSpeaking();
                        }
                        break;

                    case R.id.news_view_menu_bookmark:
                        Log.i("bookmark", String.valueOf(isInBookmark));
                        if (isInBookmark) {
                            newsViewFragment.removeCurrentNewsFromBookmark(bookmarkManager);
                            isInBookmark = false;
                        } else {
                            newsViewFragment.addCurrentNewsToBookmark(bookmarkManager);
                            isInBookmark = true;
                        }

                        resultIntent.putExtra("is_bookmark_modified", (isInBookmark != initiallyIsInBookmark));
                        resultIntent.putExtra("final_bookmark_state", isInBookmark);
//                        setResult(Activity.RESULT_OK, resultIntent);

                        String alertText = isInBookmark ? "收藏成功辣" : "收藏取消辣";
                        new SweetAlertDialog(NewsViewActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                                .setTitleText(alertText)
                                .show();

                        resetMenuItemAppearance();
                        break;
                }
                return true;

            }
        });

        initSynthesizer();


    }

    private InitListener ini = new InitListener() {
        @Override
        public void onInit(int i) {

        }
    };

    private void resetMenuItemAppearance() {
        if (isInBookmark) {
            toolbar.getMenu().getItem(2).setIcon(ContextCompat.
                    getDrawable(getApplicationContext(), R.drawable.ic_bookmark_white_24dp));
        } else {
            toolbar.getMenu().getItem(2).setIcon(ContextCompat.
                    getDrawable(getApplicationContext(), R.drawable.ic_bookmark_border_white_24dp));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.news_view_menu, menu);
        resetMenuItemAppearance();
        return true;
    }

    @Override
    public void onClick(View view) {
        NewsViewActivity.this.finish();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mTts.isSpeaking()) {
            mTts.stopSpeaking();
        }
    }

    public void onClickNews(NewsCursor cursor) {
        Intent intent = new Intent(NewsViewActivity.this, NewsViewActivity.class);
        intent.putExtra("meta_info", cursor.getNewsMetaInfo());
        startActivityForResult(intent, NEWS_VIEW_ACTIVITY);
    }
}
