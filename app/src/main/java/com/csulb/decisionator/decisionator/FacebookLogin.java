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
import com.amazonaws.mobileconnectors.cognito.CognitoSyncManager;
import com.amazonaws.mobileconnectors.cognito.Dataset;
import com.amazonaws.mobileconnectors.cognito.DefaultSyncCallback;
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
import java.util.List;
import java.util.Map;

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


                new DemoTask().execute();


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

    private void sync()
    {
        // Initialize the Cognito Sync client
        CognitoSyncManager syncClient = new CognitoSyncManager(
                getApplicationContext(),
                Regions.US_EAST_1, // Region
                credentialsProvider);

// Create a record in a dataset and synchronize with the server
        Dataset dataset = syncClient.openOrCreateDataset("myDataset");
        dataset.put("myKey", "myValue");
        dataset.synchronize(new DefaultSyncCallback() {
            @Override
            public void onSuccess(Dataset dataset, List newRecords) {

            }
        });
    }

    class DemoTask extends AsyncTask<Void, Void, Void> {

        protected Void doInBackground(Void... arg0) {
            AmazonDynamoDBClient ddbClient = new AmazonDynamoDBClient(credentialsProvider);
            DynamoDBMapper mapper = new DynamoDBMapper(ddbClient);

            User currentUser = new User();
            currentUser.setUserID("russell-2345");
            currentUser.setfName("Russell");
            currentUser.setlName("Tang");
            currentUser.setLongitude(-118.6654);
            currentUser.setLatitude(33.5643);
            mapper.save(currentUser);
            return null;
        }

        protected void onPostExecute(Void result) {
            // TODO: do something with the feed
        }
        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Void... values) {}
    }

    class updateTable extends AsyncTask<User, Void, Void>
    {
        @Override
        protected Void doInBackground(User... params) {
            AmazonDynamoDBClient ddbClient = new AmazonDynamoDBClient(credentialsProvider);
            DynamoDBMapper mapper = new DynamoDBMapper(ddbClient);

            User currentUser = new User();
            currentUser.setUserID("russell-2345");
            currentUser.setfName("Russell");
            currentUser.setlName("Tang");
            currentUser.setLongitude(-118.6654);
            currentUser.setLatitude(33.5643);
            mapper.save(currentUser);
            return null;
        }
    }
}
