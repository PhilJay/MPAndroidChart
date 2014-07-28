package com.example.mpchartexample.simple;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.example.mpchartexample.MyMarkerView;
import com.example.mpchartexample.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.utils.Legend;


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
        mChart.setYLabelCount(6);
        mChart.setDescription("");
        
        MyMarkerView mv = new MyMarkerView(getActivity(), R.layout.custom_marker_view);
        mv.setOffsets(-mv.getMeasuredWidth() / 2, -mv.getMeasuredHeight());

        mChart.setMarkerView(mv);
        
        mChart.setHighlightIndicatorEnabled(false);
        mChart.setDrawBorder(false);
//        mChart.setBorderStyles(new BorderStyle[] { BorderStyle.LEFT });
        mChart.setDrawGridBackground(false);
        mChart.setDrawVerticalGrid(false);
        mChart.setDrawXLabels(false);
        mChart.setDrawYValues(false);
        mChart.setUnit(" €");
        
        Typeface tf = Typeface.createFromAsset(getActivity().getAssets(),"OpenSans-Light.ttf");
        
        mChart.setYLabelTypeface(tf);
        
        mChart.setData(generateData(1, 20000, 12));
        
        Legend l = mChart.getLegend();
        l.setTypeface(tf);
        
        // programatically add the chart
        FrameLayout parent = (FrameLayout) v.findViewById(R.id.parentLayout);
        parent.addView(mChart);
        
        return v;
    }
}
