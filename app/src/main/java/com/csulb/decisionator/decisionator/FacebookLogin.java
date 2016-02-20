package com.csulb.decisionator.decisionator;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.csulb.decisionator.decisionator.backend.UserEndpoint;
import com.csulb.decisionator.decisionator.backend.userApi.model.User;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.logging.LogManager;

public class FacebookLogin extends AppCompatActivity {

    //Keys for the intent
    protected final static String USER_F_NAME = "com.csulb.decisionator.USER_F_NAME";
    protected final static String USER_ID = "com.csulb.decisionator.USER_ID";
    protected final static String USER_AUTH = "com.csulb.decisionator.USER_AUTH";

    private String token;
    private boolean isLoggedIn;
    private SharedPreferences prefs;

    private CallbackManager callbackManager;
    private LoginManager logManager;
    private LoginButton loginButton;
    private TextView info;
    private Button goToLobby;
    private Intent loginSuccess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_facebook_login);
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        setContentView(R.layout.activity_facebook_login);
        info = (TextView)findViewById(R.id.info);
        loginButton = (LoginButton)findViewById(R.id.login_button);
        goToLobby = (Button) findViewById(R.id.goToLobby);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        logManager = LoginManager.getInstance();
        logManager.logOut();

        goToLobby.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(loginSuccess);
            }
        });

        loginSuccess = new Intent(this, LobbyActivity.class);

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult){
                token = loginResult.getAccessToken().toString();
                Profile me = Profile.getCurrentProfile();
                me.getFirstName();

                //JSONObject profile = Util.parseJson(facebook.request("me"));
                loginSuccess.putExtra(USER_F_NAME, me.getFirstName());
                loginSuccess.putExtra(USER_ID, loginResult.getAccessToken().getUserId());
                loginSuccess.putExtra(USER_AUTH, loginResult.getAccessToken());

                goToLobby.setVisibility(View.VISIBLE);
                isLoggedIn = true;
                prefs.edit().putBoolean("isLoggedIn", isLoggedIn).commit(); // isLoggedIn is a boolean value of your login status
            }
            @Override
            public void onCancel() {

            }
            @Override
            public void onError(FacebookException e) {
                info.setText("Login attempt failed.");
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private boolean checkLogin()
    {
        boolean loggedIn = false;

        loggedIn = prefs.getBoolean("isLoggedIn",false);

        return loggedIn;
    }

   
}
