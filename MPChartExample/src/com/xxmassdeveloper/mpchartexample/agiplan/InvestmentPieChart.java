package com.xxmassdeveloper.mpchartexample.agiplan;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.xxmassdeveloper.mpchartexample.R;

import java.util.ArrayList;

/**
 * Created by vsossella on 07/02/17.
 */

public class InvestmentPieChart  {

    private static PieChart mChart;
    private static float desiredAngle = 135f;
    private static int spinDuration = 1000;
    private static SpannableString mCenterText;
    private static MarkerView mMarkView;

    public static void destroyChart() {
        mChart = null;
    }

    public static void loadChart(PieChart chart, ArrayList<InvestmentPieEntry> entries, SpannableString centerText, MarkerView markView) {

        mChart = chart;
        mCenterText = centerText;
        mMarkView = markView;


        setupGraph();
        setData(entries);

        mChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                //get current angle
                float start = mChart.getRotationAngle();
                //get index of current slice
                int currentSliceIndex = Math.round(h.getX());
                //calculate center of slice
                float offset = mChart.getDrawAngles()[currentSliceIndex] / 2;
                // calculate the next angle
                float end = desiredAngle-(mChart.getAbsoluteAngles()[currentSliceIndex]-offset);
                //rotate to slice center
                mChart.spin(spinDuration,start,end, Easing.EasingOption.EaseInOutQuad);
            }

            @Override
            public void onNothingSelected() {

            }
        });

        //@TODO remove this when get service integration to select the most valuate investment.
        mChart.highlightValue(0, 0, true);
    }


    private static void setupGraph() {
        mChart.setUsePercentValues(true);
        mChart.getDescription().setEnabled(false);
        mChart.setExtraOffsets(5, 10, 5, 35);
        mChart.setDragDecelerationFrictionCoef(0.95f);
        //Typeface font = Typeface.createFromAsset(mContext.getAssets(), "fonts/DINNextLTPro-Regular.otf");
        //mChart.setCenterTextTypeface(font);
        mChart.setCenterText(mCenterText);
        mChart.setDrawHoleEnabled(true);
        mChart.setHoleColor(Color.TRANSPARENT);
        mChart.setHoleRadius(92f);
        mChart.setTransparentCircleRadius(81f);
        mChart.setDrawCenterText(true);
        mChart.setRotationAngle(0);
        mChart.setRotationEnabled(false);
        mChart.setHighlightPerTapEnabled(true);
        mChart.animateY(1400, Easing.EasingOption.EaseInOutQuad);
        mChart.getLegend().setEnabled(false);
        mChart.setEntryLabelColor(Color.WHITE);
        mChart.setEntryLabelTextSize(12f);
        mChart.setMarker(mMarkView);
    }

    private static void setData(ArrayList<InvestmentPieEntry> entries) {
        ArrayList<PieEntry> pieEntries = new ArrayList<PieEntry>();
        pieEntries.addAll(entries);

        PieDataSet dataSet = new PieDataSet(pieEntries, "");
        dataSet.setSliceSpace(16f);
        dataSet.setSelectionShift(10f);
        setGraphColors(dataSet, entries);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.TRANSPARENT);
        mChart.setData(data);
        mChart.invalidate();
    }

    private static void setGraphColors(PieDataSet dataSet, ArrayList<InvestmentPieEntry> entries) {
        ArrayList<Integer> colors = new ArrayList<Integer>();

        for (InvestmentPieEntry entry : entries) {
            colors.add(entry.getColor());
        }

        dataSet.setColors(colors);
    }

}
