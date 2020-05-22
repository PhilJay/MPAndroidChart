package com.xxmassdeveloper.mpchartexample.fragments;
import android.graphics.Typeface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.xxmassdeveloper.mpchartexample.R;


public class SineCosineFragment extends SimpleFragment {

    @NonNull
    public static Fragment newInstance() {
        return new SineCosineFragment();
    }

    @SuppressWarnings("FieldCanBeLocal")
    private LineChart chart;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.frag_simple_line, container, false);

        chart = v.findViewById(R.id.lineChart1);

        chart.getDescription().setEnabled(false);

        chart.setDrawGridBackground(false);

        chart.setData(generateLineData());
        chart.animateX(3000);

        Typeface tf = Typeface.createFromAsset(context.getAssets(), "OpenSans-Light.ttf");

        Legend l = chart.getLegend();
        l.setTypeface(tf);

        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setTypeface(tf);
        leftAxis.setAxisMaximum(1.2f);
        leftAxis.setAxisMinimum(-1.2f);

        chart.getAxisRight().setEnabled(false);

        XAxis xAxis = chart.getXAxis();
        xAxis.setEnabled(false);

        return v;
    }
}
