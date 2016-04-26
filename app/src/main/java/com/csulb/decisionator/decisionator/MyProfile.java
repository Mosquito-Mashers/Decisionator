package com.csulb.decisionator.decisionator;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;

public class MyProfile extends AppCompatActivity {

    private Intent lobbyIntent;
    private Intent logoutIntent;
    private Intent enterProfile;


    private String poolID;
    private String uFName;
    String uID;

    private TextView profileWelcome;
    private TextView picAnalysis;
    private TextView locationAnalysis;
    private TextView likesAnalysis;
    private ImageView profilePic;
    private ProgressBar profileLoading;

    private uProfile currProfile;

    private CognitoCachingCredentialsProvider credentialsProvider;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.actionbar_resources, menu);
        MenuItem itemChart = menu.findItem(R.id.chart);
        itemChart.setVisible(false);
        MenuItem itemProfile = menu.findItem(R.id.profile);
        itemProfile.setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                startActivity(logoutIntent);
                return true;
            case R.id.lobby:
                startActivity(lobbyIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        initializeGlobals();


    }

    public void initializeGlobals()
    {
        logoutIntent = new Intent(this, FacebookLogin.class);
        lobbyIntent = new Intent(this, LobbyActivity.class);

        enterProfile = getIntent();

        poolID = enterProfile.getStringExtra(FacebookLogin.POOL_ID);
        uFName = enterProfile.getStringExtra(FacebookLogin.USER_F_NAME);
        uID = enterProfile.getStringExtra(FacebookLogin.USER_ID);

        credentialsProvider = new CognitoCachingCredentialsProvider(
                getApplicationContext(),    /* get the context for the application */
                poolID, // Identity Pool ID
                Regions.US_EAST_1           /* Region for your identity pool--US_EAST_1 or EU_WEST_1*/
        );

        lobbyIntent.putExtra(FacebookLogin.USER_ID,uID);
        lobbyIntent.putExtra(FacebookLogin.POOL_ID,poolID);
        lobbyIntent.putExtra(FacebookLogin.USER_F_NAME,uFName);

        profileWelcome = (TextView) findViewById(R.id.userProfileWelcome);
        picAnalysis = (TextView) findViewById(R.id.profilePicAnalysis);
        locationAnalysis = (TextView) findViewById(R.id.locationAnalysis);
        likesAnalysis = (TextView) findViewById(R.id.publicLikesAnalysis);
        profilePic = (ImageView) findViewById(R.id.myProfilePic);
        profileLoading = (ProgressBar) findViewById(R.id.profileLoading);

        profileWelcome.setText("Welcome to your profile!");
        new getUserProfile().execute(uID);
    }

    class getUserProfile extends AsyncTask<String,Void,Void>{

        @Override
        protected Void doInBackground(String... params) {
            AmazonDynamoDBClient ddbClient = new AmazonDynamoDBClient(credentialsProvider);
            DynamoDBMapper mapper = new DynamoDBMapper(ddbClient);

            currProfile = mapper.load(uProfile.class,params[0]);

            return null;
        }

        @Override
        protected void onPostExecute(Void param)
        {
            profileLoading.setVisibility(View.GONE);
            picAnalysis.setText(currProfile.getImageTags());
            locationAnalysis.setText(currProfile.getPlacesTags());
            likesAnalysis.setText(currProfile.getLikeTags());
        }
    }
}
