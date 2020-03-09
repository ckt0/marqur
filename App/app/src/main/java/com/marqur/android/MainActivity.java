package com.marqur.android;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;


/**
 *  MainActivity - Handles most of the work and hosts the viewpager that connects all the fragments
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "Main";

    private static final int PERMISSION_REQUEST_CODE = 200;
    private static final int AUTH_REQUEST_CODE = 250;

    private AppBarLayout appBar;
    private Toolbar mainToolbar;
    private MainViewPager pagerAdapter;
    private TabLayout navBar;
    private ViewPager viewPager;
    private Menu menu;
    private String[] pageTitles = {"Lens","Browse","Roam"};
    private int[] pageIcons = {R.drawable.ic_eye_white_24dp, R.drawable.ic_view_white_24dp, R.drawable.ic_map_white_24dp};

    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    public boolean isLoggedIn = false;
    public boolean isAppBarExpanded = false;
    public String appBarTitle = "Marqur";

    public FirebaseFirestore db;
    public CollectionReference markersCRef;
    public CollectionReference usersCRef;



    /**
     * Runs on activity creation
     * @param savedInstanceState - Saved instance data
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get Firebase Auth status
        mFirebaseAuth = FirebaseAuth.getInstance();

        // Setup an Authentication State Listener to monitor whether user signed into Firebase
        mAuthListener = firebaseAuth -> {

            checkAuth();
        };

        // Setup Firestore variables
        db = FirebaseFirestore.getInstance();
        markersCRef = db.collection("markers");
        usersCRef = db.collection("users");


        // Inflate (draw) the main ViewPager layout
        setContentView(R.layout.main_viewpager);

        setupView();

    }



    /**
     * Runs when Activity starts (after onCreate)
     */
    @Override
    protected void onStart() {
        super.onStart();
    }



    /**
     * Runs when activity resumes (when switched to foreground)
     */
    @Override
    protected void onResume() {
        super.onResume();

        // Keep Authentication Status synced with backend
        mFirebaseAuth.addAuthStateListener(mAuthListener);
    }



    /**
     * Runs when activity stops
     */
    @Override
    protected void onStop() {
        super.onStop();

        // Remove Authentication State listener
//        mFirebaseAuth.removeAuthStateListener(mAuthListener);
    }



    private void setupView() {

        // Fetch view elements
        viewPager = findViewById(R.id.view_pager);
        navBar = findViewById(R.id.navBar);
        appBar = findViewById(R.id.app_bar);
        mainToolbar = findViewById(R.id.toolbar);

        setupAppBar();

        // Create and attach ViewPager adapter using the FragmentManager that came with this Activity
        pagerAdapter = new MainViewPager(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);

        // Keeps all the pages awake when not visible
        viewPager.setOffscreenPageLimit(2);

        // Integrates tab layout with ViewPager
        navBar.setupWithViewPager(viewPager);

        // Manually set icons for each tab
        for(int i=0;i<3;i++) {
            Objects.requireNonNull(navBar.getTabAt(i)).setIcon(pageIcons[i]);
        }

        navBar.addOnTabSelectedListener(
                new TabLayout.ViewPagerOnTabSelectedListener(viewPager) {

                    @Override
                    public void onTabSelected(TabLayout.Tab tab) {
                        super.onTabSelected(tab);
                        int tabIconColor = ContextCompat.getColor(MainActivity.this, R.color.colorAccent);
                        Objects.requireNonNull(tab.getIcon()).setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
                        tab.setText(pageTitles[tab.getPosition()]);
                    }

                    @Override
                    public void onTabUnselected(TabLayout.Tab tab) {
                        super.onTabUnselected(tab);
                        int tabIconColor = ContextCompat.getColor(MainActivity.this, R.color.quantum_white_100);
                        Objects.requireNonNull(tab.getIcon()).setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
                        tab.setText("");
                    }

                    @Override
                    public void onTabReselected(TabLayout.Tab tab) {
                        super.onTabReselected(tab);
                    }
                }
        );


        // Set viewPager to page 1
        viewPager.setCurrentItem(1);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            public void onPageScrollStateChanged(int state) {
                if (state==ViewPager.SCROLL_STATE_IDLE)
                    if(viewPager.getCurrentItem() == 0)
                        ((LensFragment)pagerAdapter.getExistingFragment(0)).initialiseCamera(MainActivity.this);
            }
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            public void onPageSelected(int position) {
                if(position != 1) appBar.setExpanded(false, true);
//                if(position != 0) ((LensFragment)pagerAdapter.getItem(0)).onPause(MainActivity.this);
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.search_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }



    private void setupAppBar() {

        setSupportActionBar(mainToolbar);

        AppBarLayout mAppBarLayout = (AppBarLayout) findViewById(R.id.app_bar);
        mAppBarLayout.addOnOffsetChangedListener( (appBarLayout, verticalOffset) -> {

            if (Math.abs(verticalOffset) == appBarLayout.getTotalScrollRange()) {

                // AppBar Collapsed
                if(!isAppBarExpanded) {
                    isAppBarExpanded = true;
                    showOption(R.id.action_search);
//                    showMarquee();
                }
            } else if (verticalOffset == 0) {
                // AppBar Expanded
                if(isAppBarExpanded) {
                    isAppBarExpanded = false;
                    hideOption(R.id.action_search);

                }
            } else {
                // AppBar Partially Expanded
                hideOption(R.id.action_search);
            }

        });
    }




//    public void showMarquee(){
//
//        CoordinatorLayout mainView = findViewById(R.id.main_view);
//
//        TextView feedMarquee = new TextView(this);
//        feedMarquee.setLayoutParams(new CoordinatorLayout.LayoutParams(
//                CoordinatorLayout.LayoutParams.MATCH_PARENT,
//                CoordinatorLayout.LayoutParams.MATCH_PARENT));
//
//        LayoutInflater inflater = LayoutInflater.from(this);
//        View inflatedLayout= inflater.inflate(R.layout.feed_marquee, mainView,false);
//        mainView.addView(feedMarquee);
//
//        requireView().findViewById(R.id.feed_marquee).setVisibility(View.VISIBLE);
//        requireView().findViewById(R.id.feed_marquee).setSelected(true);
//
//    }



    public boolean checkAuth() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if(currentUser==null) isLoggedIn = false;
        else isLoggedIn = true;
        return isLoggedIn;
    }

    /**
     * Checks if specified permissions granted
     * @return Whether camera permission granted
     */
    public boolean checkPermission(String permission) {
        return (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED);
    }



    /**
     * Requests specified permission
     * @return Whether camera permission granted
     */
    public void requestPermission(String permission) {
        ActivityCompat.requestPermissions(this, new String[]{permission}, PERMISSION_REQUEST_CODE);
        Log.d(TAG, "Requesting permission "+permission+"....");
    }



    /**
     * Runs when permission request returns back with results
     * @param requestCode - Identifies which operation the result belongs to
     * @param permissions - List of permissions
     * @param grantResults - List of results
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NotNull String[] permissions, @NotNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {

            // If permission was granted,
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                // #justdebugthings
                Log.e(TAG, "Application was granted permission "+permissions[0]+" !");

                if ("android.permission.CAMERA".equals(permissions[0])) {
                    if(getPage()==0) {
                        ((LensFragment)pagerAdapter.getExistingFragment(0)).initialiseCamera(MainActivity.this);
                    }
                }

            } else {

                // #justdebugthings
                Log.e(TAG, "Application was denied permission "+permissions[0]+" !");

                // Safety Check (Only works above Marshmellow)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                    // If user had the audacity to deny permissions,
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                            != PackageManager.PERMISSION_GRANTED) {

                        // Set message to use in Dialog box below
                        String dialogMessage;
                        if ("android.permission.CAMERA".equals(permissions[0])) {
                            dialogMessage = "Marqur needs Camera permissions for AR functionality!";
                        }
                        dialogMessage = "Marqur needs this permission to work correctly!";

                        // Use a Dialog message to pester user further
                        new AlertDialog.Builder(this)
                                .setMessage(dialogMessage)
                                .setPositiveButton("Grant", (dialog, which) -> {
                                    if (which == DialogInterface.BUTTON_POSITIVE) {
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                            Log.d(TAG,"Re-requesting permission "+permissions[0]+"...");
                                            requestPermission(permissions[0]);
                                        }
                                    }
                                    else if (which == DialogInterface.BUTTON_NEGATIVE) {
                                        Log.e(TAG, "Application was denied "+permissions[0]+" AGAIN!");
                                        viewPager.setCurrentItem(1);
                                    }
                                })
                                .setNegativeButton("Cancel", null)
                                .create().show();
                    }
                }
            }
        }
    }



    /**
     * Switches ViewPager pages programmatically
     */
    public void setPage(int position){
        viewPager.setCurrentItem(position);
    }



    /**
     * Get ViewPager page position programmatically
     */
    public int getPage(){
        return viewPager.getCurrentItem();
    }



    /**
     * Runs when options menu created
     * @param menu - The menu object
     * @return Whether to display menu (True)
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;

        // Inflate the menu. Also adds items to the action bar if present.
        getMenuInflater().inflate(R.menu.main_options, menu);

        // Hide these from menu
        hideOption(R.id.action_search);
        hideOption(R.id.action_login);
        hideOption(R.id.action_logout);

        // If logged-in, show logout option... and vice-versa
        if(checkAuth()) showOption(R.id.action_logout);
        else showOption(R.id.action_login);

        // Display menu?
        return true;
    }



    /**
     * Runs when a options menu item is selected
     * @param item - Selected item
     * @return (System handled)
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Get selected item's resource id
        int id = item.getItemId();

        // If-else case matching for selected item...
        // Handle action bar item clicks here.
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.action_search) {
            return true;
        } else if (id == R.id.action_login) {
            Intent intent = new Intent(MainActivity.this, AuthActivity.class);
            startActivityForResult(intent,AUTH_REQUEST_CODE);
            return true;
        } else if (id == R.id.action_logout) {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(MainActivity.this, AuthActivity.class);
            startActivityForResult(intent,AUTH_REQUEST_CODE);
            return true;
        }

        // (System handled)
        return super.onOptionsItemSelected(item);
    }



    /**
     * Hides menu item specified
     * @param id - Id of item to be hidden
     */
    public void hideOption(int id) {
        MenuItem item = menu.findItem(id);
        item.setVisible(false);
    }



    /**
     * Reveals menu item specified
     * @param id - Id of item to be set visible
     */
    public void showOption(int id) {
        MenuItem item = menu.findItem(id);
        item.setVisible(true);
    }



    /**
     * Runs when a called Activity returns with a result
     * @param requestCode - Identifies which operation the result belongs to
     * @param resultCode - Identifies task result
     * @param data - The result
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // If request-code matches the one we specified for Auth,
        if (requestCode == AUTH_REQUEST_CODE) {

            if(resultCode==RESULT_OK)  isLoggedIn=true;
            else  isLoggedIn=false;

        }
    }



    /**
     * Runs when device back button is pressed
     */
    @Override
    public void onBackPressed() {

        // If Reader isn't selected, go back to that. Else, exit app
        if (viewPager.getCurrentItem() != 1) viewPager.setCurrentItem(1);
        else super.onBackPressed();
    }
}