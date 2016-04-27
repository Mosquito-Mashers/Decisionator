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

import org.w3c.dom.Text;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
/**
 * Created by Ron on 4/26/2016.
 */
public class ResultGraphFragment2 extends Fragment {

    private String data_for_cloud = "";
    private String sorted_venues = "";
    private SpannableString wordCloud;
    WordCloudGenerator cloudGen;
    private Map<String,Integer> sortedResults = new HashMap<String, Integer>();

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
            TextView box = (TextView) view.findViewById(R.id.venues_description);
            //TextView description = (TextView) view.findViewById(R.id.venues_cloud);
            box.setMovementMethod(new ScrollingMovementMethod());
            if (incoming != null) {
                sorted_venues = getArguments().getString(EventActivity.TOP_VENUE_DATA);
                if (sorted_venues != "" && sorted_venues != null) {
                    String raw[] = sorted_venues.split("\\|");
                    for (int k = 0; k < raw.length; k++) {
                        String item[] = raw[k].split(",");
                        sortedResults.put(item[0], Integer.parseInt(item[1]));
                    }

                }

                box.setText(wordCloud);
                return view;
            }
            return view;
        }


}






