package com.csulb.decisionator.decisionator;

import junit.framework.TestCase;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.marshallers.CalendarSetToStringSetMarshaller;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.*;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.*;
import com.amazonaws.services.dynamodbv2.model.*;

import android.content.Context;

import org.junit.Test;
import org.junit.runners.model.TestClass;

/**
 * Created by Jose on 3/7/2016.
 */
public class ProfileTest extends TestCase {


    public Profile data = new Profile();

    public User data1 = new User();
    public User data2 = new User();

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

    public void testSetUserID() throws Exception {

        //pratice
        data.setUserID("jose-");
        String actual = "jose-1";
        assertNotSame(data.getUserID(),actual);

        data.setUserID("jose-1");
        assertSame(data.getUserID(),actual);
    }

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



}

