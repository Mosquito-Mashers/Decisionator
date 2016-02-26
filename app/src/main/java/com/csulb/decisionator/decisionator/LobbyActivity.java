package com.csulb.decisionator.decisionator;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class LobbyActivity extends AppCompatActivity {

    private Intent loginSuccess;
    private Intent createEventIntent;

    private TextView welcomeMessage;
    private Button createEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);

        loginSuccess = getIntent();

        welcomeMessage = (TextView) findViewById(R.id.welcomeText);
        createEvent = (Button) findViewById(R.id.createEvent);
        createEventIntent = new Intent(this, EventCreationActivity.class);

        String uName = loginSuccess.getStringExtra(FacebookLogin.USER_F_NAME);
        String uID = loginSuccess.getStringExtra(FacebookLogin.USER_ID);
        String welcomeString = welcomeMessage.getText() + " " + uName + "!";
        String poolID = loginSuccess.getStringExtra(FacebookLogin.POOL_ID);
        createEventIntent.putExtra(FacebookLogin.POOL_ID, poolID);
        createEventIntent.putExtra(FacebookLogin.USER_ID, uID);
        createEventIntent.putExtra(FacebookLogin.USER_F_NAME,uName);

        welcomeMessage.setText(welcomeString);

        createEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(createEventIntent);
            }
        });
    }
}
