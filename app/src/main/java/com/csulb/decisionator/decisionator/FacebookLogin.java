package com.csulb.decisionator.decisionator;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import java.util.HashMap;
import java.util.Map;

public class FacebookLogin extends AppCompatActivity {

    //Keys for the intent
    protected final static String USER_F_NAME = "com.csulb.decisionator.USER_F_NAME";
    protected final static String USER_ID = "com.csulb.decisionator.USER_ID";
    protected final static String USER_AUTH = "com.csulb.decisionator.USER_AUTH";
    protected final static String CRED_ACCT_ID = "com.csulb.decisionator.CRED_ACCT_ID";
    protected final static String POOL_ID = "com.csulb.decisionator.POOL_ID";
    protected final static String UN_ROLE_ARN = "com.csulb.decisionator.UN_ROLE_ARN";
    protected final static String AU_ROLE_ARN = "com.csulb.decisionator.AU_ROLE_ARN";

    private String token;
    private boolean isLoggedIn;
    private SharedPreferences prefs;

    private CallbackManager callbackManager;
    private LoginManager logManager;
    private LoginButton loginButton;
    private TextView info;
    private Button goToLobby;
    private Intent loginSuccess;

    private CognitoCachingCredentialsProvider credentialsProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_facebook_login);
        FacebookSdk.sdkInitialize(getApplicationContext());

        credentialsProvider = new CognitoCachingCredentialsProvider(
                getApplicationContext(),    /* get the context for the application */
                "us-east-1:a74e3f8c-6c2b-40b6-89d5-46d4f870a6f2", // Identity Pool ID
                Regions.US_EAST_1           /* Region for your identity pool--US_EAST_1 or EU_WEST_1*/
        );

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

                Map<String, String> logins = new HashMap<String, String>();
                logins.put("graph.facebook.com", AccessToken.getCurrentAccessToken().getToken());
                credentialsProvider.setLogins(logins);

                token = loginResult.getAccessToken().toString();
                Profile me = Profile.getCurrentProfile();
                me.getFirstName();

                User currentUser = new User();
                currentUser.setUserID(me.getId());
                currentUser.setfName(me.getFirstName());
                currentUser.setlName(me.getLastName());
                currentUser.setProfilePic(me.getProfilePictureUri(250,250).toString());


                new addUserToDB().execute(currentUser);

                //JSONObject profile = Util.parseJson(facebook.request("me"));
                loginSuccess.putExtra(USER_F_NAME, me.getFirstName());
                loginSuccess.putExtra(USER_ID, me.getId());
                loginSuccess.putExtra(USER_AUTH, loginResult.getAccessToken());
                loginSuccess.putExtra(POOL_ID,credentialsProvider.getIdentityPoolId());

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

    public CognitoCachingCredentialsProvider getCredentials()
    {
        return credentialsProvider;
    }

    class addUserToDB extends AsyncTask<User, Void, Void> {

        protected Void doInBackground(User... arg0) {
            AmazonDynamoDBClient ddbClient = new AmazonDynamoDBClient(credentialsProvider);
            DynamoDBMapper mapper = new DynamoDBMapper(ddbClient);

            User temp = mapper.load(User.class, arg0[0].getUserID());
            temp.setProfilePic(arg0[0].getProfilePic());
            temp.setLatitude(arg0[0].getLatitude());
            temp.setLongitude(arg0[0].getLongitude());
            temp.setlName(arg0[0].getlName());
            temp.setfName(arg0[0].getfName());


            mapper.save(temp);

            return null;
        }
    }
}
