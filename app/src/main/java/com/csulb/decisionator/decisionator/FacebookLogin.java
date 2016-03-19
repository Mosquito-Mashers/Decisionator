package com.csulb.decisionator.decisionator;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
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

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class FacebookLogin extends AppCompatActivity implements LocationListener {

    //Keys for passing intents
    protected final static String USER_F_NAME = "com.csulb.decisionator.USER_F_NAME";
    protected final static String USER_ID = "com.csulb.decisionator.USER_ID";
    protected final static String USER_AUTH = "com.csulb.decisionator.USER_AUTH";
    protected final static String CRED_ACCT_ID = "com.csulb.decisionator.CRED_ACCT_ID";
    protected final static String POOL_ID = "com.csulb.decisionator.POOL_ID";
    protected final static String UN_ROLE_ARN = "com.csulb.decisionator.UN_ROLE_ARN";
    protected final static String AU_ROLE_ARN = "com.csulb.decisionator.AU_ROLE_ARN";

    private boolean isLoggedIn;
    private boolean foundLoc = false;
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
    private ProgressBar locationProg;

    //Amazon api items
    private CognitoCachingCredentialsProvider credentialsProvider;

    //location suff...
    protected LocationManager locationManager;
    LocationUpdateTimeoutHandler timeout;
    private Location userLoc;
    private Event evnt;
    private String eventID;
    private String userID;
    SimpleDateFormat date = new SimpleDateFormat("dd-MM-yyy HH:mm:ss z");

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

    /*private void checkIfLoggedIn() {
        AccessToken tok = AccessToken.getCurrentAccessToken();
        if (tok != null) {
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
    */

    private void createFBCallback() {
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

    private void populateIntent(Intent loginSuccess, Map<String, String> intentValues) {
        Iterator mapIter = intentValues.entrySet().iterator();

        while (mapIter.hasNext()) {
            Map.Entry kvPair = (Map.Entry) mapIter.next();
            loginSuccess.putExtra(kvPair.getKey().toString(), kvPair.getValue().toString());
        }
    }

    private void initializeListeners() {


    }

    private void initializeGlobals() {
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
		locationProg = (ProgressBar) findViewById(R.id.locationProgress);
        loginButton.setReadPermissions(Arrays.asList("user_likes","user_tagged_places"));
        //mProfileTracker.startTracking();
    }

    private void validateAndProceed(Profile currUser) {
        //Create a new user db object
        Date currDate = new Date();

        currentUser = new User();
        currentUser.setUserID(currUser.getId());
        userID = currentUser.getUserID();
        currentUser.setfName(currUser.getFirstName());
        currentUser.setlName(currUser.getLastName());
        //String profPic = me.getProfilePictureUri(250, 250).toString();
        currentUser.setProfilePic(currUser.getProfilePictureUri(250, 250).toString());
        currentUser.setLastLogin(date.format(currDate));

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
        //Getting user location
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //permission check for requestLocationUpdates()
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        //Checking for current location from GPS_Provider (will timeout after 10sec)
        timeout = new LocationUpdateTimeoutHandler();
        info.setText("Updating your location...");
        locationProg.setVisibility(View.VISIBLE);
        timeout.execute(currentUser.getUserID());
        if(locationManager.getAllProviders().contains(LocationManager.GPS_PROVIDER)) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 300, this);
        }
		analyzeProfile(AccessToken.getCurrentAccessToken());
//        startActivity(loginSuccess);

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
        GraphRequest postsRequest = GraphRequest.newGraphPathRequest(
                token,
                "/me/posts",
                new GraphRequest.Callback() {
                    @Override
                    public void onCompleted(GraphResponse response) {
                        String posts = "";

                        JSONObject fbObj = response.getJSONObject();
                        JSONArray postText = null;
                        if(fbObj != null)
                        {
                            try {
                                postText = fbObj.getJSONArray("data");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            if(postText != null && postText.length() != 0) {

                                for (int k = 0; k < postText.length(); k++) {
                                    try {
                                        posts += postText.getJSONObject(k).getString("message") + " ";
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                                String[] words = posts.replaceAll("[^a-zA-Z ]", "").toLowerCase().split("\\s+");
                                String finalTags = "";
                                for(int i = 0; i < words.length; i++)
                                {
                                    finalTags += words[i] + " ";
                                }

                                userProf.setTextTags(finalTags);
                            }
                        }
                    }
                });

        GraphRequest tagged_placesRequest = GraphRequest.newGraphPathRequest(
                token,
                "/me/tagged_places",
                new GraphRequest.Callback() {
                    @Override
                    public void onCompleted(GraphResponse response) {
                        String places = "";

                        JSONObject fbObj = response.getJSONObject();
                        JSONArray placesText = null;
                        if(fbObj != null)
                        {
                            try {
                                placesText = fbObj.getJSONArray("data");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            if(placesText != null && placesText.length() != 0) {

                                for (int k = 0; k < placesText.length(); k++) {
                                    try {
                                        places += placesText.getJSONObject(k).getJSONObject("place").getString("name") + ",";
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }


                                userProf.setPlacesTags(places);
                            }
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
/*
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
*/


        tagged_placesRequest.executeAsync();
        postsRequest.executeAsync();
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
            if (temp != null) {
                temp.setProfilePic(arg0[0].getProfilePic());
                temp.setlName(arg0[0].getlName());
                temp.setfName(arg0[0].getfName());
                temp.setLastLogin(arg0[0].getLastLogin());
            } else {
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
                temp.setPlacesTags(arg0[0].getPlacesTags());
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

            if (me == null) {
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
            } else {
                me = Profile.getCurrentProfile();
            }

            return me;
        }

        @Override
        protected void onPostExecute(Profile prof) {


        }
    }

    /**
     * An asynchronous task to handle the timeout of current location inquiry and initialization of
     * userLoc.  It will wait 10 seconds for userLoc to be set, if it is not set (it is null)
     * then it will attempt to retrieve last known location, if that fails (it is still null)
     * userLoc will default to CSULB coordinates.
     */
    class LocationUpdateTimeoutHandler extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {

        }

        @Override
        protected String doInBackground(String... params) {
            try {
                //no-op for 2 seconds
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            if(isCancelled())
            {
                return params[0];
            }
            return params[0];
        }

        @Override
        protected void onPostExecute(String profID) {
            if(isCancelled())
            {
                return;
            }
            //if userLoc still null after 10 seconds time out requestLocationUpdate()
            if(userLoc == null) {
                //Checking since current location via GPS timed out
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    Log.d("Location", "Permission check returned");
                    return;
                }
                userLoc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                //If still null then no last known location is found, so set to CSULB
                if(userLoc == null)
                {
                    userLoc = new Location("No providers");
                    userLoc.setLatitude(33.760605);
                    userLoc.setLongitude(-118.156446);
                }

            }

            //debug: checking location values
            Log.d("Location", "Latitude: " + userLoc.getLatitude() + "Longitude: " + userLoc.getLongitude());
            User currUser = new User();

            currUser.setUserID(profID);
            currUser.setLatitude(userLoc.getLatitude());
            currUser.setLongitude(userLoc.getLongitude());

            new updateUserLoc().execute(currUser);

            //kill the async requestLocationUpdates() task here?

        }
    }

    //implemented classes from LocationListener
    @Override
    public void onLocationChanged(Location location) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        locationManager.removeUpdates(this);
        timeout.cancel(true);
        userLoc = location;

        User currUser = new User();

        currUser.setUserID(userID);
        currUser.setLatitude(location.getLatitude());
        currUser.setLongitude(location.getLongitude());

        new updateUserLoc().execute(currUser);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d("Latitude","status");
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d("Latitude", "enable");
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d("Latitude", "disable");
    }

    class updateUserLoc extends AsyncTask<User, Void, Void> {

        protected Void doInBackground(User... arg0) {

            if(!foundLoc) {
                foundLoc = true;
                AmazonDynamoDBClient ddbClient = new AmazonDynamoDBClient(credentialsProvider);
                DynamoDBMapper mapper = new DynamoDBMapper(ddbClient);
                User temp = mapper.load(User.class, arg0[0].getUserID());

                temp.setLatitude(arg0[0].getLatitude());
                temp.setLongitude(arg0[0].getLongitude());

                mapper.save(temp);
                startActivity(loginSuccess);
                if (isCancelled()) {
                    return null;
                }
            }

            return null;
        }
    }
}