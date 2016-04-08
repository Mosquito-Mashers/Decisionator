package com.csulb.decisionator.decisionator;

import java.util.ArrayList;

/**
 * Created by Russell-Test on 4/7/2016.
 */
public class Decisionate
{
    private ArrayList <String>cloudData;
    private ArrayList<String> possiblities;
    private String finalDecision;
    private double finalLat;
    private double finalLon;

    public Decisionate(){}

    public Decisionate(ArrayList<String> cloudItems, ArrayList<String> possiblePlaces, String finalDec, double lat, double lon)
    {
        cloudData = cloudItems;
        possiblities = possiblePlaces;
        finalDecision = finalDec;
        finalLat = lat;
        finalLon = lon;
    }

    public void accumulatePoints()
    {
        int j,k;

        for(k = 0; k < possiblities.size(); k++)
        {

        }
    }
}
