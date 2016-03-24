package com.csulb.decisionator.decisionator;

import android.app.Application;

import android.location.Location;
import android.test.ApplicationTestCase;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBScanExpression;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.PaginatedScanList;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.facebook.FacebookSdk;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;



/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
    private CognitoCachingCredentialsProvider credentialsProvider;
    private AmazonDynamoDBClient ddbClient;
    private DynamoDBMapper mapper;

    public ApplicationTest() {
        super(Application.class);
    }

    @Override
    public void setUp() throws Exception {
        FacebookSdk.sdkInitialize(getContext());
        CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                getContext(),   /* get the context for the application */
                "us-east-1:a74e3f8c-6c2b-40b6-89d5-46d4f870a6f2", // Identity Pool ID
                Regions.US_EAST_1           /* Region for your identity pool--US_EAST_1 or EU_WEST_1*/
        );

        Map<String, String> logins = new HashMap<>();
        logins.put("graph.facebook.com", "CAAHqhnf8GfgBAMmRT0g9sVZAfFvijZANLuaQgbEUxLzsNVdXxtNMxW4GguBh2lEBVI7iKUQX6HBPEt7g9uxGZCcmSJzalX2gJ12vHChsyKoyKAvukddQjbZB8gkpRy8XqCC8cqldWwWyFRmopH92ZAAIssGhvuHQ94aWISuXh5fgdEENEoJK4");
        credentialsProvider.setLogins(logins);
        ddbClient = new AmazonDynamoDBClient(credentialsProvider);
        mapper = new DynamoDBMapper(ddbClient);
    }

    public void test_getMidpoint() throws Exception {
        //Initializing test locations
        Location loc1 = new Location("");
        Location loc2 = new Location("");
        Location result = new Location("");
        loc1.setLatitude(10.0);
        loc1.setLongitude(10.0);
        loc2.setLatitude(20.0);
        loc2.setLongitude(20.0);
        ArrayList<Location> testLocations = new ArrayList<Location>();
        testLocations.add(loc1);
        testLocations.add(loc2);

        //Initializing unit under test
        EventActivity test = new EventActivity();

        //Passing test values to unit under test
        result = test.getMidLocation(testLocations);

        //comparing results to expected results

         //Are these correct assumed values?
        assertEquals(15.0, result.getLongitude());
        assertEquals(15.0, result.getLatitude());

    }

    public void test_DB_userGet() throws Exception{
        String hash = "russell-2345";
        User temp = mapper.load(User.class, hash);
        String ID = temp.getUserID();
        String wID = "russell-2";

        //test userID
        assertEquals(ID, hash);
        assertNotSame(wID, ID);

        String FName = "Russell";
        String fName = temp.getfName();
        String wFName = "Russ";

        //test first name
        assertEquals(fName, FName);
        assertNotSame(wFName, fName);

        String LName = "Tang";
        String lName = temp.getlName();
        String wLName = "Tan";

        //test last name
        assertEquals(lName, LName);
        assertNotSame(wLName, lName);

        Double Latitude = 33.778347;
        Double latitude = temp.getLatitude();
        Double wLatitude = 33.6000;

        //test latitude
        assertEquals(latitude, Latitude);
        assertNotSame(wLatitude, latitude);

        Double Longitude = -118.184932;
        Double longitude = temp.getLongitude();
        Double wLongitude = 118.184932;

        //test longitude
        assertEquals(longitude, Longitude);
        assertNotSame(wLongitude, longitude);

        String ProfilePic = "https://graph.facebook.com/1168940069/picture?height=250&width=250&migration_overrides=%7Boctober_2012%3Atrue%7D";
        String profilePic = temp.getProfilePic();
        String wProfilePic = "random";

        //test profile picture
        assertEquals(profilePic, ProfilePic);
        assertNotSame(wProfilePic, profilePic);
    }

    public void test_DB_createUser(){
        String ID = "Test";
        String fName = "Tester";
        String lName = "Testing";
        Double latitude = 33.0;
        Double longitude = -118.0;
        String profilePic = "Random";
        User temp = new User();
        temp.setUserID(ID);
        temp.setfName(fName);
        temp.setlName(lName);
        temp.setLatitude(latitude);
        temp.setLongitude(longitude);
        temp.setProfilePic(profilePic);
        //test creation of a new user
        mapper.save(temp);
        temp = mapper.load(User.class, ID);
        assertNotNull(temp);
    }

    public void test_DB_deleteUser(){
        User temp = new User();
        temp.setUserID("Test");
        temp.setfName("Tester");
        temp.setlName("Testing");
        temp.setLatitude(33.0);
        temp.setLongitude(-118.0);
        temp.setProfilePic("Random");
        //deletion of a created user
        mapper.delete(temp);
        assertNull(mapper.load(User.class, temp.getUserID()));
    }

    public void Sprint1TestCase3() {
        //Initializing unit under test
        InviteFriendsActivity uut = new InviteFriendsActivity();

        //Initializing test variables
        ArrayList<User> friends = new ArrayList<User>();
        String hash = "russell-2345";
        User user = mapper.load(User.class, hash);
        String ID = user.getUserID();

        //database stuff
        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
        PaginatedScanList<User> result = mapper.scan(User.class, scanExpression);

        //getAllFriends() mock implementation
        for (int k = 0; k < result.size(); k++)
        {
            User item = result.get(k);
            if (!item.getUserID().contentEquals(ID))
            {
                friends.add(item);
            }
        }

        assertNotNull(friends);
    }
}
