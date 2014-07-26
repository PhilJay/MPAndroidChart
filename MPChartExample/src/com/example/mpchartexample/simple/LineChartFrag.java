package com.example.mpchartexample.simple;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mpchartexample.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.utils.ColorTemplate;


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
        ct.addColorsForDataSets(new int[] { R.color.vordiplom_1, R.color.vordiplom_4 }, getActivity());
        
        Log.i("TEST", "size: " + ct.getColors().size());
        
        mChart.setColorTemplate(ct);
         
        mChart.setDescription("");
        mChart.setDrawFilled(true);
        mChart.setDrawYValues(false);
        mChart.setLineWidth(3.5f);
        mChart.setCircleSize(5f);
        
        mChart.setData(generateData(2, 10000));
        
        return v;
    }
}
