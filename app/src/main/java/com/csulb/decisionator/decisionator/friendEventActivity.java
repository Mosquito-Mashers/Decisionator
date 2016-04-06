package com.csulb.decisionator.decisionator;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.regions.Regions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class friendEventActivity extends AppCompatActivity {

    private Intent fromFeedActivity;
    private Intent lobbyIntent;
    private Intent friendViewEventIntent;

    //Values retrieved fromFeedActivity intent
    private String uID;
    private String uName;
    private String poolID;
    private String fID;
    private String friendName;

    private static final Map<String, String> intentPairs = new HashMap<String, String>();
    private CognitoCachingCredentialsProvider credentialsProvider;

    private static final int notifyID = 111;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_event);
        initializeGlobals();
    }

    private void initializeGlobals() {
        fromFeedActivity = getIntent();
        lobbyIntent = new Intent(this, LobbyActivity.class);
        friendViewEventIntent = new Intent(this, EventActivity.class);

        uID = fromFeedActivity.getStringExtra(FacebookLogin.USER_ID);
        poolID = fromFeedActivity.getStringExtra(FacebookLogin.POOL_ID);
        uName = fromFeedActivity.getStringExtra(FacebookLogin.USER_F_NAME);

        fID = fromFeedActivity.getStringExtra(FacebookLogin.USER_ID friendID);

        credentialsProvider = new CognitoCachingCredentialsProvider(
                getApplicationContext(),    /* get the context for the application */
                poolID, // Identity Pool ID
                Regions.US_EAST_1           /* Region for your identity pool--US_EAST_1 or EU_WEST_1*/
        );
    }



}