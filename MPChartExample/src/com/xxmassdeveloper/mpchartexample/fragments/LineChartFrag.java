package com.xxmassdeveloper.mpchartexample.fragments;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.Legend;
import com.xxmassdeveloper.mpchartexample.R;


public class LineChartFrag extends SimpleFragment {

    public static Fragment newInstance() {
        return new LineChartFrag();
    }

    private LineChart mChart;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.frag_simple_line, container, false);
        
        mChart = (LineChart) v.findViewById(R.id.lineChart1);
        
        ColorTemplate ct = new ColorTemplate();
        ct.addColorsForDataSets(ColorTemplate.VORDIPLOM_COLORS, getActivity());
        
        mChart.setColorTemplate(ct);
        mChart.setDrawCircles(false);
         
        mChart.setDescription("");
        mChart.setDrawFilled(false);
        mChart.setDrawYValues(false);
//        mChart.setCircleSize(5f);
        mChart.setDrawCircles(false);
        mChart.setHighlightIndicatorEnabled(false); 
        mChart.setDrawBorder(false);
        mChart.setDrawGridBackground(false);
        mChart.setDrawVerticalGrid(false);
        mChart.setDrawXLabels(false);
        mChart.setDrawYValues(false);
        mChart.setStartAtZero(false);
        
        mChart.setYRange(-1.2f, 1.2f, false);
        
        mChart.setData(generateLineData());
        
//        mChart.setScaleMinima(3f, 3f);
//        mChart.centerViewPort(300, 0);
        
        Typeface tf = Typeface.createFromAsset(getActivity().getAssets(),"OpenSans-Light.ttf");
        
        mChart.setYLabelTypeface(tf);
        
        Legend l = mChart.getLegend();
        l.setTypeface(tf);
        
        return v;
    }
}
