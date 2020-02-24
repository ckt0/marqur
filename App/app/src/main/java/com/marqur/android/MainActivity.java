package com.marqur.android;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

// MainActivity - Handles startup stuff and hosts the viewpager that connects all the fragments
public class MainActivity extends AppCompatActivity {

    TabLayout navBar;
    ViewPager2 viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        setContentView(R.layout.activity_viewpager);
        viewPager = findViewById(R.id.view_pager);
        navBar = findViewById(R.id.navBar);
        viewPager.setAdapter(createCardAdapter());
        new TabLayoutMediator(navBar, viewPager, (tab, position) -> tab.setText("Tab " + (position + 1))).attach();
    }

    private ViewPagerAdapter createCardAdapter() {
        return new ViewPagerAdapter(this);
    }
}