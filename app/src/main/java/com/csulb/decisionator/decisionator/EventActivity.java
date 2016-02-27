package com.csulb.decisionator.decisionator;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class EventActivity extends AppCompatActivity {

    private Intent enterEvent;
    private Intent goToLobby;
    private Map<String, String> intentPairs = new HashMap<String, String>();

    private String eTopic;
    private String eHost;
    private String eInvites;
    private String eCategory;
    private String eID;
    private String poolID;
    private String uID;
    private String uName;


    private TextView eventTitle;
    private TextView mapsContainer;
    private TextView eventHost;
    private ListView invitedList;
    private Button returnToLobby;
    private ImageView eventCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        initializeGlobals();

        initializeListeners();

        prepareIntent(goToLobby,intentPairs);
    }



    private void initializeGlobals()
    {
        goToLobby = new Intent(this, LobbyActivity.class);

        enterEvent = getIntent();
        eTopic = enterEvent.getStringExtra(EventCreationActivity.EVENT_TOPIC);
        eHost = enterEvent.getStringExtra(EventCreationActivity.EVENT_HOST_NAME);
        eInvites = enterEvent.getStringExtra(EventCreationActivity.EVENT_INVITES);
        eCategory = enterEvent.getStringExtra(EventCreationActivity.EVENT_CATEGORY);
        eID = enterEvent.getStringExtra(EventCreationActivity.EVENT_ID);
        poolID = enterEvent.getStringExtra(FacebookLogin.POOL_ID);
        uID = enterEvent.getStringExtra(FacebookLogin.USER_ID);
        uName = enterEvent.getStringExtra(FacebookLogin.USER_F_NAME);

        intentPairs.put(FacebookLogin.POOL_ID, poolID);
        intentPairs.put(FacebookLogin.USER_ID, uID);
        intentPairs.put(FacebookLogin.USER_F_NAME, uName);

        eventTitle = (TextView) findViewById(R.id.eventTitle);
        mapsContainer = (TextView) findViewById(R.id.gMapsContainer);
        eventHost = (TextView) findViewById(R.id.eventHost);
        invitedList = (ListView) findViewById(R.id.invitedList);
        returnToLobby = (Button) findViewById(R.id.returnToLobby);
        eventCategory = (ImageView) findViewById(R.id.eventDescPic);

        eventTitle.setText(eTopic);
        eventHost.setText(eHost);
    }

    private void initializeListeners() {

        returnToLobby.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(goToLobby);
            }
        });
    }

    private void prepareIntent(Intent moveToLobby, Map<String, String> intentPairs) {
        Iterator mapIter = intentPairs.entrySet().iterator();

        while (mapIter.hasNext())
        {
            Map.Entry kvPair = (Map.Entry) mapIter.next();
            moveToLobby.putExtra(kvPair.getKey().toString(), kvPair.getValue().toString());
        }
    }
}
