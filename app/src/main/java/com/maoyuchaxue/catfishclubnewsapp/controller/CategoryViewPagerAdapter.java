package com.maoyuchaxue.catfishclubnewsapp.controller;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.lang.reflect.Method;
import java.util.List;

/**
 * Created by catfish on 17/9/6.
 */

public class CategoryViewPagerAdapter extends FragmentPagerAdapter {
    private boolean[] flags;

    private List<Fragment> mFragments;
    private List<String> mTitles;
    private FragmentManager fm;

    public CategoryViewPagerAdapter(FragmentManager fm) {
        super(fm);
        mFragments = null;
        mTitles = null;
        this.fm = fm;
    }

    public void resetWithData(List<Fragment> fragments, List<String> titles) {
        mFragments = fragments;
        if (fragments != null) {
            flags = new boolean[fragments.size()];
            for (int i = 0; i < fragments.size(); i++) {
                flags[i] = true;
            }
        }

        mTitles = titles;
        this.notifyDataSetChanged();
    }


    @Override
    public Fragment getItem(int position) {
        if (mFragments != null) {
            return mFragments.get(position);
        } else {
            return null;
        }
    }

    @Override
    public int getItemPosition(Object object) {
        return PagerAdapter.POSITION_NONE;
    }

    @Override
    public int getCount() {
        if (mFragments != null) {
            return mFragments.size();
        } else {
            return 0;
        }
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        String tag = getFragmentTag(container.getId(), position);
        Fragment fragment = fm.findFragmentByTag(tag);

        if (fragment != null) {
            FragmentTransaction ft = fm.beginTransaction();
            ft.remove(fragment);
            ft.commitAllowingStateLoss();
            fm.executePendingTransactions();
        }

        return super.instantiateItem(container, position);

    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (mTitles != null) {
            return mTitles.get(position);
        } else {
            return null;
        }
    }

    private String getFragmentTag(int viewId, int index) {
        try {
            Class<FragmentPagerAdapter> cls = FragmentPagerAdapter.class;
            Class<?>[] parameterTypes = { int.class, long.class };
            Method method = cls.getDeclaredMethod("makeFragmentName",
                    parameterTypes);
            method.setAccessible(true);
            String tag = (String) method.invoke(this, viewId, index);
            return tag;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }


}
