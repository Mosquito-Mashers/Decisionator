package com.csulb.decisionator.decisionator;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;

/**
 * Created by Russell on 2/22/2016.
 */
@DynamoDBTable(tableName = "Profiles")
public class Profile {
    private String userID;
    private String imageTags;
    private String textTags;
    private String likeTags;

    @DynamoDBHashKey(attributeName = "userID")
    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    @DynamoDBAttribute(attributeName = "imageTags")
    public String getImageTags() {
        return imageTags;
    }

    public void setImageTags(String imageTags) {
        this.imageTags = imageTags;
    }

    @DynamoDBAttribute(attributeName = "textTags")
    public String getTextTags() {
        return textTags;
    }

    public void setTextTags(String textTags) {
        this.textTags = textTags;
    }

    @DynamoDBAttribute(attributeName = "likeTags")
    public String getLikeTags() {
        return likeTags;
    }

    public void setLikeTags(String likeTags) {
        this.likeTags = likeTags;
    }
}
