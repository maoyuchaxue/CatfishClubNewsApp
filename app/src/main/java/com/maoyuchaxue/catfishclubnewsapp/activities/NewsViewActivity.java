package com.maoyuchaxue.catfishclubnewsapp.activities;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.maoyuchaxue.catfishclubnewsapp.R;
import com.maoyuchaxue.catfishclubnewsapp.data.NewsMetaInfo;
import com.maoyuchaxue.catfishclubnewsapp.fragments.NewsViewFragment;

import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SynthesizerListener;
import com.iflytek.cloud.SpeechError;

import cn.sharesdk.onekeyshare.OnekeyShare;

public class NewsViewActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_view);

        SpeechUtility.createUtility(NewsViewActivity.this, SpeechConstant.APPID +"=59b0c3fb");

        Intent intent = getIntent();
        NewsMetaInfo metaInfo = (NewsMetaInfo)intent.getSerializableExtra("meta_info");
//        String newsID = intent.getExtras().getString("id");
//        String title = intent.getExtras().getString("title");
//        String newsID = metaInfo.getId();
//        String title = metaInfo.getTitle();

//        Fragment newFragment = NewsViewFragment.newInstance(newsID, title);
        Fragment newFragment = NewsViewFragment.newInstance(metaInfo);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        transaction.replace(R.id.news_view, newFragment);
        transaction.commit();

        Toolbar toolbar = (Toolbar)findViewById(R.id.news_view_menu_toolbar);
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
                        //开始语音
                        //需要文章正文+文章内容，用String传入

                        SpeechSynthesizer mTts= SpeechSynthesizer.createSynthesizer(NewsViewActivity.this, ini);

                        mTts.setParameter(SpeechConstant.VOICE_NAME, "xiaoyan");//设置发音人
                        mTts.setParameter(SpeechConstant.SPEED, "50");//设置语速
                        mTts.setParameter(SpeechConstant.VOLUME, "80");//设置音量，范围0~100
                        mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD); //设置云端
                        //设置合成音频保存位置（可自定义保存位置），保存在“./sdcard/iflytek.pcm”
                        //保存在SD卡需要在AndroidManifest.xml添加写SD卡权限

                        String speaking = "大家好，我很皮诶。我买了一台iPhone 7。曾经，曾子My name is Van, I am an artist, a performance artist。";
                        mTts.startSpeaking(speaking, mSynListener);
//                        TODO: implement voice here
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

    private SynthesizerListener mSynListener = new SynthesizerListener() {
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
    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.news_view_menu, menu);
        return true;
    }

    @Override
    public void onClick(View view) {

        NewsViewActivity.this.finish();
    }
}
