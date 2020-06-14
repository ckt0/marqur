package com.marqur.android;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.android.gms.common.SignInButton;




public class LoginFragment extends Fragment {

    private EditText editTextEmail;
    private EditText editTextPassword;
    private Button buttonLogin;
    private TextView textViewRegister;
    private SignInButton buttonGoogleSignIn;


    /**
     * Runs while fragment view is being created. Inflates fragment.
     * @param inflater - Thing that draws layout views
     * @param container - The parent view in which new layout is inflated (drawn)
     * @param savedInstanceState - Saved instance data
     * @return The newly inflated view
     */
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.page_login, container, false);
    }


    /**
     * Runs after fragment view is loaded
     * @param view - Created view
     * @param savedInstanceState - Saved instance data
     */
    public void onViewCreated( View view, Bundle savedInstanceState) {

        // Fetching views
        editTextEmail = requireView().findViewById(R.id.editTextEmail);
        editTextPassword = requireView().findViewById(R.id.editTextPassword);
        textViewRegister = requireView().findViewById(R.id.textViewRegister);
        buttonLogin = requireView().findViewById(R.id.buttonLogin);
        buttonGoogleSignIn = requireView().findViewById(R.id.google_sign_in_button);

        // Attaching listeners to views
        buttonLogin.setOnClickListener(new AuthClickListener());
        textViewRegister.setOnClickListener(new AuthClickListener());
        buttonGoogleSignIn.setOnClickListener(new AuthClickListener());

    }


    /**
     * Runs after fragment is loaded completely
     * @param savedInstanceState - Saved instance data
     */
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }


    /**
     * Creates and returns the Login fragment
     * @return the Login Fragment
     */
    static LoginFragment newInstance() {
        LoginFragment loginFragment = new LoginFragment();
        Bundle args = new Bundle();
        loginFragment.setArguments(args);
        return loginFragment;
    }

    /**
     * Click listener for entire view
     */
    private class AuthClickListener implements View.OnClickListener {
        public void onClick(View view){

            // If Sign-Up button was clicked, register user
            if (view == buttonLogin) {

                // Get email and password from edit texts
                String email = editTextEmail.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();

                // Check if email field is empty
                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(requireActivity(), "Please enter email", Toast.LENGTH_LONG).show();
                    return;
                }

                // Check if password field is empty
                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(requireActivity(), "Please enter password", Toast.LENGTH_LONG).show();
                    return;
                }

                // Display a progress dialog- the circle-loading thingy
                ProgressDialog progressDialog = new ProgressDialog(requireActivity());
                progressDialog.setMessage("Logging in, Please Wait...");
                progressDialog.show();

                ((AuthActivity)requireActivity()).loginUser(email,password);

                // Close the loading thingy
                progressDialog.dismiss();
            }

            // If 'already registered' textview was clicked, switch to login activity
            if (view == textViewRegister) {
                ((AuthActivity)requireActivity()).setPage(0);
            }

            // Launch Google Sign-In Intent when Google Sign-In button clicked
            if (view == buttonGoogleSignIn) {

                // Display a progress dialog- the circle-loading thingy
                ProgressDialog progressDialog = new ProgressDialog(requireActivity());
                progressDialog.setMessage("Logging in, Please Wait...");
                progressDialog.show();

                ((AuthActivity)requireActivity()).inititiateGoogleSignIn();

                // Close the loading thingy
                progressDialog.dismiss();
            }
        }
    }
}
