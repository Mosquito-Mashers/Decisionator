package com.csulb.decisionator.decisionator;

import android.app.Activity;
import android.content.Intent;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.login.widget.LoginButton;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;
import org.robolectric.internal.Shadow;
import org.robolectric.shadows.ShadowActivity;
import org.robolectric.shadows.ShadowApplication;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Russell-Test on 4/2/2016.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class RoboTests extends TestCase{
    private CognitoCachingCredentialsProvider credentialsProvider;
    private AmazonDynamoDBClient ddbClient;
    private DynamoDBMapper mapper;
    private String fbToken = "CAAHqhnf8GfgBAMmRT0g9sVZAfFvijZANLuaQgbEUxLzsNVdXxtNMxW4GguBh2lEBVI7iKUQX6HBPEt7g9uxGZCcmSJzalX2gJ12vHChsyKoyKAvukddQjbZB8gkpRy8XqCC8cqldWwWyFRmopH92ZAAIssGhvuHQ94aWISuXh5fgdEENEoJK4";
    private String poolID = "us-east-1:a74e3f8c-6c2b-40b6-89d5-46d4f870a6f2";


    @Override
    public void setUp() throws Exception {

        Map<String, String> logins = new HashMap<>();
        logins.put("graph.facebook.com", fbToken);
        credentialsProvider.setLogins(logins);
        ddbClient = new AmazonDynamoDBClient(credentialsProvider);
        mapper = new DynamoDBMapper(ddbClient);
        FacebookSdk.setClientToken(fbToken);
    }
    @Test
    public void testEventCreateActivityTest(){
        FacebookLogin act = Robolectric.buildActivity(FacebookLogin.class).create().get();

        ShadowApplication shadAct = ShadowApplication.getInstance().getShadowApplication();
        Intent intent = new Intent(shadAct.getApplicationContext(), LobbyActivity.class);
        intent.putExtra(FacebookLogin.USER_F_NAME, "Russell");
        intent.putExtra(FacebookLogin.USER_ID, "1253227638023946");
        intent.putExtra(FacebookLogin.POOL_ID, poolID);
        LobbyActivity xyz = Robolectric.buildActivity(LobbyActivity.class).withIntent(intent).create().get();

        intent = new Intent(shadAct.getApplicationContext(), EventCreationActivity.class);
        intent.putExtra(FacebookLogin.USER_F_NAME, "Russell");
        intent.putExtra(FacebookLogin.USER_ID, "1253227638023946");
        intent.putExtra(FacebookLogin.POOL_ID, poolID);
        EventCreationActivity evCreatAct = Robolectric.buildActivity(EventCreationActivity.class).withIntent(intent).create().get();


        intent = new Intent(shadAct.getApplicationContext(), InviteFriendsActivity.class);
        intent.putExtra(EventCreationActivity.EVENT_TOPIC, "Test");
        intent.putExtra(FacebookLogin.POOL_ID, poolID);
        intent.putExtra(FacebookLogin.USER_ID, "1253227638023946");
        intent.putExtra(FacebookLogin.USER_F_NAME, "Russell");
        intent.putExtra(EventCreationActivity.EVENT_ID, "test12345");
        InviteFriendsActivity inviteAct = Robolectric.buildActivity(InviteFriendsActivity.class).withIntent(intent).create().get();

        intent = new Intent(shadAct.getApplicationContext(), InviteFriendsActivity.class);
        intent.putExtra(FacebookLogin.POOL_ID, poolID);
        intent.putExtra(FacebookLogin.USER_ID, "1253227638023946");
        intent.putExtra(FacebookLogin.USER_F_NAME, "Russell");
        intent.putExtra(EventCreationActivity.EVENT_ID,"test12345");
        intent.putExtra(EventCreationActivity.EVENT_TOPIC, "Test");
        intent.putExtra(EventCreationActivity.EVENT_INVITES, "No one");
        intent.putExtra(EventCreationActivity.EVENT_HOST_NAME, "Russsell");
        intent.putExtra(EventCreationActivity.EVENT_CATEGORY, "Location Based");
        //EventActivity eventAct = Robolectric.buildActivity(EventActivity.class).withIntent(intent).create().get();



        //LobbyActivity lAct = new LobbyActivity();
        //lAct.setPoolID(poolID);
        //lAct = Robolectric.buildActivity(LobbyActivity.class).create().get();
        /*
        //act.onCreate(null);

        //  ShadowActivity sAct = Shadows.shadowOf(act);
        LoginButton button = (LoginButton) act.findViewById(R.id.login_button);


        button.performClick();

        /*
        act.setCredentialsProvider(credentialsProvider);
        Profile testProf = new Profile();

        User tester = new User();
        tester.setUserID("1253227638023946");
        act.setCurrentUser(tester);
        act.validateAndProceed();
        act.analyzeProfile(AccessToken.getCurrentAccessToken());
*/
        assertTrue(true);
    }

}
