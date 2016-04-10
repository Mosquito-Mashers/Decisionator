package com.csulb.decisionator.decisionator;

import android.content.Intent;
import android.view.MenuItem;
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
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;
import org.robolectric.fakes.RoboMenuItem;
import org.robolectric.shadows.ShadowActivity;
import org.robolectric.shadows.ShadowApplication;

import java.util.HashMap;
import java.util.Map;

import dalvik.annotation.TestTarget;

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
    public void test_initFB()
    {
        FacebookLogin act = Robolectric.buildActivity(FacebookLogin.class).create().visible().get();



    }

    @Test
    public void test_initLobby()
    {
        ShadowApplication shadAct = ShadowApplication.getInstance().getShadowApplication();

        Intent intent = new Intent(shadAct.getApplicationContext(), LobbyActivity.class);
        intent.putExtra(FacebookLogin.USER_F_NAME, "Russell");
        intent.putExtra(FacebookLogin.USER_ID, "1253227638023946");
        intent.putExtra(FacebookLogin.POOL_ID, poolID);
        LobbyActivity xyz = Robolectric.buildActivity(LobbyActivity.class).withIntent(intent).create().visible().get();
    }

    @Test
    public void test_init_EventCreation()
    {
        ShadowApplication shadAct = ShadowApplication.getInstance().getShadowApplication();

        Intent intent = new Intent(shadAct.getApplicationContext(), EventCreationActivity.class);

        intent = new Intent(shadAct.getApplicationContext(), EventCreationActivity.class);
        intent.putExtra(FacebookLogin.USER_F_NAME, "Russell");
        intent.putExtra(FacebookLogin.USER_ID, "1253227638023946");
        intent.putExtra(FacebookLogin.POOL_ID, poolID);
        EventCreationActivity evCreatAct = Robolectric.buildActivity(EventCreationActivity.class).withIntent(intent).create().visible().get();

    }

    @Test
    public void test_initInviteFriends()
    {

        ShadowApplication shadAct = ShadowApplication.getInstance().getShadowApplication();

        Intent intent = new Intent(shadAct.getApplicationContext(), InviteFriendsActivity.class);

        intent = new Intent(shadAct.getApplicationContext(), InviteFriendsActivity.class);
        intent.putExtra(EventCreationActivity.EVENT_TOPIC, "Test");
        intent.putExtra(FacebookLogin.POOL_ID, poolID);
        intent.putExtra(FacebookLogin.USER_ID, "1253227638023946");
        intent.putExtra(FacebookLogin.USER_F_NAME, "Russell");
        intent.putExtra(EventCreationActivity.EVENT_ID, "test12345");
        InviteFriendsActivity inviteAct = Robolectric.buildActivity(InviteFriendsActivity.class).withIntent(intent).create().visible().get();

    }

    @Test
    public void test_FriendEventActivity()
    {
        ShadowApplication shadAct = ShadowApplication.getInstance().getShadowApplication();

        Intent intent = new Intent(shadAct.getApplicationContext(), friendEventActivity.class);

        intent = new Intent(shadAct.getApplicationContext(), friendEventActivity.class);
        intent.putExtra(EventCreationActivity.EVENT_TOPIC, "Test");
        intent.putExtra(FacebookLogin.POOL_ID, poolID);
        intent.putExtra(FacebookLogin.USER_ID, "1253227638023946");
        intent.putExtra(FacebookLogin.USER_F_NAME, "Russell");
        intent.putExtra(EventCreationActivity.EVENT_ID, "test12345");
        friendEventActivity inviteAct = Robolectric.buildActivity(friendEventActivity.class).withIntent(intent).create().visible().get();
    }

    @Test
    public void test_FeedActivity()
    {
        ShadowApplication shadAct = ShadowApplication.getInstance().getShadowApplication();

        Intent intent = new Intent(shadAct.getApplicationContext(), FeedActivity.class);

        intent = new Intent(shadAct.getApplicationContext(), FeedActivity.class);
        intent.putExtra(EventCreationActivity.EVENT_TOPIC, "Test");
        intent.putExtra(FacebookLogin.POOL_ID, poolID);
        intent.putExtra(FacebookLogin.USER_ID, "1253227638023946");
        intent.putExtra(FacebookLogin.USER_F_NAME, "Russell");
        intent.putExtra(EventCreationActivity.EVENT_ID, "test12345");
        FeedActivity inviteAct = Robolectric.buildActivity(FeedActivity.class).withIntent(intent).create().visible().get();
    }

    @Test
    public void test_HistoryActivity()
    {
        ShadowApplication shadAct = ShadowApplication.getInstance().getShadowApplication();

        Intent intent = new Intent(shadAct.getApplicationContext(), UsersHistory.class);

        intent = new Intent(shadAct.getApplicationContext(), UsersHistory.class);
        intent.putExtra(EventCreationActivity.EVENT_TOPIC, "Test");
        intent.putExtra(FacebookLogin.POOL_ID, poolID);
        intent.putExtra(FacebookLogin.USER_ID, "1253227638023946");
        intent.putExtra(FacebookLogin.USER_F_NAME, "Russell");
        intent.putExtra(EventCreationActivity.EVENT_ID, "test12345");
        UsersHistory inviteAct = Robolectric.buildActivity(UsersHistory.class).withIntent(intent).create().visible().get();
    }

    @Test
    public void testEventCreate(){
        ShadowApplication shadAct = ShadowApplication.getInstance().getShadowApplication();
        Intent intent = new Intent(shadAct.getApplicationContext(), EventCreationActivity.class);
        intent.putExtra(FacebookLogin.USER_F_NAME, "Russell");
        intent.putExtra(FacebookLogin.USER_ID, "1253227638023946");
        intent.putExtra(FacebookLogin.POOL_ID, poolID);
        EventCreationActivity evCreatAct = Robolectric.buildActivity(EventCreationActivity.class).withIntent(intent).create().visible().get();

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

    @Test
    public void testMenu()
    {

        ShadowApplication shadAct = ShadowApplication.getInstance().getShadowApplication();
        Intent intent = new Intent(shadAct.getApplicationContext(), LobbyActivity.class);
        intent.putExtra(FacebookLogin.USER_F_NAME, "Russell");
        intent.putExtra(FacebookLogin.USER_ID, "1253227638023946");
        intent.putExtra(FacebookLogin.POOL_ID, poolID);
        LobbyActivity lobby = Robolectric.buildActivity(LobbyActivity.class).withIntent(intent).create().visible().get();

        MenuItem menuItem = new RoboMenuItem(R.id.logout);
        lobby.onOptionsItemSelected(menuItem);
//        ShadowActivity shadowActivity = Shadows.shadowOf(lobby);

    }
}
