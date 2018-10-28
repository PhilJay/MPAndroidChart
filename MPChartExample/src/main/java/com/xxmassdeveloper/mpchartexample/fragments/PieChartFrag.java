package com.xxmassdeveloper.mpchartexample.fragments;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.xxmassdeveloper.mpchartexample.R;


public class PieChartFrag extends SimpleFragment {

    @NonNull
    public static Fragment newInstance() {
        return new PieChartFrag();
    }

    @SuppressWarnings("FieldCanBeLocal")
    private PieChart chart;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.frag_simple_pie, container, false);

        chart = v.findViewById(R.id.pieChart1);
        chart.getDescription().setEnabled(false);

        Typeface tf = Typeface.createFromAsset(context.getAssets(), "OpenSans-Light.ttf");

        chart.setCenterTextTypeface(tf);
        chart.setCenterText(generateCenterText());
        chart.setCenterTextSize(10f);
        chart.setCenterTextTypeface(tf);

        // radius of the center hole in percent of maximum radius
        chart.setHoleRadius(45f);
        chart.setTransparentCircleRadius(50f);

        Legend l = chart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);

        chart.setData(generatePieData());

        return v;
    }

    private SpannableString generateCenterText() {
        SpannableString s = new SpannableString("Revenues\nQuarters 2015");
        s.setSpan(new RelativeSizeSpan(2f), 0, 8, 0);
        s.setSpan(new ForegroundColorSpan(Color.GRAY), 8, s.length(), 0);
        return s;
    }
}
