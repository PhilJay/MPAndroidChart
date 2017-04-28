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

public class InvestmentChart {

    private static PieChart mChart;
    private static Context mContext;
    private static float desiredAngle = 135f;
    private static int spinDuration = 1000;

    public static void destroyChart() {
        mChart = null;
        mContext = null;
    }

    public static void loadChart(PieChart chart, Context context) {

        mChart = chart;
        mContext = context;

        setupGraph();
        setData(3);

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
        mChart.setCenterText(generateCenterSpannableText());
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
        mChart.setMarker(new InvestmentMarkerView(mContext, R.layout.pie_chart_marker));



    }

    private static void setData(int count) {
        ArrayList<PieEntry> entries = new ArrayList<PieEntry>();
        //@TODO remove this mocked data when get service integration
        entries.add(new InvestmentPieEntry(95963.563f, "LCI Banco Agiplan", 45.1f));
        entries.add(new InvestmentPieEntry(38540.563f, "LCA Banco Agiplan", 18.1f));
        entries.add(new InvestmentPieEntry(78386.57f, "CDB Banco Agiplan", 36.8f));

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setSliceSpace(16f);
        dataSet.setSelectionShift(10f);
        setGraphColors(dataSet);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.TRANSPARENT);
        mChart.setData(data);
        mChart.invalidate();
    }

    private static void setGraphColors(PieDataSet dataSet) {
        ArrayList<Integer> colors = new ArrayList<Integer>();
        colors.add(mContext.getResources().getColor(R.color.pie_graph_color_primary));
        colors.add(mContext.getResources().getColor(R.color.pie_graph_color_blue));
        colors.add(mContext.getResources().getColor(R.color.pie_graph_color_green));
        dataSet.setColors(colors);
    }

    private static SpannableString generateCenterSpannableText() {
        SpannableString s = new SpannableString("Total Investido\nR$212.890,69");
        s.setSpan(new RelativeSizeSpan(1.2f), 0, 15, 0);
        s.setSpan(new RelativeSizeSpan(1.2f), 16, 18, 0);
        s.setSpan(new RelativeSizeSpan(2.8f), 18, 25, 0);
        s.setSpan(new RelativeSizeSpan(1.2f), 25, s.length(), 0);
        s.setSpan(new StyleSpan(Typeface.BOLD), 18, 25, 0);
        s.setSpan(new ForegroundColorSpan(Color.WHITE), 0, s.length(), 0);
        return s;
    }
}
