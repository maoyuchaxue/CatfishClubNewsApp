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
//                        TODO: implement intent to share URL here
                        break;
                    case R.id.news_view_menu_voice:
                        //开始语音
                        //1.创建SpeechSynthesizer对象, 第二个参数：本地合成时传InitListener

                        SpeechSynthesizer mTts= SpeechSynthesizer.createSynthesizer(NewsViewActivity.this, ini);
//2.合成参数设置，详见《科大讯飞MSC API手册(Android)》SpeechSynthesizer 类
                        mTts.setParameter(SpeechConstant.VOICE_NAME, "xiaoyan");//设置发音人
                        mTts.setParameter(SpeechConstant.SPEED, "50");//设置语速
                        mTts.setParameter(SpeechConstant.VOLUME, "80");//设置音量，范围0~100
                        mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD); //设置云端
//设置合成音频保存位置（可自定义保存位置），保存在“./sdcard/iflytek.pcm”
//保存在SD卡需要在AndroidManifest.xml添加写SD卡权限
//如果不需要保存合成音频，注释该行代码
                        //mTts.setParameter(SpeechConstant.TTS_AUDIO_PATH, "./sdcard/iflytek.pcm");
//3.开始合成
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
