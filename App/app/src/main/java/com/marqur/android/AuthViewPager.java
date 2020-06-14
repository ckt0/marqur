package com.marqur.android;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;




/**
 * Adapter for the Authentication ViewPager that holds the Register and Log-In fragments.
 * You can add/remove pages or change their titles here.
 */
public class AuthViewPager  extends FragmentPagerAdapter {

    private static final int NUM_PAGES = 2;
    private String[] pageTitles = new String[] { "Register", "Log In" };

    /**
     * Constructs an adapter for the Authentication ViewPager
     * @param fragmentManager - The thing that handles fragments in the ViewPager
     */
    AuthViewPager(@NonNull FragmentManager fragmentManager) {
        super(fragmentManager,BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
    }


    /**
     * Returns the fragment at position specified
     * @param position - The fragment position
     * @return The fragment at that position
     */

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return RegisterFragment.newInstance();
            default:
                return LoginFragment.newInstance();
        }
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
        return pageTitles[position];
    }

}
