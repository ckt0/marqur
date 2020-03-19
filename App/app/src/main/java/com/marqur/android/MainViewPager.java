package com.marqur.android;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import org.jetbrains.annotations.NotNull;


/**
 * Adapter for the main ViewPager that holds all the other fragments.
 * You can add/remove pages or change their titles here.
 */
public class MainViewPager extends FragmentPagerAdapter {

    private static final int NUM_PAGES = 3;
    private final Fragment[] mFragmentList = {LensFragment.newInstance(), ReaderFragment.newInstance(), MapFragment.newInstance()};

    /**
     * Constructs an adapter for the main ViewPager
     * @param fragmentManager - The thing that handles fragments in the ViewPager
     */
    MainViewPager(@NonNull FragmentManager fragmentManager) {
        super(fragmentManager,BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
    }


    /**
     * Returns the fragment at position specified
     * @param position - The fragment position
     * @return The fragment at that position
     */
    @NotNull
    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                mFragmentList[0]= LensFragment.newInstance();
                break;
            case 1:
                mFragmentList[1]=ReaderFragment.newInstance();
                break;
            default:
                mFragmentList[2]=MapFragment.newInstance();
        }
        return mFragmentList[position];
    }


    /**
     * Used by ViewPager for... stuff
     * @return Number of Pages
     */
    @Override
    public int getCount() {
        return NUM_PAGES;
    }


    /**
     * Used by Tab-Strip to name the tabs
     * @param position - The fragment position
     * @return The title of requested fragment
     */
    @Override
    public CharSequence getPageTitle(int position) {
        return "";
    }

    public Fragment getExistingFragment(int position) { return mFragmentList[position]; }
}
