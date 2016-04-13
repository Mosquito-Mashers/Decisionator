package com.csulb.decisionator.decisionator;

import android.content.Intent;
import android.location.Location;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
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
import org.robolectric.RobolectricTestRunner;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;
import org.robolectric.internal.Shadow;
import org.robolectric.internal.ShadowExtractor;
import org.robolectric.shadows.ShadowAbsListView;
import org.robolectric.shadows.ShadowActivity;
import org.robolectric.shadows.ShadowApplication;
import org.robolectric.shadows.ShadowListView;
import org.robolectric.shadows.ShadowLog;
import org.robolectric.shadows.ShadowLooper;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by Jose on 4/7/2016.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class RoboTestsGUI extends TestCase {
    private CognitoCachingCredentialsProvider credentialsProvider;
    private AmazonDynamoDBClient ddbClient;
    private DynamoDBMapper mapper;
    private String fbToken = "CAAHqhnf8GfgBAMmRT0g9sVZAfFvijZANLuaQgbEUxLzsNVdXxtNMxW4GguBh2lEBVI7iKUQX6HBPEt7g9uxGZCcmSJzalX2gJ12vHChsyKoyKAvukddQjbZB8gkpRy8XqCC8cqldWwWyFRmopH92ZAAIssGhvuHQ94aWISuXh5fgdEENEoJK4";
    private String poolID = "us-east-1:a74e3f8c-6c2b-40b6-89d5-46d4f870a6f2";

    @Override
    public void setUp(){
        Map<String, String> logins = new HashMap<>();
        logins.put("graph.facebook.com", fbToken);
        credentialsProvider.setLogins(logins);
        ddbClient = new AmazonDynamoDBClient(credentialsProvider);
        mapper = new DynamoDBMapper(ddbClient);
        FacebookSdk.setClientToken(fbToken);
    }

    @Test
    public void testLobby(){
        FacebookLogin act = Robolectric.buildActivity(FacebookLogin.class).create().get();

        ShadowApplication shadAct = ShadowApplication.getInstance().getShadowApplication();
        Intent intent = new Intent(shadAct.getApplicationContext(), LobbyActivity.class);
        intent.putExtra(FacebookLogin.USER_F_NAME, "Russell");
        intent.putExtra(FacebookLogin.USER_ID, "1253227638023946");
        intent.putExtra(FacebookLogin.POOL_ID, poolID);
        LobbyActivity xyz = Robolectric.buildActivity(LobbyActivity.class).withIntent(intent).create().get();

        ImageButton button = (ImageButton) xyz.findViewById(R.id.history);
        assertNotNull(button);

        Intent expectedIntent = new Intent(shadAct.getApplicationContext(), UsersHistory.class);
        expectedIntent.putExtra(FacebookLogin.USER_F_NAME, "Russell");
        expectedIntent.putExtra(FacebookLogin.USER_ID, "1253227638023946");
        expectedIntent.putExtra(FacebookLogin.POOL_ID, poolID);
        button.performClick();
        //Test usersHistory button loads correct activity
        assertEquals(shadAct.getNextStartedActivity(), expectedIntent);

        Button button2 = (Button) xyz.findViewById(R.id.createEvent);

        expectedIntent = new Intent(shadAct.getApplicationContext(),EventCreationActivity.class);
        expectedIntent.putExtra(FacebookLogin.USER_F_NAME, "Russell");
        expectedIntent.putExtra(FacebookLogin.USER_ID, "1253227638023946");
        expectedIntent.putExtra(FacebookLogin.POOL_ID, poolID);
        button2.performClick();
        //Test createEvent button loads the correct activity
        assertEquals(shadAct.getNextStartedActivity(), expectedIntent);

        ImageButton button3 = (ImageButton) xyz.findViewById(R.id.refreshEvents);

        expectedIntent = new Intent(shadAct.getApplicationContext(),LobbyActivity.class);
        expectedIntent.putExtra(FacebookLogin.USER_F_NAME, "Russell");
        expectedIntent.putExtra(FacebookLogin.USER_ID, "1253227638023946");
        expectedIntent.putExtra(FacebookLogin.POOL_ID, poolID);
        button3.performClick();
        assertEquals(xyz.getIntent(),expectedIntent);
    }
    @Test
    public void testUsersHistory(){
        ShadowApplication shadAct = ShadowApplication.getInstance().getShadowApplication();
        Intent intent = new Intent(shadAct.getApplicationContext(), UsersHistory.class);
        intent.putExtra(FacebookLogin.USER_F_NAME, "Russell");
        intent.putExtra(FacebookLogin.USER_ID, "1253227638023946");
        intent.putExtra(FacebookLogin.POOL_ID, poolID);
        UsersHistory hist = Robolectric.buildActivity(UsersHistory.class).withIntent(intent).create().get();

        hist.initializeGlobals();
        ListView items = (ListView) hist.findViewById(R.id.list);
        assertNotNull(items);
        ShadowListView lists = (ShadowListView)ShadowExtractor.extract(items);
        lists.populateItems();
        //Tests to see if the list is not empty
        assertTrue("pass", items.getCount() > 0);

        //Prints to the console to check if items are working
        //This is where im having trouble grabbing the actual values of the view
        //It seems like Franklin was found but im not sure
        ShadowLog.stream = System.out;
        ShadowLog.d("data", String.valueOf(items));
        ShadowLog.d("data", String.valueOf(lists.findIndexOfItemContainingText("Franklin")));

    }
    @Test
    public void testFacebookLogin(){

        ShadowApplication shadAct = ShadowApplication.getInstance().getShadowApplication();
        Intent intent = new Intent(shadAct.getApplicationContext(), FacebookLogin.class);
        intent.putExtra(FacebookLogin.USER_F_NAME, "Russell");
        intent.putExtra(FacebookLogin.USER_ID, "1253227638023946");
        intent.putExtra(FacebookLogin.POOL_ID, poolID);
        FacebookLogin face = Robolectric.buildActivity(FacebookLogin.class).withIntent(intent).create().get();
        Button login = (Button) face.findViewById(R.id.login_button);

        Intent expectedIntent = new Intent(shadAct.getApplicationContext(),LobbyActivity.class);
        face.initializeGlobals();
        assertNotSame(shadAct.getNextStartedActivity(),expectedIntent);
        //login.performClick();
        shadAct.startActivity(face.loginSuccess);
        assertEquals(shadAct.getNextStartedActivity(), expectedIntent);

    }
}
