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
 * Created by Ron on 5/6/2016.
 */
public class profilePieFragment extends Fragment {
    private PieChart profileChart;
    private uProfile currProfile;
    private User currUser;
    private uProfile friendProfile;
    private User friendUser;
    String uID;
    String fID;

    public static profilePieFragment newInstance(Bundle b) {
        profilePieFragment profileFrag = new profilePieFragment();
        profileFrag.setArguments(b);

        return profileFrag;
    }

}
