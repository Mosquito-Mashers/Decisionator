package com.csulb.decisionator.decisionator;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Iterator;
import java.util.Map;

public class LobbyActivity extends AppCompatActivity {

    //Values retrieved from intent
    private String uName;
    private String uID;
    private String welcomeString;
    private String poolID;
    private Map<String, String> intentPairs = null;

    //Gui items
    private Intent loginSuccess;
    private Intent createEventIntent;
    private TextView welcomeMessage;
    private Button createEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initializeGlobals();

        initializeListeners();

        prepareIntent(createEventIntent, intentPairs);

        setContentView(R.layout.activity_lobby);
    }

    private void prepareIntent(Intent createEventIntent, Map<String, String> intentPairs) {
        Iterator mapIter = intentPairs.entrySet().iterator();

        while (mapIter.hasNext())
        {
            Map.Entry kvPair = (Map.Entry) mapIter.next();
            createEventIntent.putExtra(kvPair.getKey().toString(), kvPair.getValue().toString());
        }
    }

    private void initializeListeners() {
        createEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(createEventIntent);
            }
        });
    }

    private void initializeGlobals() {
        //Intent Initialization
        loginSuccess = getIntent();
        createEventIntent = new Intent(this, EventCreationActivity.class);

        //GUI assignments
        welcomeMessage = (TextView) findViewById(R.id.welcomeText);
        createEvent = (Button) findViewById(R.id.createEvent);

        //Prepare outgoing intent
        intentPairs.put(FacebookLogin.POOL_ID, poolID);
        intentPairs.put(FacebookLogin.USER_ID, uID);
        intentPairs.put(FacebookLogin.USER_F_NAME, uName);

        //Global string values
        uName = loginSuccess.getStringExtra(FacebookLogin.USER_F_NAME);
        uID = loginSuccess.getStringExtra(FacebookLogin.USER_ID);
        poolID = loginSuccess.getStringExtra(FacebookLogin.POOL_ID);

        //GUI Update based on intent
        welcomeString = welcomeMessage.getText() + " " + uName + "!";
        welcomeMessage.setText(welcomeString);
    }
}
