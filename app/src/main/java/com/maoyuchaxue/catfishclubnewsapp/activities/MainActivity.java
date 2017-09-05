package com.maoyuchaxue.catfishclubnewsapp.activities;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.maoyuchaxue.catfishclubnewsapp.R;
import com.maoyuchaxue.catfishclubnewsapp.fragments.NewsListFragment;

public class MainActivity extends AppCompatActivity implements NewsListFragment.OnFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Fragment newFragment = NewsListFragment.newInstance(-1, "", false);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        transaction.replace(R.id.news_info_list, newFragment);
        transaction.commit();
    }

    @Override
    public void onFragmentInteraction(String newsID) {
        Intent intent = new Intent(MainActivity.this, NewsViewActivity.class);
        intent.putExtra("id", newsID);
        startActivity(intent);
    }
}
