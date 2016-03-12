package com.csulb.decisionator.decisionator;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class FacebookLogin extends AppCompatActivity {

    //Keys for passing intents
    protected final static String USER_F_NAME = "com.csulb.decisionator.USER_F_NAME";
    protected final static String USER_ID = "com.csulb.decisionator.USER_ID";
    protected final static String USER_AUTH = "com.csulb.decisionator.USER_AUTH";
    protected final static String CRED_ACCT_ID = "com.csulb.decisionator.CRED_ACCT_ID";
    protected final static String POOL_ID = "com.csulb.decisionator.POOL_ID";
    protected final static String UN_ROLE_ARN = "com.csulb.decisionator.UN_ROLE_ARN";
    protected final static String AU_ROLE_ARN = "com.csulb.decisionator.AU_ROLE_ARN";

    private boolean isLoggedIn;
    private SharedPreferences prefs;
    private static final Map<String, String> intentValues = new HashMap<String, String>();
    private User currentUser;
    private uProfile userProf;

    //Facebook api items
    private CallbackManager callbackManager;
    private ProfileTracker mProfileTracker;
    private Profile me;
    private LoginManager logManager;
    private LoginButton loginButton;

    //Amazon api items
    private CognitoCachingCredentialsProvider credentialsProvider;

    //Gui items
    private TextView info;
    private Button goToLobby;
    private Intent loginSuccess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Initialize the facebook api
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        logManager = LoginManager.getInstance();

        setContentView(R.layout.activity_facebook_login);

        logManager.logOut();
        isLoggedIn = false;

        //Initialize the global variables for:
        //Android objects
        //Api objects
        initializeGlobals();

        //Create and assign the appropriate listeners for each gui object
        initializeListeners();

        //Start the Facebook api callback
        createFBCallback();

        //checkIfLoggedIn();
    }

    private void checkIfLoggedIn() {
        AccessToken tok = AccessToken.getCurrentAccessToken();
        if(tok != null)
        {
            //Get all relevant facebook data
            Profile me = Profile.getCurrentProfile();

            //Create a new user db object
            User currentUser = new User();
            currentUser.setUserID(me.getId());
            currentUser.setfName(me.getFirstName());
            currentUser.setlName(me.getLastName());
            currentUser.setProfilePic(me.getProfilePictureUri(250, 250).toString());

            //Start the asynchronous push to the db
            new addUserToDB().execute(currentUser);

            //Prepare all the intent data to be passed to the next activity
            intentValues.put(USER_F_NAME, me.getFirstName());
            intentValues.put(USER_ID, me.getId());
            intentValues.put(USER_AUTH, tok.toString());
            intentValues.put(POOL_ID, credentialsProvider.getIdentityPoolId());

            populateIntent(loginSuccess, intentValues);
            startActivity(loginSuccess);
        }
    }

    private void createFBCallback()
    {
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                Map<String, String> logins = new HashMap<String, String>();
                logins.put("graph.facebook.com", AccessToken.getCurrentAccessToken().getToken());

                credentialsProvider.setLogins(logins);

                //new getUserProf().execute();

                //me = Profile.getCurrentProfile();

                /*
                //Get all relevant facebook data
                Profile me = Profile.getCurrentProfile();

                //Create a new user db object
                User currentUser = new User();
                currentUser.setUserID(me.getId());
                currentUser.setfName(me.getFirstName());
                currentUser.setlName(me.getLastName());
                //String profPic = me.getProfilePictureUri(250, 250).toString();
                currentUser.setProfilePic(me.getProfilePictureUri(250, 250).toString());

                //Start the asynchronous push to the db
                new addUserToDB().execute(currentUser);

                //Prepare all the intent data to be passed to the next activity
                intentValues.put(USER_F_NAME, me.getFirstName());
                intentValues.put(USER_ID, me.getId());
                intentValues.put(USER_AUTH, loginResult.getAccessToken().toString());
                intentValues.put(POOL_ID, credentialsProvider.getIdentityPoolId());

                populateIntent(loginSuccess, intentValues);

                //Show the button to allow the user to move on
                isLoggedIn = true;
                prefs.edit().putBoolean("isLoggedIn", isLoggedIn).commit(); // isLoggedIn is a boolean value of your login status
                startActivity(loginSuccess);
                */
            }

            @Override
            public void onCancel() {
                info.setText("Login attempt canceled.");
            }

            @Override
            public void onError(FacebookException e) {
                info.setText("Login attempt failed.");
            }
        });
    }

    private void populateIntent(Intent loginSuccess, Map<String, String> intentValues)
    {
        Iterator mapIter = intentValues.entrySet().iterator();

        while (mapIter.hasNext())
        {
            Map.Entry kvPair = (Map.Entry) mapIter.next();
            loginSuccess.putExtra(kvPair.getKey().toString(), kvPair.getValue().toString());
        }
    }

    private void initializeListeners()
    {
    }

    private void initializeGlobals()
    {
        //Initialize Amazon api
        credentialsProvider = new CognitoCachingCredentialsProvider(
                getApplicationContext(),    /* get the context for the application */
                "us-east-1:a74e3f8c-6c2b-40b6-89d5-46d4f870a6f2", // Identity Pool ID
                Regions.US_EAST_1           /* Region for your identity pool--US_EAST_1 or EU_WEST_1*/
        );

        //Initialize android objects
        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        ProfileTracker mProfileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile profile, Profile profile2) {
                this.stopTracking();
                validateAndProceed(profile2);
            }
        };
        mProfileTracker.startTracking();

        //Initialize the intents
        loginSuccess = new Intent(this, LobbyActivity.class);

        //Assign gui objects
        info = (TextView)findViewById(R.id.info);
        loginButton = (LoginButton)findViewById(R.id.login_button);
        loginButton.setReadPermissions(Arrays.asList("user_likes"));
        //mProfileTracker.startTracking();
    }

    private void validateAndProceed(Profile currUser) {
        //Create a new user db object
        currentUser = new User();
        currentUser.setUserID(currUser.getId());
        currentUser.setfName(currUser.getFirstName());
        currentUser.setlName(currUser.getLastName());
        //String profPic = me.getProfilePictureUri(250, 250).toString();
        currentUser.setProfilePic(currUser.getProfilePictureUri(250, 250).toString());

        //Start the asynchronous push to the db
        new addUserToDB().execute(currentUser);
        //Prepare all the intent data to be passed to the next activity
        intentValues.put(USER_F_NAME, currUser.getFirstName());
        intentValues.put(USER_ID, currUser.getId());
        intentValues.put(POOL_ID, credentialsProvider.getIdentityPoolId());

        populateIntent(loginSuccess, intentValues);

        //Show the button to allow the user to move on
        isLoggedIn = true;
        prefs.edit().putBoolean("isLoggedIn", isLoggedIn).commit(); // isLoggedIn is a boolean value of your login status
        analyzeProfile(AccessToken.getCurrentAccessToken());
        startActivity(loginSuccess);

    }

    private void analyzeProfile(AccessToken token) {
        userProf = new uProfile();
        userProf.setUserID(currentUser.getUserID());
        GraphRequest movieRequest = GraphRequest.newGraphPathRequest(
                token,
                "/me/movies",
                new GraphRequest.Callback() {
                    @Override
                    public void onCompleted(GraphResponse response) {
                        String likedMovs = "";

                        JSONObject fbObj = response.getJSONObject();
                        JSONArray likedMovies = null;
                        try {
                            likedMovies = fbObj.getJSONArray("data");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if(likedMovies != null && likedMovies.length() != 0) {

                            for (int k = 0; k < likedMovies.length(); k++) {
                                try {
                                    likedMovs += likedMovies.getJSONObject(k).getString("name") + ",";
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            userProf.setMovieLikeTags(likedMovs);
                        }
                    }
                });

        GraphRequest likeRequest = GraphRequest.newGraphPathRequest(
                token,
                "/me/likes",
                new GraphRequest.Callback() {
                    @Override
                    public void onCompleted(GraphResponse response) {
                        String likes = "";
                        JSONObject fbObj = response.getJSONObject();

                        JSONArray likesArr = null;
                        try {
                            likesArr = fbObj.getJSONArray("data");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        if(likesArr != null && likesArr.length() != 0) {
                            for (int k = 0; k < likesArr.length(); k++) {
                                try {
                                    likes += likesArr.getJSONObject(k).getString("name") + ",";
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            userProf.setLikeTags(likes);
                        }
                        new updateProfile().execute(userProf);
                    }
                });

        GraphRequest request = GraphRequest.newMeRequest(
                token,
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        // Insert your code here
                    }
                });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "about,bio,posts,photos.limit(20),likes,sports,music,tagged_places");
        request.setParameters(parameters);
        request.executeAsync();



        movieRequest.executeAsync();
        likeRequest.executeAsync();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    //Asynchronous task to add a user to the db, updates user it they already exist
    class addUserToDB extends AsyncTask<User, Void, Void> {

        protected Void doInBackground(User... arg0) {
            AmazonDynamoDBClient ddbClient = new AmazonDynamoDBClient(credentialsProvider);
            DynamoDBMapper mapper = new DynamoDBMapper(ddbClient);

            User temp = mapper.load(User.class, arg0[0].getUserID());
            if(temp != null) {
                temp.setProfilePic(arg0[0].getProfilePic());
                temp.setlName(arg0[0].getlName());
                temp.setfName(arg0[0].getfName());
            }
            else
            {
                temp = arg0[0];
            }
            mapper.save(temp);
            return null;
        }
    }

    //Asynchronous task to add a user to the db, updates user it they already exist
    class updateProfile extends AsyncTask<uProfile, Void, Void> {

        protected Void doInBackground(uProfile... arg0) {
            AmazonDynamoDBClient ddbClient = new AmazonDynamoDBClient(credentialsProvider);
            DynamoDBMapper mapper = new DynamoDBMapper(ddbClient);

            uProfile temp = mapper.load(uProfile.class, arg0[0].getUserID());
            if (temp != null) {
                temp.setMovieLikeTags(arg0[0].getMovieLikeTags());
                temp.setImageTags(arg0[0].getImageTags());
                temp.setLikeTags(arg0[0].getLikeTags());
                temp.setTextTags(arg0[0].getTextTags());
            }
            else
            {
                temp = arg0[0];
            }
            mapper.save(temp);
            return null;
        }
    }
    //Asynchronous task to add a user to the db, updates user it they already exist
    class getUserProf extends AsyncTask<Void, Void, Profile> {
        private ProfileTracker mProfileTracker;
        @Override
        protected Profile doInBackground(Void... arg0) {
            //Get all relevant facebook data
            Profile me = Profile.getCurrentProfile();

            if(me == null) {
                mProfileTracker = new ProfileTracker() {
                    @Override
                    protected void onCurrentProfileChanged(Profile profile, Profile profile2) {
                        // profile2 is the new profile
                        Log.v("facebook - profile", profile2.getFirstName());
                        mProfileTracker.stopTracking();
                    }
                };
                mProfileTracker.startTracking();
                me = Profile.getCurrentProfile();
            }
            else
            {
                me = Profile.getCurrentProfile();
            }

            return me;
        }

        @Override
        protected void onPostExecute(Profile prof)
        {


        }
    }
}
