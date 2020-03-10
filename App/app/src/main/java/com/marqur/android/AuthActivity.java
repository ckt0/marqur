package com.marqur.android;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

/**
 * Authentication activity that registers/signs-in user
 */
public class AuthActivity extends AppCompatActivity {

    private PagerAdapter pagerAdapter;
    private TabLayout navBar;
    private ViewPager viewPager;
    private ProgressBar progressBar;
    private static final String TAG = "Authentication";

    FirebaseAuth firebaseAuth;
    GoogleSignInClient mGoogleSignInClient;

    private static final int GOOGLE_SIGN_IN_REQUEST = 234;



    /**
     * Runs when activity created
     * @param savedInstanceState - Saved instance data
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize Firebase Authentication object
        firebaseAuth = FirebaseAuth.getInstance();

        // If user already logged in, exit activity
        if (firebaseAuth.getCurrentUser() != null) {
            Intent intent = getIntent();
            intent.putExtra("user", firebaseAuth.getCurrentUser());
            setResult(RESULT_OK, intent);
            finish();
        }

        // Inflates the authentication view
        setContentView(R.layout.activity_auth);

        // Fetch view elements
        viewPager = findViewById(R.id.view_pager);
        navBar = findViewById(R.id.navBar);
        progressBar=findViewById(R.id.progressBar);
        // Create and attach ViewPager adapter using the FragmentManager that came with this Activity
        pagerAdapter = new AuthViewPager(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);

        // Integrates tab layout with ViewPager
        navBar.setupWithViewPager(viewPager);

        // Create the Google Sign-In Request
        GoogleSignInOptions mGoogleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // Holds Google Signed-In Client data
        mGoogleSignInClient = GoogleSignIn.getClient(this, mGoogleSignInOptions);

        // Kevin's stupid transition that wastes 1000ms!! Im gonna change it to 300ms...
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
    }


    /**
     * Runs when device back button is pressed
     */
    @Override
    public void onBackPressed() {

        // If not on first authentication page, go back to that. Else, exit activity
        if (viewPager.getCurrentItem() != 0) viewPager.setCurrentItem(0);
        else super.onBackPressed();
    }


    /**
     * Registers user with Marqur
     */
    public void registerUser(String email, String password, String username) {

        // Create a new user and run listener after completion
        progressBar.setVisibility(View.VISIBLE);
        navBar.setVisibility(View.GONE);
        viewPager.setVisibility(View.GONE);
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {

                        // If sign-up was successful
                        if (task.isSuccessful()) {

                            // Initialise user variable with logged-in user
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            assert user != null;
                            progressBar.setVisibility(View.GONE);
                            Intent intent = getIntent();
                            intent.putExtra("user", user);
                            setResult(RESULT_OK, intent);
                            finish();

                        } else {
                            // If sign-up fails, alert user with Toast
                            Toast.makeText(AuthActivity.this,
                                    "Registration Failed, Please retry...", Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);
                            navBar.setVisibility(View.VISIBLE);
                            viewPager.setVisibility(View.VISIBLE);
                        }

                });
    }


    /**
     * Signs-in user to Marqur
     */
    public void loginUser(String email, String password) {

        // Log-in user and run listener after completion
        progressBar.setVisibility(View.VISIBLE);
        navBar.setVisibility(View.GONE);
        viewPager.setVisibility(View.GONE);
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {

                    // If sign-in was successful, exit activity
                    if (task.isSuccessful()) {
                        progressBar.setVisibility(View.GONE);
                        Intent intent = getIntent();
                        intent.putExtra("user", firebaseAuth.getCurrentUser());
                        setResult(RESULT_OK, intent);
                        finish();
                    } else {
                        progressBar.setVisibility(View.GONE);
                        navBar.setVisibility(View.VISIBLE);
                        viewPager.setVisibility(View.VISIBLE);
                        // Sign-In failed, alert user
                        Toast.makeText(this, "Login Failed! Please retry...", Toast.LENGTH_LONG).show();
                    }
                });

    }


    /**
     * Runs when the Sign-In button is pressed, launches Google Sign-In
     */
    public void inititiateGoogleSignIn() {
        startActivityForResult(mGoogleSignInClient. getSignInIntent(), GOOGLE_SIGN_IN_REQUEST);
    }


    /**
     * Integrates Google Sign-In data with our Firebase Authentication
     * @param googleSignInAccount - Hold Google Sign-In account data
     */
    private void firebaseAuthWithGoogle(GoogleSignInAccount googleSignInAccount) {

        // #justdebugthings
        Log.d(TAG, "firebaseAuthWithGoogle:" + googleSignInAccount.getId());

        // Gets Authentication Credentials (instead of password)
        AuthCredential credential = GoogleAuthProvider.getCredential(googleSignInAccount.getIdToken(), null);

        //Now using firebase we are signing in the user here
        progressBar.setVisibility(View.VISIBLE);
        navBar.setVisibility(View.GONE);
        viewPager.setVisibility(View.GONE);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {

                    if (task.isSuccessful()) {

                        // #justdebugthings

                        Log.d(TAG, "signInWithCredential:success");

                        // Authentication successful, alert user
                        Toast.makeText(getApplicationContext(), "Google Sign-In Successful!", Toast.LENGTH_SHORT).show();

                        // Exit activity
                        if(firebaseAuth.getCurrentUser() != null) {
                            progressBar.setVisibility(View.GONE);
                            Intent intent = getIntent();
                            intent.putExtra("user", firebaseAuth.getCurrentUser());
                            setResult(RESULT_OK, intent);
                            finish();
                        }

                    } else {

                        // #justdebugthings
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        progressBar.setVisibility(View.GONE);
                        navBar.setVisibility(View.VISIBLE);
                        viewPager.setVisibility(View.VISIBLE);
                        // Authentication failed, alert user
                        Toast.makeText(getApplicationContext(), "Google Sign-In Failed!",
                                Toast.LENGTH_SHORT).show();

                    }

                });
    }


    /**
     * Switches ViewPager pages programmatically
     */
    public void setPage(int position){
        viewPager.setCurrentItem(position);
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

        // If request-code matches the one we specified for Google Sign-In,
        if (requestCode == GOOGLE_SIGN_IN_REQUEST) {

            // Check Google Sign-In task results
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {

                // Works only if Google Sign-In task was executed successfully, else raises exception
                GoogleSignInAccount account = task.getResult(ApiException.class);
                assert account != null;

                // Use Google Sign-In data to continue with Firebase authentication
                firebaseAuthWithGoogle(account);

            } catch (ApiException e) {

                // Google Sign-In task failed, alert user
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

}


