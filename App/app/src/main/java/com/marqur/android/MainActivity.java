package com.marqur.android;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

// MainActivity - Handles startup stuff and hosts the viewpager that connects all the fragments
public class MainActivity extends FragmentActivity {

    private TabLayout navBar;
    private ViewPager viewPager;
    private PagerAdapter pagerAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        setContentView(R.layout.activity_viewpager);
        viewPager = findViewById(R.id.view_pager);
        navBar = findViewById(R.id.navBar);
        pagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        viewPager.setPageTransformer(true, new ZoomOutPageTransformer());
        navBar.setupWithViewPager(viewPager);
//        new TabLayoutMediator(navBar, viewPager, (tab, position) -> tab.setText("Tab " + (position + 1))).attach();

    }

    @Override
    public void onBackPressed() {
        if (viewPager.getCurrentItem() == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed();
        } else {
            // Otherwise, select the previous step.
            viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
        }
    }

}