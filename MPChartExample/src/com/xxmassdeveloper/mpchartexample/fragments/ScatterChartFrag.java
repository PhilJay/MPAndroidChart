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
import com.github.mikephil.charting.components.YAxis;
import com.xxmassdeveloper.mpchartexample.MyMarkerView;
import com.xxmassdeveloper.mpchartexample.R;


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
        
        YAxis labels = mChart.getAxisLeft();
        labels.setTypeface(tf);
        
        MyMarkerView mv = new MyMarkerView(getActivity(), R.layout.custom_marker_view);

        mChart.setMarkerView(mv);

        mChart.setHighlightIndicatorEnabled(false);
        mChart.setDrawBorder(false);
//        mChart.setBorderStyles(new BorderStyle[] { BorderStyle.LEFT });
        mChart.setDrawGridBackground(false);
        mChart.setDrawXLabels(false);

        mChart.setData(generateScatterData(3, 10000, 150));
        
        XAxis xAxis = mChart.getXAxis();
        xAxis.setDrawGridLines(false);
        
        Legend l = mChart.getLegend();
        l.setTypeface(tf);
        
        return v;
    }
}
