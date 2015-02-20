package com.xxmassdeveloper.mpchartexample.fragments;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.xxmassdeveloper.mpchartexample.R;
import com.xxmassdeveloper.mpchartexample.custom.MyMarkerView;


public class BarChartFrag extends SimpleFragment {

    public static Fragment newInstance() {
        return new BarChartFrag();
    }

    private BarChart mChart;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.frag_simple_bar, container, false);
        
        // create a new chart object
        mChart = new BarChart(getActivity());
        mChart.setDescription("");
        
        MyMarkerView mv = new MyMarkerView(getActivity(), R.layout.custom_marker_view);

        mChart.setMarkerView(mv);
        
        mChart.setHighlightIndicatorEnabled(false);

        mChart.setDrawGridBackground(false);
        mChart.setDrawBarShadow(false);
        
        Typeface tf = Typeface.createFromAsset(getActivity().getAssets(),"OpenSans-Light.ttf");
        
        mChart.setData(generateBarData(1, 20000, 12));
        
        Legend l = mChart.getLegend();
        l.setTypeface(tf);
        
        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setTypeface(tf);

        mChart.getAxisRight().setEnabled(false);
        
        XAxis xAxis = mChart.getXAxis();
        xAxis.setEnabled(false);
        
        // programatically add the chart
        FrameLayout parent = (FrameLayout) v.findViewById(R.id.parentLayout);
        parent.addView(mChart);
        
        return v;
    }
}
