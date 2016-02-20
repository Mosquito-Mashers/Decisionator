package com.csulb.decisionator.decisionator.backend;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

import java.util.ArrayList;

/**
 * Created by Russell-Test on 2/20/2016.
 */
@Entity
public class User {
    @Id
    private String userName;
    private String userPermissionData;
    private double userLat;
    private double userLong;
    private ArrayList<String> userPersonalityProfile;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPermissionData() {
        return userPermissionData;
    }

    public void setUserPermissionData(String userPermissionData) {
        this.userPermissionData = userPermissionData;
    }

    public double getUserLat() {
        return userLat;
    }

    public void setUserLat(double userLat) {
        this.userLat = userLat;
    }

    public double getUserLong() {
        return userLong;
    }

    public void setUserLong(double userLong) {
        this.userLong = userLong;
    }

    public ArrayList<String> getUserPersonalityProfile() {
        return userPersonalityProfile;
    }

    public void setUserPersonalityProfile(ArrayList<String> userPersonalityProfile) {
        this.userPersonalityProfile = userPersonalityProfile;
    }
}
