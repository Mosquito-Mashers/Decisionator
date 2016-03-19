package com.csulb.decisionator.decisionator;

import android.app.Application;
import android.test.ApplicationTestCase;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.facebook.FacebookSdk;

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
        logins.put("graph.facebook.com", "CAAHqhnf8GfgBAHLRGJ6nbJBKv6S8sitdxdnLuGioZCaZBcRx9VtQrNxe9xfmyS9KVEhm7rCJmY9ZArZBeVbXoT7XprePE0r6xfw8oMm6rAdGECZBitRp2NBCAAf1FytDWZBPWw6Bz6nYv9mXWaLKmtQk7gFwjMzalYslSmCZAuBTCLlvXwPyPAnSQpUDIpBETw0QC6ZCYxZAuCwZDZD");
        credentialsProvider.setLogins(logins);
        ddbClient = new AmazonDynamoDBClient(credentialsProvider);
        mapper = new DynamoDBMapper(ddbClient);
    }

    public void test_DB_userGet() throws Exception{
        //test
        String hash = "russell-2345";
        User temp = mapper.load(User.class, hash);
        String check = temp.getUserID();

        String check1 = temp.getfName();
        String check1_1 = "Russ";
        User temp1 = new User();
        temp1.setUserID("russell-2345");

        assertEquals(check,hash);
        assertNotSame(check1, check1_1);

        check1 = temp.getlName();
        check1_1 = "Tang";

        assertEquals(check1, check1_1);
    }



}