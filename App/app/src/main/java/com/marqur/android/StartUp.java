package com.marqur.android;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;



/**
 *  StartUp Activity - Handles app startup
 */
public class StartUp extends AppCompatActivity {


    private static final int AUTH_REQUEST_CODE = 240;



    /**
     * Runs when activity created
     * @param savedInstanceState - For restoring previous sessions
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTheme( R.style.Marqur_NoActionBar);

        // If not logged into Firebase,
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {

            // Launch Authentication activity
            Intent intent = new Intent(this, AuthActivity.class);
            startActivityForResult(intent,AUTH_REQUEST_CODE);

        } else {

            // Launch Main activity
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);

            // Close Startup
            finish();
        }
    }



    /**
     * Handles results from Activities launched
     * @param requestCode - Result of which operation?
     * @param resultCode - Type of result
     * @param data - Extra result data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // If Auth activity over, launch Main Activity
        if (requestCode == AUTH_REQUEST_CODE) {
            onBackPressed();
        }
    }



    /**
     * Runs when device back button is pressed
     */
    @Override
    public void onBackPressed() {

        // Launch Main activity
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);

        // Close Startup
        finish();
    }
}
