package com.java.team17.fragments;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.Preference.OnPreferenceChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v7.app.AppCompatDelegate;

import com.java.team17.R;

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
