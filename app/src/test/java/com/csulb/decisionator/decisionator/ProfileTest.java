package com.csulb.decisionator.decisionator;

import junit.framework.TestCase;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.*;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.*;
import com.amazonaws.services.dynamodbv2.model.*;

import android.content.Context;
import android.test.InstrumentationTestCase;
import android.test.mock.MockContext;
import android.util.EventLog;
import android.view.inputmethod.InputMethodSession;
import android.webkit.ConsoleMessage;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;



/**
 * Created by Jose on 3/7/2016.
 */
public class ProfileTest extends InstrumentationTestCase {

    public Profile data = new Profile();

    public User data1 = new User();

    MockContext context;

    public void testGetUserID() throws Exception {

        assertNull(data.getUserID());
        assertNotNull(data);

        String id = "12345";
        data.setUserID("12345");
        assertSame(data.getUserID(), id);

        String id1 = "russell-2345";
        data1.setUserID(id1);
        double latitude = 33.778347;
        data1.setLatitude(latitude);
        assertSame(data1.getUserID(), id1);
        assertEquals(data1.getLatitude(), latitude);

        context = new MockContext();
        assertNotNull(context);

        CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                context,   /* get the context for the application */
                "us-east-1:a74e3f8c-6c2b-40b6-89d5-46d4f870a6f2", // Identity Pool ID
                Regions.US_EAST_1           /* Region for your identity pool--US_EAST_1 or EU_WEST_1*/
        );
        AmazonDynamoDBClient ddbClient = new AmazonDynamoDBClient(credentialsProvider);
        DynamoDBMapper mapper = new DynamoDBMapper(ddbClient);

        //Need to figure this part out
        User temp = mapper.load(User.class,"russell-2345");
        String check = temp.getUserID();
        String check1 = temp.getfName();
        String check1_1 = "Russ";
        User temp1 = null;
        temp1.setUserID("russell-2345");

        assertEquals(check, id1);
        //should fail but passes
        assertEquals(check1, check1_1);


    }

    public void testSetUserID() throws Exception {

        data.setUserID("jose-");
        String actual = "jose-1";
        assertNotSame(data.getUserID(),actual);

        data.setUserID("jose-1");
        assertSame(data.getUserID(),actual);
    }

}

