package com.csulb.decisionator.decisionator;

import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Created by Russell on 4/4/2016.
 */
public class WordCloudGenerator
{

    private String rawText;
    private TreeMap<String,Integer> frequencyMap;
    private Map sortedMap;
    private ArrayList<String> stopWordsList;
    private SpannableString cloudString;
    /**
     * Constructors
     * @param raw
     */
    public WordCloudGenerator(String raw)
    {
        rawText = raw;

        frequencyMap = new TreeMap<String, Integer>();
        stopWordsList = new ArrayList<String>();
        stopWordsList.add("");
        stopWordsList.add("&");
        stopWordsList.add("-");
        stopWordsList.add("a");
        stopWordsList.add("about");
        stopWordsList.add("above");
        stopWordsList.add("after");
        stopWordsList.add("again");
        stopWordsList.add("against");
        stopWordsList.add("all");
        stopWordsList.add("am");
        stopWordsList.add("an");
        stopWordsList.add("anaheim");
        stopWordsList.add("and");
        stopWordsList.add("any");
        stopWordsList.add("are");
        stopWordsList.add("aren't");
        stopWordsList.add("as");
        stopWordsList.add("at");
        stopWordsList.add("be");
        stopWordsList.add("beach");
        stopWordsList.add("because");
        stopWordsList.add("been");
        stopWordsList.add("before");
        stopWordsList.add("being");
        stopWordsList.add("below");
        stopWordsList.add("between");
        stopWordsList.add("both");
        stopWordsList.add("but");
        stopWordsList.add("by");
        stopWordsList.add("california");
        stopWordsList.add("can't");
        stopWordsList.add("cannot");
        stopWordsList.add("could");
        stopWordsList.add("couldn't");
        stopWordsList.add("did");
        stopWordsList.add("didn't");
        stopWordsList.add("do");
        stopWordsList.add("does");
        stopWordsList.add("doesn't");
        stopWordsList.add("doing");
        stopWordsList.add("don't");
        stopWordsList.add("down");
        stopWordsList.add("during");
        stopWordsList.add("each");
        stopWordsList.add("few");
        stopWordsList.add("for");
        stopWordsList.add("from");
        stopWordsList.add("further");
        stopWordsList.add("had");
        stopWordsList.add("hadn't");
        stopWordsList.add("has");
        stopWordsList.add("hasn't");
        stopWordsList.add("have");
        stopWordsList.add("haven't");
        stopWordsList.add("having");
        stopWordsList.add("he");
        stopWordsList.add("he'd");
        stopWordsList.add("he'll");
        stopWordsList.add("he's");
        stopWordsList.add("her");
        stopWordsList.add("here");
        stopWordsList.add("here's");
        stopWordsList.add("hers");
        stopWordsList.add("herself");
        stopWordsList.add("him");
        stopWordsList.add("himself");
        stopWordsList.add("his");
        stopWordsList.add("how");
        stopWordsList.add("how's");
        stopWordsList.add("i");
        stopWordsList.add("i'd");
        stopWordsList.add("i'll");
        stopWordsList.add("i'm");
        stopWordsList.add("i've");
        stopWordsList.add("if");
        stopWordsList.add("in");
        stopWordsList.add("into");
        stopWordsList.add("is");
        stopWordsList.add("isn't");
        stopWordsList.add("it");
        stopWordsList.add("it's");
        stopWordsList.add("its");
        stopWordsList.add("itself");
        stopWordsList.add("let's");
        stopWordsList.add("long");
        stopWordsList.add("me");
        stopWordsList.add("more");
        stopWordsList.add("most");
        stopWordsList.add("mustn't");
        stopWordsList.add("my");
        stopWordsList.add("myself");
        stopWordsList.add("no");
        stopWordsList.add("nor");
        stopWordsList.add("not");
        stopWordsList.add("of");
        stopWordsList.add("off");
        stopWordsList.add("on");
        stopWordsList.add("once");
        stopWordsList.add("only");
        stopWordsList.add("or");
        stopWordsList.add("other");
        stopWordsList.add("ought");
        stopWordsList.add("our");
        stopWordsList.add("ours	ourselves");
        stopWordsList.add("out");
        stopWordsList.add("over");
        stopWordsList.add("own");
        stopWordsList.add("same");
        stopWordsList.add("seattle");
        stopWordsList.add("shan't");
        stopWordsList.add("she");
        stopWordsList.add("she'd");
        stopWordsList.add("she'll");
        stopWordsList.add("she's");
        stopWordsList.add("should");
        stopWordsList.add("shouldn't");
        stopWordsList.add("so");
        stopWordsList.add("some");
        stopWordsList.add("such");
        stopWordsList.add("university");
        stopWordsList.add("than");
        stopWordsList.add("that");
        stopWordsList.add("that's");
        stopWordsList.add("the");
        stopWordsList.add("their");
        stopWordsList.add("theirs");
        stopWordsList.add("them");
        stopWordsList.add("themselves");
        stopWordsList.add("then");
        stopWordsList.add("there");
        stopWordsList.add("there's");
        stopWordsList.add("these");
        stopWordsList.add("they");
        stopWordsList.add("they'd");
        stopWordsList.add("they'll");
        stopWordsList.add("they're");
        stopWordsList.add("they've");
        stopWordsList.add("this");
        stopWordsList.add("those");
        stopWordsList.add("through");
        stopWordsList.add("to");
        stopWordsList.add("too");
        stopWordsList.add("under");
        stopWordsList.add("until");
        stopWordsList.add("up");
        stopWordsList.add("ventura");
        stopWordsList.add("very");
        stopWordsList.add("was");
        stopWordsList.add("wasn't");
        stopWordsList.add("we");
        stopWordsList.add("we'd");
        stopWordsList.add("we'll");
        stopWordsList.add("we're");
        stopWordsList.add("we've");
        stopWordsList.add("were");
        stopWordsList.add("weren't");
        stopWordsList.add("what");
        stopWordsList.add("what's");
        stopWordsList.add("when");
        stopWordsList.add("when's");
        stopWordsList.add("where");
        stopWordsList.add("where's");
        stopWordsList.add("which");
        stopWordsList.add("while");
        stopWordsList.add("who");
        stopWordsList.add("who's");
        stopWordsList.add("whom");
        stopWordsList.add("why");
        stopWordsList.add("why's");
        stopWordsList.add("with");
        stopWordsList.add("won't");
        stopWordsList.add("would");
        stopWordsList.add("wouldn't");
        stopWordsList.add("you");
        stopWordsList.add("you'd");
        stopWordsList.add("you'll");
        stopWordsList.add("you're");
        stopWordsList.add("you've");
        stopWordsList.add("your");
        stopWordsList.add("yours");
        stopWordsList.add("yourself");
        stopWordsList.add("yourselves");
    }

    public Map getSortedMap() {
        sortedMap = sortByValues(frequencyMap);
        return sortedMap;
    }

    public String[] splitAndTrimText()
    {
        String noStops = this.removeStopWords(rawText.toLowerCase().replaceAll(",", " "));
        String rawSplit[] = noStops.split(" ");

        return rawSplit;
    }

    public String removeStopWords(String input)
    {
        for(String stopWord : stopWordsList){
            input = input.replaceAll(" "+ stopWord + " ", " ");
        }

        return input;
    }

    public void createFrequencyMap()
    {
        int k;
        int freq;
        String allText[] = this.splitAndTrimText();

        for(k = 0; k < allText.length; k++)
        {
            String temp = allText[k].trim().toLowerCase();

            if(frequencyMap.get(temp) == null)
            {
                frequencyMap.put(temp,1);
            }
            else
            {
                freq = frequencyMap.get(temp);
                frequencyMap.put(temp,freq+1);
            }
        }
    }

    public SpannableString getSpannableString()
    {
        int currPointer = 0;
        String tempSpan = "";

        TreeMap<String,Integer> tempMap = new TreeMap<String, Integer>();

        Iterator findImportant = frequencyMap.entrySet().iterator();

        while( findImportant.hasNext())
        {
            Map.Entry me = (Map.Entry)findImportant.next();
            if((int)me.getValue() > 1 )
            {
                tempMap.put((String)me.getKey(),(int)me.getValue());
            }
        }

        frequencyMap = tempMap;

        sortedMap = sortByValues(frequencyMap);

        // Get an iterator
        Iterator i = sortedMap.entrySet().iterator();

        // Display elements
        while(i.hasNext()) {
            Map.Entry me = (Map.Entry)i.next();
            tempSpan += me.getKey()+" ";
        }

        cloudString = new SpannableString(tempSpan);
        // Get an iterator
        i = sortedMap.entrySet().iterator();

        // Display elements
        while(i.hasNext()) {
            Map.Entry me = (Map.Entry) i.next();

            int len = me.getKey().toString().length();
            int size = Integer.parseInt(me.getValue().toString());
            cloudString.setSpan(new RelativeSizeSpan(size), currPointer, currPointer + len, 0);
            currPointer += len+1;
        }
        return cloudString;
    }

    public SpannableString getSpannableString(Map<String,Integer> venueMap)
    {
        int k;
        int maxSize;
        int minSize;
        int currPointer = 0;
        String tempSpan = "";

        Iterator m = venueMap.entrySet().iterator();
        while(m.hasNext()) {
            Map.Entry me = (Map.Entry)m.next();
            tempSpan += me.getKey()+" ";
        }

        cloudString = new SpannableString(tempSpan);
        // Get an iterator
        Iterator i = venueMap.entrySet().iterator();

        // Display elements
        while(i.hasNext()) {
            Map.Entry me = (Map.Entry) i.next();
            System.out.print(me.getKey() + ": ");
            System.out.println(me.getValue());

            int len = me.getKey().toString().length();
            int size = Integer.parseInt(me.getValue().toString());
            if(size < 1)
            {
                size = 2;
            }
            cloudString.setSpan(new RelativeSizeSpan(size), currPointer, currPointer + len, 0);
            currPointer += len+1;
        }
        return cloudString;
    }

    //Method for sorting the TreeMap based on values
    public static <K, V extends Comparable<V>> Map<K, V>
    sortByValues(final Map<K, V> map) {
        Comparator<K> valueComparator =
                new Comparator<K>() {
                    public int compare(K k1, K k2) {
                        int compare =
                                map.get(k2).compareTo(map.get(k1));
                        if (compare == 0)
                            return 1;
                        else
                            return compare;
                    }
                };

        Map<K, V> sortedByValues =
                new TreeMap<K, V>(valueComparator);
        sortedByValues.putAll(map);
        return sortedByValues;
    }

    public String buildSmallMap()
    {
        String items = "";
        Set set = this.getSortedMap().entrySet();

        // Get an iterator
        Iterator i = set.iterator();

        int count = 0;
        // Display elements
        while(i.hasNext()) {
            if(count < 10) {
                Map.Entry me = (Map.Entry) i.next();
                items += me.getKey() + ": " + me.getValue() + "\n";
            }
            else
            {
                break;
            }
            count++;
        }
        return items;
    }

}
