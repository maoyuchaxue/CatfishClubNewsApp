package com.maoyuchaxue.catfishclubnewsapp.activities;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.maoyuchaxue.catfishclubnewsapp.R;
import com.maoyuchaxue.catfishclubnewsapp.fragments.NewsViewFragment;

public class NewsViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_view);

        Intent intent = getIntent();
        String newsID = intent.getExtras().getString("id");

        Fragment newFragment = NewsViewFragment.newInstance(newsID);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        transaction.replace(R.id.news_view, newFragment);
        transaction.commit();
    }
}
