package com.xxmassdeveloper.mpchartexample.fragments;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.utils.Legend;
import com.github.mikephil.charting.utils.YLabels;
import com.xxmassdeveloper.mpchartexample.R;


public class ComplexityFragment extends SimpleFragment {

    public static Fragment newInstance() {
        return new ComplexityFragment();
    }

    private LineChart mChart;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.frag_simple_line, container, false);
        
        mChart = (LineChart) v.findViewById(R.id.lineChart1);
        
        mChart.setDescription("");
        mChart.setDrawYValues(false);
        
        mChart.setHighlightIndicatorEnabled(false); 
        mChart.setDrawBorder(false);
        mChart.setDrawGridBackground(false);
        mChart.setDrawVerticalGrid(false);
        mChart.setDrawXLabels(false);
        mChart.setDrawYValues(false);
        mChart.setStartAtZero(false);
        
        mChart.setData(getComplexity());
        mChart.animateX(3000);
        
//        mChart.setScaleMinima(3f, 3f);
//        mChart.centerViewPort(300, 0);
        
        Typeface tf = Typeface.createFromAsset(getActivity().getAssets(),"OpenSans-Light.ttf");
        
        Legend l = mChart.getLegend();
        l.setTypeface(tf);
        
        YLabels labels = mChart.getYLabels();
        labels.setTypeface(tf);
        
        return v;
    }
}
