package com.example.myweatherapp.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.myweatherapp.fragments.ExampleFragment;

public class TabsPagerFragmentAdapter extends FragmentPagerAdapter {

    public TabsPagerFragmentAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return "Daily Forecast";
    }

    @Override
    public Fragment getItem(int position) {
        return ExampleFragment.getInstance();
    }

    @Override
    public int getCount() {
        return 1;
    }
}