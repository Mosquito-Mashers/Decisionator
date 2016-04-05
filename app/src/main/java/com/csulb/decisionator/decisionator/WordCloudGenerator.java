package com.csulb.decisionator.decisionator;

import java.util.TreeMap;

/**
 * Created by Russell on 4/4/2016.
 */
public class WordCloudGenerator
{

    private String rawText;
    TreeMap<String,Integer> frequencyMap;

    public WordCloudGenerator(String raw)
    {
        rawText = raw;
        frequencyMap = new TreeMap<String, Integer>();
    }

    public String getRawText()
    {
        return rawText;
    }

    public void setRawText(String rawText) {
        this.rawText = rawText;
    }

    public String splitAndTrimText()
    {
        String output = "";

        return output;
    }

    public void createFrequencyMap()
    {

    }

}
