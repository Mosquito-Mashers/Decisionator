package com.csulb.decisionator.decisionator;

import junit.framework.TestCase;

import org.json.JSONObject;
import org.junit.Test;

import java.util.ArrayList;

/**
 * Created by Jose on 3/7/2016.
 */
public class ProfileTest extends TestCase {


    public uProfile data = new uProfile();

    public User data1 = new User();
    public User data2 = new User();

    @Test
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
    }

    @Test
    public void testSetUserID() throws Exception {

        //pratice
        data.setUserID("jose-");
        String actual = "jose-1";
        assertNotSame(data.getUserID(), actual);

        data.setUserID("jose-1");
        assertSame(data.getUserID(), actual);
    }

    @Test
    public void testVariousVariables() throws Exception {
        //right data
        data1.setUserID("russell-2345");
        data1.setfName("Russell");
        data1.setlName("Tang");
        data1.setLatitude(33.778347);
        data1.setLongitude(118.184932);

        //incorrect data
        data2.setUserID("russell-");
        data2.setfName("Russel");
        data2.setlName("Tan");
        data2.setLatitude(33.779);
        data2.setLongitude(118.19);

        assertEquals("russell-2345", data1.getUserID());
        assertNotSame(data2.getUserID(), data1.getUserID());

        assertEquals("Russell", data1.getfName());
        assertNotSame(data1.getfName(),data2.getfName());

        assertEquals("Tang", data1.getlName());
        assertNotSame(data1.getlName(), data2.getlName());

        assertEquals(33.778347, data1.getLatitude());
        assertNotSame(data1.getLatitude(), data2.getLatitude());

        assertEquals(118.184932, data1.getLongitude());
        assertNotSame(data1.getLongitude(), data2.getLongitude());

    }

    //Lab 4, part 1 -- unit tests

    /*
    * Test Case Number	Sprint 3 Test Case 1 – User Story #1
    * As a user I want to be able to see the results of the event in a histogram
    * */
    public void testResultGraphFragment() throws Exception{
        //seems to be covered in WordCloudTest...
    }

    /*
    * Test Case Number	Sprint 3 Test Case 4 – User Story #4
    * As a user I want to be able to join public events
    * */
    public void testUserHistory() throws Exception {
        //Initializing unit under test
    }
    /**
     * Test Case Number	Sprint 2 Test Case 3 – User Story #3 - A
     * As a user I want to be able to provide a general category for the event
     */
    @Test
    public void testEventCatagory() {
        //initialize unit under test
        EventActivity uut = new EventActivity();

        //setting up test variables
        ArrayList<JSONObject> result = new ArrayList<JSONObject>();
        Event testEvent = new Event();
        testEvent.setDateCreated("01022015");
        testEvent.setTopic("Party at TGI Fridays!");
        testEvent.setCategory("Food");
        testEvent.setLatitude(10);
        testEvent.setLongitude(10);
        String api_key = "AIzaSyCpKblHKkLlan0H33WsA_yPgkDe4K6-C38";

        //simulated category selection in JSON
        String type = testEvent.getCategory();
        String query = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?";
        query += "keyword=" + testEvent.getTopic().replace(' ','+');
        query += "&location="+testEvent.getLatitude() + "," + testEvent.getLongitude();
        query += "&rankby=distance";
        query += "&key="+api_key;
        result = uut.getJSON(query);

        //test asserstions
        assertNotNull(result);
        assertEquals("Food", type);
    }
}

