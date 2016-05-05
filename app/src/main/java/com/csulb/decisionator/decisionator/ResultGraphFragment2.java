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
 * Created by Ron on 4/26/2016.
 */
public class ResultGraphFragment2 extends Fragment {

    private String data_for_cloud = "";
    private String top_venues = "";
    private Map<String,Integer> sortedResults = new HashMap<String, Integer>();

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
            pieChart.getLegend().setEnabled(false);


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
                        venues += item[0]+": " + item[1] + "\n";
                    }
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
            }
        }

    }
       if(sortedResults.size() > 0) {
           Iterator mapIter = sortedResults.entrySet().iterator();
           xVals = new ArrayList<String>();
           int count = 0;
           while (mapIter.hasNext() && count < 5) {
               Map.Entry ent = (Map.Entry) mapIter.next();
               if((int)ent.getValue() > 0) {
                   pieEntries.add(new Entry((float) ((int) (ent.getValue())), count));
                   xVals.add(count, ent.getKey().toString());
                   count++;
               }
           }
           ds1 = new PieDataSet(pieEntries, "Venue Analysis");

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
        SpannableString s = new SpannableString("Venue\nAnalysis");
        s.setSpan(new RelativeSizeSpan(2f), 0, 14, 0);
        return s;
    }
}




