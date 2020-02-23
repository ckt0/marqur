package com.marqur.android;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SignupActivity extends AppCompatActivity implements View.OnClickListener {


    private static final String TAG = "Signup";
    FirebaseUser user;
    //defining view objects
    private EditText editTextEmail;
    private EditText editTextPassword;
    private EditText editTextUsername;
    private Button buttonSignup;
    private TextView textViewSignin;
    private ProgressDialog progressDialog;
    private Users P_user;
    //defining firebaseauth object
    private FirebaseAuth firebaseAuth;
    //database reference
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        setContentView(R.layout.activity_main);

        //initializing firebase auth object
        firebaseAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();


        //initializing views
        editTextUsername = (EditText) findViewById(R.id.username);
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        textViewSignin = (TextView) findViewById(R.id.textViewSignin);

        buttonSignup = (Button) findViewById(R.id.buttonSignup);

        progressDialog = new ProgressDialog(this);

        //attaching listener to button
        buttonSignup.setOnClickListener(this);
        textViewSignin.setOnClickListener(this);
        //if getCurrentUser does not returns null
        user = firebaseAuth.getCurrentUser();
        if (user != null) {
            finish();

            //and open profile activity
            startActivity(new Intent(getApplicationContext(), MapsActivity.class));
            // User is signed in

        }
    }


    private void registerUser() {

        //getting email and password from edit texts
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        //checking if email and passwords are empty
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Please enter email", Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please enter password", Toast.LENGTH_LONG).show();
            return;
        }

        //if the email and password are not empty
        //displaying a progress dialog

        progressDialog.setMessage("Registering Please Wait...");
        progressDialog.show();

        //creating a new user
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //checking if success
                        if (task.isSuccessful()) {
                            user = firebaseAuth.getCurrentUser();
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(editTextUsername.getText().toString().trim()).build();

                            user.updateProfile(profileUpdates);

                            String date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
                            Users p_user = new Users(user.getUid(), editTextUsername.getText().toString().trim(), email, date, 0, 0, null, null);
                            mDatabase.child("users").child(user.getUid()).setValue(p_user);
                            finish();
                            startActivity(new Intent(getApplicationContext(), MapsActivity.class));
                        } else {
                            //display some message here
                            Toast.makeText(SignupActivity.this, "Registration Error", Toast.LENGTH_LONG).show();
                        }
                        progressDialog.dismiss();
                    }
                });


    }

    @Override
    public void onClick(View view) {

        if (view == buttonSignup) {
            registerUser();
        }

        if (view == textViewSignin) {
            //open login activity when user taps on the already registered textview
            startActivity(new Intent(this, MapsActivity.class));
        }

    }

}


