package com.csulb.decisionator.decisionator;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.facebook.FacebookSdk;

import junit.framework.TestCase;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.fakes.RoboMenuItem;
import org.robolectric.shadows.ShadowApplication;

import java.util.HashMap;
import java.util.Map;

import static org.robolectric.shadows.support.v4.SupportFragmentTestUtil.startFragment;

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

        intent.putExtra(FeedActivity.FRIEND_ID, "1253227638023946");
        intent.putExtra(FeedActivity.FRIEND_F_NAME, "Russell");
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
    @Ignore
    public void test_EventActivity()
    {
        ShadowApplication shadAct = ShadowApplication.getInstance().getShadowApplication();

        Intent intent = new Intent(shadAct.getApplicationContext(), EventActivity.class);

        intent = new Intent(shadAct.getApplicationContext(), EventActivity.class);
        intent.putExtra(EventCreationActivity.EVENT_TOPIC, "Test");
        intent.putExtra(FacebookLogin.POOL_ID, poolID);
        intent.putExtra(FacebookLogin.USER_ID, "1253227638023946");
        intent.putExtra(FacebookLogin.USER_F_NAME, "Russell");
        intent.putExtra(EventCreationActivity.EVENT_ID, "test12345");
        EventActivity eventAct = Robolectric.buildActivity(EventActivity.class).withIntent(intent).create().visible().get();
        assertTrue(true);
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
    public void test_resultFragment()
    {
        Bundle fragArgs = new Bundle();
        fragArgs.putString(EventActivity.WORD_CLOUD_DATA, "Burger, Burger, burger, Burger, Bgr, Thai Food, Burger, burger, BuRgEr");
        fragArgs.putString(EventActivity.TOP_VENUE_DATA, "Tipps thai restaurant,1|May's Thai Kitchen,2|Bai Plu,3|Your Place restaurant,4|Long Beach Thai,5|");
        ResultGraphFragment frag = ResultGraphFragment.newInstance(fragArgs);

        startFragment(frag);
        assertNotNull(frag);

        Bundle fragArgsNull = new Bundle();
        fragArgsNull.putString(EventActivity.WORD_CLOUD_DATA, "Burger, Burger, burger, Burger, Bgr, Thai Food, Burger, burger, BuRgEr");
        fragArgsNull.putString(EventActivity.TOP_VENUE_DATA, "");
        ResultGraphFragment fragNull = ResultGraphFragment.newInstance(fragArgsNull);

        startFragment(fragNull);
        assertNotNull(fragNull);
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

    @Test
    public void test_CreateProfile()
    {
        String poolID = "us-east-1:a74e3f8c-6c2b-40b6-89d5-46d4f870a6f2";
        AmazonDynamoDBClient ddbClient;
        DynamoDBMapper mapper;

        CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                RuntimeEnvironment.application,   /* get the context for the application */
                poolID, // Identity Pool ID
                Regions.US_EAST_1           /* Region for your identity pool--US_EAST_1 or EU_WEST_1*/
        );

        ddbClient = new AmazonDynamoDBClient(credentialsProvider);
        mapper = new DynamoDBMapper(ddbClient);

        String userID ="russell-2345";
        String imageTags ="Man";
        String textTags = "text";
        String placesTags = "Long Beach";
        String likeTags = "Burgers";
        String movieLikeTags = "Deadpool";

        uProfile before = new uProfile();
        uProfile dbProfile = new uProfile();

        before.setUserID(userID);
        before.setPlacesTags(placesTags);
        before.setMovieLikeTags(movieLikeTags);
        before.setImageTags(imageTags);
        before.setTextTags(textTags);
        before.setLikeTags(likeTags);

        mapper.save(before);

        dbProfile = mapper.load(uProfile.class, before.getUserID());

        assertNotNull(dbProfile);
        assertEquals(dbProfile.getUserID(), userID);
        assertEquals(dbProfile.getImageTags(), imageTags);
        assertEquals(dbProfile.getMovieLikeTags(), movieLikeTags);
        assertEquals(dbProfile.getLikeTags(), likeTags);
        assertEquals(dbProfile.getPlacesTags(), placesTags);
        assertEquals(dbProfile.getTextTags(), textTags);

        mapper.delete(before);
        assertNull(mapper.load(uProfile.class, before.getUserID()));
    }
}
