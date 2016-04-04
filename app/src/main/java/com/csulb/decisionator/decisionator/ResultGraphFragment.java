package com.csulb.decisionator.decisionator;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class ResultGraphFragment extends Fragment {

    private String data_for_cloud = "";

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
        box.setText("SWAG");
        if(incoming != null) {
            data_for_cloud = getArguments().getString(EventActivity.WORD_CLOUD_DATA);


            box.setText(data_for_cloud);
        }
        // Inflate the layout for this fragment
        return view;
    }
}
