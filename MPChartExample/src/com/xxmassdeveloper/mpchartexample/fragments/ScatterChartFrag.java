package com.xxmassdeveloper.mpchartexample.fragments;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.ScatterChart;
import com.github.mikephil.charting.charts.ScatterChart.ScatterShape;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.Legend;
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
        mChart.setDrawYValues(false);
        mChart.setDescription("");
        mChart.setYLabelCount(6);
        
        ColorTemplate ct = new ColorTemplate();
        ct.addDataSetColors(ColorTemplate.VORDIPLOM_COLORS, getActivity());
        ct.addDataSetColors(new int[] { R.color.colorful_1, R.color.colorful_2, R.color.colorful_3, R.color.colorful_4 } , getActivity());
        
        mChart.setColorTemplate(ct);
        
        Typeface tf = Typeface.createFromAsset(getActivity().getAssets(),"OpenSans-Light.ttf");
        
        mChart.setYLabelTypeface(tf);
        
        MyMarkerView mv = new MyMarkerView(getActivity(), R.layout.custom_marker_view);
        mv.setOffsets(-mv.getMeasuredWidth() / 2, -mv.getMeasuredHeight());

        mChart.setMarkerView(mv);
        
        mChart.setScatterShapes(new ScatterShape[] {ScatterShape.CIRCLE, ScatterShape.SQUARE });
        mChart.setScatterShapeSize(18f);
        
        mChart.setHighlightIndicatorEnabled(false);
        mChart.setDrawBorder(false);
//        mChart.setBorderStyles(new BorderStyle[] { BorderStyle.LEFT });
        mChart.setDrawGridBackground(false);
        mChart.setDrawVerticalGrid(false);
        mChart.setDrawXLabels(false);
        mChart.setUnit(" $");
        
        mChart.setData(generateData(3, 10000, 100));
        
        Legend l = mChart.getLegend();
        l.setTypeface(tf);
        
        return v;
    }
}
