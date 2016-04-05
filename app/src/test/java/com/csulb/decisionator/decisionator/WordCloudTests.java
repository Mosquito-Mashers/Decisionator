package com.csulb.decisionator.decisionator;

import android.text.SpannableString;

import junit.framework.TestCase;

import org.junit.Test;

import java.util.Iterator;
import java.util.Map;

/**
 * Created by Russell on 4/4/2016.
 */
public class WordCloudTests extends TestCase {
    private String SampleData;

    @Override
    public void setUp()
    {
        SampleData = "Burger, Burger, burger, Burger, Bgr, Thai Food, Burger, burger, BuRgEr";
    }

    @Test
    public void testEndToEndCloud()
    {
        WordCloudGenerator cloudGen = new WordCloudGenerator(SampleData,null);
        cloudGen.createFrequencyMap();
        SpannableString mySpan = cloudGen.getSpannableString();
        Iterator setIter = cloudGen.getSortedMap().entrySet().iterator();

        int maxCount = Integer.parseInt(((Map.Entry) setIter.next()).getValue().toString());

        assertNotNull(mySpan);
        assertEquals(maxCount,7);

    }
}
