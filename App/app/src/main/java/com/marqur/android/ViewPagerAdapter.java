package com.marqur.android;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;


public class ViewPagerAdapter extends FragmentPagerAdapter {

    private static final int NUM_PAGES = 3;
    private String pageTitles[] = new String[] { "Discover", "Browse", "Explore" };
    private Context context;

    public ViewPagerAdapter(@NonNull FragmentManager fragmentManager) {
        super(fragmentManager);
        this.context = context;
    }


    @Override
    public int getCount() {
        return NUM_PAGES;
    }


    // Returns the fragment to display for that page
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return ARActivity.newInstance();
            case 1:
                return FeedActivity.newInstance();
            case 2:
                return MapsActivity.newInstance();
            default:
                return null;
        }
    }

    // Returns the page title for the top indicator
    @Override
    public CharSequence getPageTitle(int position) {
        return pageTitles[position];
    }

}
