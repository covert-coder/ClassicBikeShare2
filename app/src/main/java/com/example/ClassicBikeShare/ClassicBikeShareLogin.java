package com.example.ClassicBikeShare;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;



import java.util.Objects;

public class ClassicBikeShareLogin extends AppCompatActivity {

    private EditText txtEmailOnfile;
    private EditText txtPassOnfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classicbikeshare_login);

        setTitle("Login to Classic Bike Share");


        txtEmailOnfile = findViewById(R.id.newUserEmail);
        txtPassOnfile = findViewById(R.id.newUserPass);

        // the login button is pushed,
        findViewById(R.id.btnSignup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // user name, as entered, and password, as entered are sent to parse server for match
                // parse server will produce error if either username or password do not match records (e),then e != null
                // using Back4.com for parse server, lack of either username or password generates built-in error, e
                ParseUser.logInInBackground(txtEmailOnfile.getText().toString(), txtPassOnfile.getText().toString(), new LogInCallback() {
                            @Override
                            public void done(ParseUser user, ParseException e) {
                                if(user!=null && e==null){
                                    Toast.makeText(ClassicBikeShareLogin.this, "your login was successful "+user.get("username")
                                            , Toast.LENGTH_SHORT).show();
                                transitionToSocialMediaActivity();
                                }else{
                                    Toast.makeText(ClassicBikeShareLogin.this, "your login failed: "+ e.getMessage(),
                                            Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            }
        });

        findViewById(R.id.btnSwitchToLogin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentSignUp = new Intent(ClassicBikeShareLogin.this,
                        ClassicBikeShareSignup.class);
                startActivity(intentSignUp);

            }
        });
    }
    // the method below is an onClickListener for the constraint layout of the login page
    // clicking anywhere on the page (other than established UI's) triggers the method
    public void rootLayoutLoginPageTapped(View layoutView){
        try{
        // access the inputmethodmanager
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        // use the manager to hide the current focus (the keyboard) by getting its token
            assert inputMethodManager != null;
            inputMethodManager.hideSoftInputFromWindow(Objects.requireNonNull(getCurrentFocus()).getWindowToken(), 0);
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
    // this method, takes us to the social media page when signup is complete
    private void transitionToSocialMediaActivity(){
        Intent intentSocialActivity = new Intent(ClassicBikeShareLogin.this, SocialMediaActivity.class);
        startActivity(intentSocialActivity);
        finish();
    }
}
