package com.csulb.decisionator.decisionator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by Russell-Tan on 4/7/2016.
 */
public class Decisionate
{
    private ArrayList <String>cloudData;
    private ArrayList<String> possibilities;

    public Decisionate(ArrayList<String> cloudItems, ArrayList<String> possiblePlaces)
    {
        cloudData = cloudItems;
        possibilities = possiblePlaces;
    }

    public HashMap<String,Integer> accumulatePoints()
    {
        int j,k,m,runningTotal;
        HashMap<String,Integer> pointsPerVenue = new HashMap<String,Integer>();
        String wordsInVenue[];

        //Iterates through the venues
        for(k = 0; k < possibilities.size(); k++)
        {
            runningTotal = 0;
            wordsInVenue = possibilities.get(k).split(" ");

            //Iterates through each word in the venue
            for(j = 0; j < wordsInVenue.length; j++)
            {
                //Iterates through each word in the combined tag data for all the users
                for(m = 0; m < cloudData.size(); m++)
                {
                    if(wordsInVenue[j].toLowerCase().contentEquals(cloudData.get(m)))
                    {
                        runningTotal++;
                    }
                }
            }
            pointsPerVenue.put(possibilities.get(k),runningTotal);
        }

        pointsPerVenue = this.sortHashMapByValues(pointsPerVenue);
        return pointsPerVenue;
    }

    public LinkedHashMap sortHashMapByValues(HashMap passedMap) {
        List mapKeys = new ArrayList(passedMap.keySet());
        List mapValues = new ArrayList(passedMap.values());
        Collections.sort(mapValues,Collections.reverseOrder());
        Collections.sort(mapKeys,Collections.reverseOrder());

        LinkedHashMap sortedMap = new LinkedHashMap();

        Iterator valueIt = mapValues.iterator();
        while (valueIt.hasNext()) {
            Object val = valueIt.next();
            Iterator keyIt = mapKeys.iterator();

            while (keyIt.hasNext()) {
                Object key = keyIt.next();
                String comp1 = passedMap.get(key).toString();
                String comp2 = val.toString();

                if (comp2.equals(comp1)){
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
