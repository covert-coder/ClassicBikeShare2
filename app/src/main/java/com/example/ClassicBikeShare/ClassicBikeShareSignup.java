package com.example.ClassicBikeShare;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.parse.ParseUser;

import java.util.Objects;

public class ClassicBikeShareSignup extends AppCompatActivity implements View.OnClickListener {
    private EditText mPasswordNew;
    private EditText mUserNameNew;
    private EditText mEmailNew;
    private Button mSignUpBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classicbikeshare_signup);

        ParseUser.getCurrentUser();
        ParseUser.logOut();
        setTitle("Sign Up for Classic Bike Share"); // sets the title in the action bar for this main page

        // logout any user that is logged in
        if (ParseUser.getCurrentUser() != null) {

            transitionToSocialMediaActivity();
        }
        // assign edit texts to variables
        mUserNameNew = findViewById(R.id.newUserName);
        mEmailNew = findViewById(R.id.newUserEmail);
        mPasswordNew = findViewById(R.id.newUserPass);
        mSignUpBtn = findViewById(R.id.btnSignup);
        Button mLoginBtn = findViewById(R.id.btnSwitchToLogin);

        // assigning the two buttons to the implemented onClickListener
        mSignUpBtn.setOnClickListener(this);
        mLoginBtn.setOnClickListener(this);

        // allowing the user to enter their data using the enter key by setting an
        // onKeyListener that looks for the enter key click and press down
        // set to the password field because it is the last one filled by the user (last in sequence on screen)
        mPasswordNew.setOnKeyListener((view, keyCode, event) -> {
            if(keyCode==KeyEvent.KEYCODE_ENTER && event.getAction()==KeyEvent.ACTION_DOWN){

                onClick(mSignUpBtn); // calls the onClick assigned to the signUP button as though it was clicked
            }
            return false;
        });
    }
    // onClick implemented by main class
    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            // the login button is pressed sending user to the login screen
            case R.id.btnSwitchToLogin:
                Intent intentLogin = new Intent(ClassicBikeShareSignup.this,
                        ClassicBikeShareLogin.class);
                startActivity(intentLogin);
                break;
            // signup button is pressed, sending data to the parse server
            // and generating a progress dialog
            // plus error checking
            case R.id.btnSignup:

                final ParseUser appUser = new ParseUser();
                appUser.setUsername(mUserNameNew.getText().toString());
                appUser.setPassword(mPasswordNew.getText().toString());
                appUser.setEmail(mEmailNew.getText().toString());

                //create a progress dialog to notify the user that their signup is progressing
                final ProgressDialog signUpDialog = new ProgressDialog(ClassicBikeShareSignup.this);
                signUpDialog.setTitle("Working");
                signUpDialog.setMessage(mUserNameNew.getText().toString() + " Your signUp is in progress");
                signUpDialog.show();

                // check for an empty field in password, username, email address
                if (mEmailNew.getText().toString().equals("") || mPasswordNew.getText().toString().equals("")
                        || mPasswordNew.getText().toString().equals("")) {
                    Toast.makeText(ClassicBikeShareSignup.this, "email address, password, " +
                            "and username must be provided", Toast.LENGTH_LONG).show();
                    signUpDialog.dismiss(); // signup is not occurring so..
                }
                // but.., if all fields have been filled, then
                else{
                        appUser.signUpInBackground(e -> {
                            if (e == null) {
                                Toast.makeText(ClassicBikeShareSignup.this, "your password and login were " +
                                        "set successfully;", Toast.LENGTH_SHORT).show();
                                signUpDialog.dismiss();
                                // possibility of server error since e is not null
                                transitionToSocialMediaActivity();
                            } else {
                                Toast.makeText(ClassicBikeShareSignup.this, "your password and login were " +
                                        "not successful;" + e.getMessage(), Toast.LENGTH_LONG).show();
                                signUpDialog.dismiss();
                            }
                        });
                        break; // break from case R.id.btnSignupInstag
                    }
                } // end of switch stmt
        } // end of implemented onClick containing switch stmts

    // the method below is an onClickListener for the constraint layout of the main activity page
    // clicking anywhere on the page (other than established UI's) triggers the method
    public void rootLayoutTapped(View layoutView) {
        // encapsulate in a try / catch to catch the error generated when the user taps the screen and the
        // keyboard is not displayed.  This causes a crash without this code.
        try {
            // access the inputMethodManager
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            // use the manager to hide the current focus (the keyboard) by getting its token
            assert inputMethodManager != null;
            inputMethodManager.hideSoftInputFromWindow(Objects.requireNonNull(getCurrentFocus()).getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace(); // sends some info to the stack regarding the error generated instead of causing a crash
        }
    }
    // this method, takes us to the social media page when login is complete
    private void transitionToSocialMediaActivity(){
           Intent intentSocialActivity = new Intent(ClassicBikeShareSignup.this, SocialMediaActivity.class);
           startActivity(intentSocialActivity);
           finish();
    }
} // end of main class




