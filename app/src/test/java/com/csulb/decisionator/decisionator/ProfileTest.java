package com.csulb.decisionator.decisionator;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.PaginatedScanList;
import com.csulb.decisionator.decisionator.UsersHistory;
import com.google.android.gms.maps.GoogleMap;

import junit.framework.TestCase;

import android.content.ComponentName;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.ActionProvider;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Jose on 3/7/2016.
 */
public class ProfileTest extends TestCase {


    public uProfile data = new uProfile();

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
        assertNotSame(data.getUserID(), actual);

        data.setUserID("jose-1");
        assertSame(data.getUserID(), actual);
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
        friendEventActivity uut = new friendEventActivity();


        //setting up test variables
        MenuItem item = new MenuItem() {
            @Override
            public int getItemId() {
                return 0;
            }

            @Override
            public int getGroupId() {
                return 0;
            }

            @Override
            public int getOrder() {
                return 0;
            }

            @Override
            public MenuItem setTitle(CharSequence title) {
                return null;
            }

            @Override
            public MenuItem setTitle(int title) {
                return null;
            }

            @Override
            public CharSequence getTitle() {
                return null;
            }

            @Override
            public MenuItem setTitleCondensed(CharSequence title) {
                return null;
            }

            @Override
            public CharSequence getTitleCondensed() {
                return null;
            }

            @Override
            public MenuItem setIcon(Drawable icon) {
                return null;
            }

            @Override
            public MenuItem setIcon(int iconRes) {
                return null;
            }

            @Override
            public Drawable getIcon() {
                return null;
            }

            @Override
            public MenuItem setIntent(Intent intent) {
                return null;
            }

            @Override
            public Intent getIntent() {
                return null;
            }

            @Override
            public MenuItem setShortcut(char numericChar, char alphaChar) {
                return null;
            }

            @Override
            public MenuItem setNumericShortcut(char numericChar) {
                return null;
            }

            @Override
            public char getNumericShortcut() {
                return 0;
            }

            @Override
            public MenuItem setAlphabeticShortcut(char alphaChar) {
                return null;
            }

            @Override
            public char getAlphabeticShortcut() {
                return 0;
            }

            @Override
            public MenuItem setCheckable(boolean checkable) {
                return null;
            }

            @Override
            public boolean isCheckable() {
                return false;
            }

            @Override
            public MenuItem setChecked(boolean checked) {
                return null;
            }

            @Override
            public boolean isChecked() {
                return false;
            }

            @Override
            public MenuItem setVisible(boolean visible) {
                return null;
            }

            @Override
            public boolean isVisible() {
                return false;
            }

            @Override
            public MenuItem setEnabled(boolean enabled) {
                return null;
            }

            @Override
            public boolean isEnabled() {
                return false;
            }

            @Override
            public boolean hasSubMenu() {
                return false;
            }

            @Override
            public SubMenu getSubMenu() {
                return null;
            }

            @Override
            public MenuItem setOnMenuItemClickListener(OnMenuItemClickListener menuItemClickListener) {
                return null;
            }

            @Override
            public ContextMenu.ContextMenuInfo getMenuInfo() {
                return null;
            }

            @Override
            public void setShowAsAction(int actionEnum) {

            }

            @Override
            public MenuItem setShowAsActionFlags(int actionEnum) {
                return null;
            }

            @Override
            public MenuItem setActionView(View view) {
                return null;
            }

            @Override
            public MenuItem setActionView(int resId) {
                return null;
            }

            @Override
            public View getActionView() {
                return null;
            }

            @Override
            public MenuItem setActionProvider(ActionProvider actionProvider) {
                return null;
            }

            @Override
            public ActionProvider getActionProvider() {
                return null;
            }

            @Override
            public boolean expandActionView() {
                return false;
            }

            @Override
            public boolean collapseActionView() {
                return false;
            }

            @Override
            public boolean isActionViewExpanded() {
                return false;
            }

            @Override
            public MenuItem setOnActionExpandListener(OnActionExpandListener listener) {
                return null;
            }
        };
        boolean itemSelected = uut.onOptionsItemSelected(item);

        //assertion tests
        assertFalse(itemSelected);
    }

    /**
     * Test Case Number	Sprint 2 Test Case 3 – User Story #3 - A
     * As a user I want to be able to provide a general category for the event
     */
    public void testEventCatagory() {
        //initialize unit under test
        EventActivity uut = new EventActivity();

        //setting up test variables
        GoogleMap map;
        ArrayList<JSONObject> result = new ArrayList<JSONObject>();
        ArrayList<JSONObject> places;
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
        assertEquals("Food", testEvent.getCategory());
    }
}

