package com.xxmassdeveloper.mpchartexample.fragments;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.YAxis;
import com.xxmassdeveloper.mpchartexample.R;


public class SineCosineFragment extends SimpleFragment {

    public static Fragment newInstance() {
        return new SineCosineFragment();
    }

    private LineChart mChart;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.frag_simple_line, container, false);
        
        mChart = (LineChart) v.findViewById(R.id.lineChart1);
        
        mChart.setDescription("");
//        mChart.setCircleSize(5f);
        
        mChart.setHighlightIndicatorEnabled(false); 
        mChart.setDrawBorder(false);
        mChart.setDrawGridBackground(false);
        mChart.setDrawVerticalGrid(false);
        mChart.setDrawXLabels(false);
        mChart.setStartAtZero(false);
        
        mChart.setYRange(-1.2f, 1.2f, false);
        
        mChart.setData(generateLineData());
        mChart.animateX(3000);
        
//        mChart.setScaleMinima(3f, 3f);
//        mChart.centerViewPort(300, 0);
        
        Typeface tf = Typeface.createFromAsset(getActivity().getAssets(),"OpenSans-Light.ttf");
        
        Legend l = mChart.getLegend();
        l.setTypeface(tf);
        
        YAxis labels = mChart.getAxisLeft();
        labels.setTypeface(tf);
        
        return v;
    }
}
