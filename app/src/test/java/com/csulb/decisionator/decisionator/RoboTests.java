package com.csulb.decisionator.decisionator;

import android.content.Intent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.facebook.FacebookSdk;

import junit.framework.TestCase;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;
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

    @Test
    public void testEventCreate(){
        ShadowApplication shadAct = ShadowApplication.getInstance().getShadowApplication();
        Intent intent = new Intent(shadAct.getApplicationContext(), EventCreationActivity.class);
        intent.putExtra(FacebookLogin.USER_F_NAME, "Russell");
        intent.putExtra(FacebookLogin.USER_ID, "1253227638023946");
        intent.putExtra(FacebookLogin.POOL_ID, poolID);
        EventCreationActivity evCreatAct = Robolectric.buildActivity(EventCreationActivity.class).withIntent(intent).create().get();

        RadioButton locationBased = (RadioButton) evCreatAct.findViewById(R.id.radioLocation);
        RadioButton foodBased = (RadioButton) evCreatAct.findViewById(R.id.radioFood);
        RadioButton entertainmentBased = (RadioButton) evCreatAct.findViewById(R.id.radioEntertainment);


        locationBased.performClick();

        TextView helperText = (TextView) evCreatAct.findViewById(R.id.topicPredicate);

        String text = helperText.getText().toString();

        assertEquals(text, "Lets go to a...");
        ////////////////
        foodBased.performClick();

        text = helperText.getText().toString();

        assertEquals(text, "I'm feeling...");
        /////////////////
        entertainmentBased.performClick();

        text = helperText.getText().toString();

        assertEquals(text, "Lets go to a...");

        EditText topic = (EditText) evCreatAct.findViewById(R.id.eventTopic);

        topic.performClick();
        evCreatAct.hideKeyboard();
        topic.setText("Thai Restaurant");

        Button invite = (Button) evCreatAct.findViewById(R.id.inviteFriendsBtn);

        invite.performClick();
    }
}
