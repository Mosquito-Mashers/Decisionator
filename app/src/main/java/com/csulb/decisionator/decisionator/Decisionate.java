package com.csulb.decisionator.decisionator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

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

    public HashMap<String,Integer> accumulatePoints()
    {
        int j,k,m,runningTotal;
        HashMap<String,Integer> pointsPerVenue = new HashMap<String,Integer>();
        String wordsInVenue[];


        for(k = 0; k < possiblities.size(); k++)
        {
            runningTotal = 0;
            wordsInVenue = possiblities.get(k).split(" ");

            for(j = 0; j < wordsInVenue.length; j++)
            {
                for(m = 0; m < cloudData.size(); m++)
                {
                    if(wordsInVenue[j].toLowerCase().contentEquals(cloudData.get(m)))
                    {
                        runningTotal++;
                    }
                }
            }
            pointsPerVenue.put(possiblities.get(k),runningTotal);

        }

        pointsPerVenue = this.sortHashMapByValuesD(pointsPerVenue);
        return pointsPerVenue;
    }

    public LinkedHashMap sortHashMapByValuesD(HashMap passedMap) {
        List mapKeys = new ArrayList(passedMap.keySet());
        List mapValues = new ArrayList(passedMap.values());
        Collections.sort(mapValues);
        Collections.sort(mapKeys);

        LinkedHashMap sortedMap = new LinkedHashMap();

        Iterator valueIt = mapValues.iterator();
        while (valueIt.hasNext()) {
            Object val = valueIt.next();
            Iterator keyIt = mapKeys.iterator();

            while (keyIt.hasNext()) {
                Object key = keyIt.next();
                String comp1 = passedMap.get(key).toString();
                String comp2 = val.toString();

                if (comp1.equals(comp2)){
                    passedMap.remove(key);
                    mapKeys.remove(key);
                    sortedMap.put((String)key, (Integer)val);
                    break;
                }

            }

        }
        return sortedMap;
    }
}
