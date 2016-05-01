package com.csulb.decisionator.decisionator;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.text.method.ScrollingMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
/**
 * Created by Ron on 4/26/2016.
 */
public class ResultGraphFragment2 extends Fragment {

    private String data_for_cloud = "";
    private String top_venues = "";
    private SpannableString wordCloud;
    WordCloudGenerator cloudGen;
    private Map<String,Integer> sortedResults = new HashMap<String, Integer>();
    private TextView box;
    private TextView mapText;
    private LinkedHashMap linkHashSort = new LinkedHashMap();

    private PieChart pieChart;
    private SeekBar seekX, seekY;
    private TextView tvX, tvY;
    private Typeface tf;


    public static ResultGraphFragment2 newInstance(Bundle b) {
        ResultGraphFragment2 fragment2 = new ResultGraphFragment2();
        fragment2.setArguments(b);

        return fragment2;
    }
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View view2 = inflater.inflate(R.layout.fragment_result_graph2, container, false);
            Bundle incoming = getArguments();

            box = (TextView) view2.findViewById(R.id.venues_description);
            mapText = (TextView) view2.findViewById(R.id.topVenueAnalysis);
            box.setMovementMethod(new ScrollingMovementMethod());

            pieChart = (PieChart) view2.findViewById(R.id.piechart);
            pieChart.setDescription("");
            Typeface tf = Typeface.createFromAsset(getActivity().getAssets(), "OpenSans-Light.ttf");
            pieChart.setCenterTextTypeface(tf);
            pieChart.setCenterText(generateCenterText());
            pieChart.setCenterTextSize(10f);
            pieChart.setCenterTextTypeface(tf);
            // radius of the center hole in percent of maximum radius
            pieChart.setHoleRadius(45f);
            pieChart.setTransparentCircleRadius(50f);
            Legend l = pieChart.getLegend();
            l.setPosition(Legend.LegendPosition.RIGHT_OF_CHART);
           // pieChart.setData(generatePieData());

            if (incoming != null)
                {
                data_for_cloud = getArguments().getString(EventActivity.WORD_CLOUD_DATA);
                top_venues = getArguments().getString(EventActivity.TOP_VENUE_DATA);
                    String venues = "";
                if (top_venues != "" && top_venues != null)
                {

                    String raw[] = top_venues.split("\\|");
                    for (int k = 0; k < raw.length; k++) {
                        String item[] = raw[k].split(",");
                        //sortedResults.put(item[0], Integer.parseInt(item[1]));
                        venues += item[0]+": " + item[1] + "\n";
                    }
                    mapText.setText(venues);
                }
                    pieChart.setData(generatePieData());
            }
            return view2;
        }

   public PieData generatePieData()
   {
       // Entry will be the weight of the venue
       List<Entry> pieEntries = new ArrayList<Entry>();
       List<Integer> entryInt = new ArrayList<Integer>();
       //xVal will be used for the venue names and legend
       PieDataSet ds1 = new PieDataSet(pieEntries, "Venue Analysis");

       List<String> xVals = new ArrayList<String>();
       PieData d = new PieData(xVals, ds1);
    Bundle incoming2 = getArguments();
    if(incoming2 != null)
    {


        data_for_cloud = getArguments().getString(EventActivity.WORD_CLOUD_DATA);
        top_venues = getArguments().getString(EventActivity.TOP_VENUE_DATA);
        if (top_venues != "" && top_venues != null)
        {
            String raw[] = top_venues.split("\\|");
            for (int k = 0; k < raw.length; k++)
            {
                String item[] = raw[k].split(",");
                sortedResults.put(item[0], Integer.parseInt(item[1]));
                // copy in sortHashMapByValuesD if ordering is inconsistent
               //linkHashSort.sortHashMapByValuesD(sortedResults);
                xVals = new ArrayList<String>(sortedResults.keySet());
                entryInt = new ArrayList<Integer>(sortedResults.values());
                //pieEntries = new ArrayList<Entry>(sortedResults.values());

            }
        }
    }
    int count = 4;

    if(entryInt.size() > 0) {
        for (int i = 0; i < count; i++) {
            // xVals.add("entry" + (i+1));

            pieEntries.add(new Entry((float) entryInt.get(i), i));
        }

        ds1 = new PieDataSet(pieEntries, "Venue Analysis");

        ds1.setColors(ColorTemplate.VORDIPLOM_COLORS);
        ds1.setSliceSpace(2f);
        ds1.setValueTextColor(Color.WHITE);
        ds1.setValueTextSize(12f);

        d = new PieData(xVals, ds1);
    }
    d.setValueTypeface(tf);

    return d;
}
    public SpannableString generateCenterText() {
        SpannableString s = new SpannableString("Venue\nAnalysis");
        s.setSpan(new RelativeSizeSpan(2f), 0, 8, 0);
        s.setSpan(new ForegroundColorSpan(Color.GRAY), 8, s.length(), 0);
        return s;
    }

}




