package com.xxmassdeveloper.mpchartexample.fragments;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.ScatterChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.XAxis.XAxisPosition;
import com.github.mikephil.charting.components.YAxis;
import com.xxmassdeveloper.mpchartexample.R;
import com.xxmassdeveloper.mpchartexample.custom.MyMarkerView;


public class ScatterChartFrag extends SimpleFragment {

    public static Fragment newInstance() {
        return new ScatterChartFrag();
    }

    private ScatterChart mChart;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.frag_simple_scatter, container, false);
        
        mChart = (ScatterChart) v.findViewById(R.id.scatterChart1);
        mChart.setDescription("");
        
        Typeface tf = Typeface.createFromAsset(getActivity().getAssets(),"OpenSans-Light.ttf");
        
        MyMarkerView mv = new MyMarkerView(getActivity(), R.layout.custom_marker_view);

        mChart.setMarkerView(mv);

        mChart.setDrawGridBackground(false);
        mChart.setData(generateScatterData(6, 10000, 200));
        
        XAxis xAxis = mChart.getXAxis();
        xAxis.setEnabled(true);
        xAxis.setPosition(XAxisPosition.BOTTOM);
        
        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setTypeface(tf);
        
        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setTypeface(tf);
        rightAxis.setDrawGridLines(false);
        
        Legend l = mChart.getLegend();
        l.setWordWrapEnabled(true);
        l.setTypeface(tf);
        l.setFormSize(14f);
        l.setTextSize(9f);
        
        // increase the space between legend & bottom and legend & content
        l.setYOffset(13f);       
        mChart.setExtraBottomOffset(16f);
        
        return v;
    }
}
