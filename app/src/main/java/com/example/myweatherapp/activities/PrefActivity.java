package com.example.myweatherapp.activities;

import android.preference.PreferenceActivity;

import com.example.myweatherapp.R;
import com.example.myweatherapp.fragments.PrefFragment;

import java.util.List;

public class PrefActivity extends PreferenceActivity
{
    @Override
    public void onBuildHeaders(List<Header> target)
    {
        loadHeadersFromResource(R.xml.headers_preference, target);
    }

    @Override
    protected boolean isValidFragment(String fragmentName)
    {
        return PrefFragment.class.getName().equals(fragmentName);
    }
}