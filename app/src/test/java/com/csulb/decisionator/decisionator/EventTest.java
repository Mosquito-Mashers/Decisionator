package com.csulb.decisionator.decisionator;

import android.location.Location;

import junit.framework.TestCase;

import java.util.ArrayList;

/**
 * Created by Jose on 3/7/2016.
 */
public class EventTest extends TestCase {


    public void test_getMidpoint() throws Exception {
        //Initializing test locations
        Location loc1 = new Location("");
        Location loc2 = new Location("");
        Location result;
        loc1.setLatitude(10);
        loc1.setLongitude(10);
        loc2.setLatitude(20);
        loc2.setLongitude(20);
        ArrayList<Location> testLocations = new ArrayList<Location>();
        testLocations.add(loc1);
        testLocations.add(loc2);

        //Initializing unit under test
        EventActivity test = new EventActivity();

        //Passing test values to unit under test
        result = test.getMidLocation(testLocations);

        //comparing results to expected results
        assertEquals(15, result.getLongitude());
        assertEquals(15, result.getLatitude());
    }

    public void testGetEventID() throws Exception {
        
    }

    public void testSetEventID() throws Exception {

    }

    public void testGetHostID() throws Exception {

    }

    public void testSetHostID() throws Exception {

    }

    public void testGetHostName() throws Exception {

    }

    public void testSetHostName() throws Exception {

    }

    public void testGetAttendees() throws Exception {

    }

    public void testSetAttendees() throws Exception {

    }

    public void testGetRsvpList() throws Exception {

    }

    public void testSetRsvpList() throws Exception {

    }

    public void testGetTopic() throws Exception {

    }

    public void testSetTopic() throws Exception {

    }

    public void testGetDateCreated() throws Exception {

    }

    public void testSetDateCreated() throws Exception {

    }

    public void testGetCategory() throws Exception {

    }

    public void testSetCategory() throws Exception {

    }

    public void testGetLatitude() throws Exception {

    }

    public void testSetLatitude() throws Exception {

    }

    public void testGetLongitude() throws Exception {

    }

    public void testSetLongitude() throws Exception {

    }
}