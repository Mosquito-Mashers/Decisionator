package com.csulb.decisionator.decisionator;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
/**
 * Created by Ronald on 5/6/2016.
 */
public class PersonalityPieFragment extends Fragment {
    private PieChart ppChart;
    private String allUsersTagsStr = "";
    private String allMyTags = "";
    private String allFriendTags = "";
    private String allCommonTags = "";
    private Map<String,Integer> commonTagMap = new HashMap<String, Integer>();
    private Typeface tf;
    WordCloudGenerator myGen, friendGen;

    public static PersonalityPieFragment newInstance(Bundle b) {
        PersonalityPieFragment ppFrag = new PersonalityPieFragment();
        ppFrag.setArguments(b);

        return ppFrag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View ppView = inflater.inflate(R.layout.fragment_personality_pie, container, false);
        Bundle incoming = getArguments();
        ppChart = (PieChart) ppView.findViewById(R.id.profilepiechart);
        ppChart.setDescription("");
        Typeface tf = Typeface.createFromAsset(getActivity().getAssets(), "OpenSans-Light.ttf");
        ppChart.setCenterTextTypeface(tf);
        ppChart.setCenterText(generateCenterText());
        ppChart.setCenterTextSize(10f);
        ppChart.setCenterTextTypeface(tf);
        // radius of the center hole in percent of maximum radius
        ppChart.setHoleRadius(45f);
        ppChart.setTransparentCircleRadius(50f);
        ppChart.getLegend().setEnabled(false);

        if(incoming != null)
        {
            allUsersTagsStr = getArguments().getString(EventActivity.PERSONALITY_DATA);
            //Toast.makeText(getContext(), allUsersTagsStr, Toast.LENGTH_SHORT).show();
            allMyTags = getArguments().getString(EventActivity.CURRENT_USER_DATA);
            myGen = new WordCloudGenerator(allMyTags);
            allFriendTags = getArguments().getString(EventActivity.FRIEND_DATA);
            friendGen = new WordCloudGenerator(allFriendTags);
            myGen.createFrequencyMap();
            friendGen.createFrequencyMap();


            ppChart.setData(generatePieData());

        }

        return ppView;
    }
    public PieData generatePieData()
    {
        List<Entry> pieEntries = new ArrayList<Entry>();
        List<Integer> entryInt = new ArrayList<Integer>();
        PieDataSet ds1 = new PieDataSet(pieEntries, "Personality Analysis");
        List<String> xVals = new ArrayList<String>();
        PieData d = new PieData(xVals, ds1);
        Bundle incoming = getArguments();

        if(incoming != null)
        {
            allCommonTags = getArguments().getString(EventActivity.PERSONALITY_DATA);
            if(allCommonTags != "" && allCommonTags != null)
            {
                String raw[] = allCommonTags.split("\\|");
                for (int k = 0; k < raw.length; k++)
                {
                    String item[] = raw[k].split(",");
                    commonTagMap.put(item[0], Integer.parseInt(item[1]));
                }
            }
        }
        if(commonTagMap.size() > 0) {
            Iterator mapIter = commonTagMap.entrySet().iterator();
            xVals = new ArrayList<String>();
            int count = 0;
            while (mapIter.hasNext() && count < 10) {
                Map.Entry ent = (Map.Entry) mapIter.next();
                if((int)ent.getValue() > 0) {
                    pieEntries.add(new Entry((float) ((int) (ent.getValue())), count));
                    xVals.add(count, ent.getKey().toString());
                    count++;
                }
            }
            ds1 = new PieDataSet(pieEntries, "Profile Analysis");

            ds1.setColors(ColorTemplate.VORDIPLOM_COLORS);
            ds1.setSliceSpace(2f);
            ds1.setValueTextColor(Color.BLACK);
            ds1.setValueTextSize(12f);

            d = new PieData(xVals, ds1);
            d.setValueTypeface(tf);
        }
        return d;
    }



    public SpannableString generateCenterText() {
        SpannableString s = new SpannableString("Profile\nAnalysis");
        s.setSpan(new RelativeSizeSpan(2f), 0, 16, 0);
        return s;
    }
}