package com.maoyuchaxue.catfishclubnewsapp.activities;

import android.content.Intent;
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
import com.maoyuchaxue.catfishclubnewsapp.data.NewsMetaInfo;
import com.maoyuchaxue.catfishclubnewsapp.data.db.CacheDBOpenHelper;
import com.maoyuchaxue.catfishclubnewsapp.fragments.NewsViewFragment;

import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SynthesizerListener;
import com.iflytek.cloud.SpeechError;

import cn.sharesdk.onekeyshare.OnekeyShare;

public class NewsViewActivity extends AppCompatActivity implements View.OnClickListener {

    private SpeechSynthesizer mTts;
    private NewsViewFragment newsViewFragment;

    private BookmarkManager bookmarkManager;
    private Toolbar toolbar;
    private boolean isInBookmark;

    private void initSynthesizer() {
        SpeechUtility.createUtility(NewsViewActivity.this, SpeechConstant.APPID +"=59b0c3fb");

        mTts = SpeechSynthesizer.createSynthesizer(NewsViewActivity.this, ini);
        mTts.setParameter(SpeechConstant.VOICE_NAME, "xiaoyan");//设置发音人
        mTts.setParameter(SpeechConstant.SPEED, "50");//设置语速
        mTts.setParameter(SpeechConstant.VOLUME, "80");//设置音量，范围0~100
        mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD); //设置云端
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initSynthesizer();

        setContentView(R.layout.activity_news_view);

        Intent intent = getIntent();
        NewsMetaInfo metaInfo = (NewsMetaInfo)intent.getSerializableExtra("meta_info");

        newsViewFragment = NewsViewFragment.newInstance(mTts, metaInfo);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        transaction.replace(R.id.news_view, newsViewFragment);
        transaction.commit();

        bookmarkManager = BookmarkManager.getInstance(CacheDBOpenHelper.getInstance(getApplicationContext()));
        isInBookmark = bookmarkManager.isBookmarked(metaInfo.getId());

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
                        //分享界面：
                        //需要：
                        //获得需要分享的链接，放到url里面
                        //获得文章简介，放到setText里面

                        OnekeyShare oks = new OnekeyShare();
                        //关闭sso授权
                        oks.disableSSOWhenAuthorize();

                        // 分享时Notification的图标和文字  2.5.9以后的版本不     调用此方法
                        //oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
                        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
                        oks.setTitle(getString(R.string.app_name));
                        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
                        oks.setTitleUrl("http://sharesdk.cn");
                        // text是分享文本，所有平台都需要这个字段
                        oks.setText("分享新闻");
                        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
                        //oks.setImagePath("storage/sdcard0/Download/15-26-52-u=3242651622,884328141&fm=27&gp=0.jpg");//确保SDcard下面存在此张图片
                        oks.setImageUrl("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1504852209254&di=a0f05c8d58eb5cefdee04ac0fc0e4269&imgtype=0&src=http%3A%2F%2Fpic.58pic.com%2F58pic%2F16%2F42%2F96%2F56e58PICAu9_1024.jpg");
                        // url仅在微信（包括好友和朋友圈）中使用
                        oks.setUrl("http://sharesdk.cn");
                        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
                        oks.setComment("输入评论：");
                        // site是分享此内容的网站名称，仅在QQ空间使用
                        oks.setSite(getString(R.string.app_name));
                        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
                        oks.setSiteUrl("http://sharesdk.cn");

                        // 启动分享GUI
                        oks.show(NewsViewActivity.this);

//                        TODO: implement intent to share URL here
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
//                            TODO: should set isInBookmark = false, when DB implemented
                        } else {
                            newsViewFragment.addCurrentNewsToBookmark(bookmarkManager);
                            isInBookmark = true;

                        }

                        resetMenuItemAppearance();
                        break;
                }
                return true;

            }
        });


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
}
