package com.csulb.decisionator.decisionator;

import android.text.SpannableString;

import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Russell-Test on 4/10/2016.
 */
public class DecisionateTests extends TestCase {

    private ArrayList<String> SampleCloud;
    private String cloudString;
    private ArrayList<String> SampleChoices;

    @Override
    public void setUp()
    {
        //"Burger, Burger, burger, Burger, Bgr, Thai Food, Burger, burger, BuRgEr";
        SampleCloud = new ArrayList<String>();
        SampleChoices = new ArrayList<String>();

        cloudString = "Long Beach Aquarium,May's Thai Kitchen,In-N-Out Burger,May's Thai Kitchen,May's Thai Kitchen,Burger King,In-N-Out Burger,MVP's,May's Thai Kitchen,Universal Studios Hollywood";

        SampleChoices.add("Tipps thai restaurant");
        SampleChoices.add("May's Thai Kitchen");
        SampleChoices.add("Bai Plu");
        SampleChoices.add("Your Place restaurant");
        SampleChoices.add("Long Beach Thai");
    }

    public void testDecisionate()
    {

        WordCloudGenerator gen = new WordCloudGenerator(cloudString,SampleChoices);
        String cleaned = gen.removeStopWords(cloudString);
        String trimmed[] = gen.splitAndTrimText();
        ArrayList<String> cloudItems = new ArrayList<String>();
        cloudItems.addAll(Arrays.asList(trimmed));

        String decisionatedVenue = "";
        int decisionatedWeight = 0;
        Decisionate terminator = new Decisionate(cloudItems,SampleChoices,null,0,0);

        HashMap<String, Integer> decisionatedResult = terminator.accumulatePoints();

        SpannableString venueCloud = gen.getSpannableString(decisionatedResult);

        Iterator i = decisionatedResult.entrySet().iterator();
        while(i.hasNext()) {
            Map.Entry<String,Integer> me = (Map.Entry<String,Integer>) i.next();
            decisionatedVenue = me.getKey();
            decisionatedWeight = me.getValue();
            break;
        }

        assertEquals(decisionatedVenue, "May's Thai Kitchen");
        assertEquals(decisionatedWeight,12);
        assertNotNull(venueCloud);


    }
}
