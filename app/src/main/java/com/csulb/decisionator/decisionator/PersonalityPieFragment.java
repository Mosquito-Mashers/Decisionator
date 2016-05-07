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
 * Created by Ronald on 5/6/2016.
 */
public class PersonalityPieFragment extends Fragment {
    private PieChart ppChart;


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


        return ppView;
    }



    public SpannableString generateCenterText() {
        SpannableString s = new SpannableString("Common\nInterest");
        s.setSpan(new RelativeSizeSpan(2f), 0, 14, 0);
        return s;
    }
}