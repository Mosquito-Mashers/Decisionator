package com.csulb.decisionator.decisionator;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class ResultGraphFragment extends Fragment {

    private String data_for_cloud = "";
    private String top_venues = "";
    private SpannableString wordCloud;
    WordCloudGenerator cloudGen;
    private Map<String,Integer> venueMap = new HashMap<String, Integer>();

    public static ResultGraphFragment newInstance(Bundle b) {
        ResultGraphFragment fragment = new ResultGraphFragment();
        fragment.setArguments(b);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_result_graph, container, false);
        Bundle incoming = getArguments();
        TextView box = (TextView) view.findViewById(R.id.word_cloud);
        box.setMovementMethod(new ScrollingMovementMethod());
        if(incoming != null) {
            data_for_cloud = getArguments().getString(EventActivity.WORD_CLOUD_DATA);
            top_venues = getArguments().getString(EventActivity.TOP_VENUE_DATA);
            cloudGen = new WordCloudGenerator(data_for_cloud,null);
            if(top_venues != "" && top_venues != null)
            {
                String raw[] = top_venues.split("\\|");
                for(int k = 0; k < raw.length; k++)
                {
                    String item[] = raw[k].split(",");
                    venueMap.put(item[0],Integer.parseInt(item[1]));
                }
                wordCloud = cloudGen.getSpannableString(venueMap);
            }
            else {

                cloudGen.createFrequencyMap();
                wordCloud = cloudGen.getSpannableString();
            }


            box.setText(wordCloud);
        }
        // Inflate the layout for this fragment
        return view;
    }
}
