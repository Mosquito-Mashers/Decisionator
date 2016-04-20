package com.csulb.decisionator.decisionator;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class MyProfile extends AppCompatActivity {

    private Intent lobbyIntent;
    private Intent logoutIntent;
    private Intent enterProfile;


    private String poolID;
    private String uFName;
    String uID;

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

        lobbyIntent.putExtra(FacebookLogin.USER_ID,uID);
        lobbyIntent.putExtra(FacebookLogin.POOL_ID,poolID);
        lobbyIntent.putExtra(FacebookLogin.USER_F_NAME,uFName);
    }
}
