package com.example.mpchartexample.simple;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mpchartexample.MyMarkerView;
import com.example.mpchartexample.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.filter.Approximator;
import com.github.mikephil.charting.data.filter.Approximator.ApproximatorType;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.Legend;


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
        mChart.setLineWidth(3.5f);
        mChart.setCircleSize(5f);
        mChart.setHighlightIndicatorEnabled(false); 
        mChart.setDrawBorder(false);
        mChart.setDrawGridBackground(false);
        mChart.setDrawVerticalGrid(false);
        mChart.setDrawXLabels(false);
        mChart.setDrawYValues(false);
        mChart.setStartAtZero(false);
        
        mChart.setYRange(-1f, 1f, false);
        mChart.setDrawCircles(true);
        
        mChart.setData(getComplexity());
        
        Typeface tf = Typeface.createFromAsset(getActivity().getAssets(),"OpenSans-Light.ttf");
        
        mChart.setYLabelTypeface(tf);
        
        Legend l = mChart.getLegend();
        l.setTypeface(tf);
        
        return v;
    }
}
