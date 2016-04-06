package com.csulb.decisionator.decisionator;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class friendEventActivity extends AppCompatActivity {

    private Intent fromFeedActivity;
    private Intent friendViewIntent;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_event);
    }
}
