package com.csulb.decisionator.decisionator;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;

/**
 * Created by Russell on 2/22/2016.
 */
@DynamoDBTable(tableName = "Events")
public class Event {
    private String eventID;
    private String hostID;
    private String hostName;
    private String attendees;
    private String rsvpList;
    private String topic;
    private String dateCreated;
    private String category;
    private String viewedList;
    private double latitude;
    private double longitude;
    private boolean isPrivate;

    @DynamoDBHashKey(attributeName = "eventID")
    public String getEventID() {
        return eventID;
    }

    public void setEventID(String eventID) {
        this.eventID = eventID;
    }

    @DynamoDBAttribute(attributeName = "hostID")
    public String getHostID() {
        return hostID;
    }

    public void setHostID(String hostID) {
        this.hostID = hostID;
    }

    @DynamoDBAttribute(attributeName = "hostName")
    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    @DynamoDBAttribute(attributeName = "attendees")
    public String getAttendees() {
        return attendees;
    }

    public void setAttendees(String attendees) {
        this.attendees = attendees;
    }

    @DynamoDBAttribute(attributeName = "rsvpList")
    public String getRsvpList() {
        return rsvpList;
    }

    public void setRsvpList(String rsvpList) {
        this.rsvpList = rsvpList;
    }

    @DynamoDBAttribute(attributeName = "viewedList")
    public String getViewedList() {
        return viewedList;
    }

    public void setViewedList(String viewedList) { this.viewedList = viewedList; }

    @DynamoDBAttribute(attributeName = "topic")
    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    @DynamoDBAttribute(attributeName = "dateCreated")
    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    @DynamoDBAttribute(attributeName = "category")
    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
    @DynamoDBAttribute(attributeName = "latitude")
    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    @DynamoDBAttribute(attributeName = "longitude")
    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    @DynamoDBAttribute(attributeName = "isPrivate")
    public boolean isPrivate() {
        return isPrivate;
    }

    public void setIsPrivate(boolean isPrivate) {
        this.isPrivate = isPrivate;
    }
}
