package com.marqur.android;


import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 *  StartUp Activity - Handles startup stuff and hosts the viewpager that connects all the fragments
 */
public class StartUp extends AppCompatActivity {

    private static final int AUTH_REQUEST_CODE = 240;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // If not logged into Firebase,
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {

            // Launch Authentication activity
            Intent intent = new Intent(this, AuthActivity.class);
            startActivityForResult(intent,AUTH_REQUEST_CODE);

        } else {

            // Launch Main activity
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);

            finish();
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Check which request we're responding to
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

        finish();
    }
}
