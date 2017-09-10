package com.maoyuchaxue.catfishclubnewsapp.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.SwitchPreference;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.content.Intent;
import android.support.v7.app.AppCompatDelegate;

import com.maoyuchaxue.catfishclubnewsapp.R;
import com.maoyuchaxue.catfishclubnewsapp.activities.BookmarkListActivity;
import com.maoyuchaxue.catfishclubnewsapp.activities.MainActivity;
import com.maoyuchaxue.catfishclubnewsapp.data.BookmarkManager;

import java.util.Set;

/**
 * Created by catfish on 17/9/10.
 */

public class SettingsFragment extends PreferenceFragment {

    private Preference darkModePreference;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_general);
        darkModePreference = getPreferenceManager().findPreference("dark_style");
        darkModePreference.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                Boolean result = (Boolean) o;
                if (result) {
                    if(AppCompatDelegate.getDefaultNightMode() == -1 ||
                            AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_NO)  {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                        getActivity().recreate();
                    }
                } else {
                    if(AppCompatDelegate.getDefaultNightMode() == -1 ||
                            AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES)  {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                        getActivity().recreate();
                    }
                }
                return true;
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        return view;
    }


}
