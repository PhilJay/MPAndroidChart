package com.github.mikephil.charting.agiplan;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.SpannableString;
import android.util.AttributeSet;

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

import java.util.ArrayList;

/**
 * Created by vsossella on 07/02/17.
 */

public class InvestmentPieChart extends PieChart  {

    private static float desiredAngle = 135f;
    private static int spinDuration = 1000;
    private SpannableString mCenterText;
    private MarkerView mMarkView;
    private boolean isOneInvestmentOnly = false;

    public InvestmentPieChart(Context context) {
        super(context);
    }

    public InvestmentPieChart(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public InvestmentPieChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void loadChart(ArrayList<InvestmentPieEntry> entries, SpannableString centerText, MarkerView markView) {
        mCenterText = centerText;
        mMarkView = markView;

        setupGraph();
        setData(entries);

        this.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                if ((e instanceof InvestmentPieEntry) && ((InvestmentPieEntry) e).isAddSlice()) {
                    InvestmentPieChart.this.highlightValue(0, 0, false);
                    return;
                }

                float start = InvestmentPieChart.this.getRotationAngle();
                int currentSliceIndex = Math.round(h.getX());
                float offset = InvestmentPieChart.this.getDrawAngles()[currentSliceIndex] / 2;
                float end = desiredAngle-(InvestmentPieChart.this.getAbsoluteAngles()[currentSliceIndex]-offset);
                InvestmentPieChart.this.spin(spinDuration,start,end, Easing.EasingOption.EaseInOutQuad);
            }

            @Override
            public void onNothingSelected() {

            }
        });

        //@TODO remove this when get service integration to select the most valuate investment.
        this.highlightValue(0, 0, false);
    }


    private void setupGraph() {
        this.setUsePercentValues(true);
        this.getDescription().setEnabled(false);
        this.setExtraOffsets(5, 10, 5, 35);
        this.setDragDecelerationFrictionCoef(0.95f);
        //Typeface font = Typeface.createFromAsset(mContext.getAssets(), "fonts/DINNextLTPro-Regular.otf");
        //mChart.setCenterTextTypeface(font);
        this.setCenterText(mCenterText);
        this.setDrawHoleEnabled(true);
        this.setHoleColor(Color.TRANSPARENT);
        this.setHoleRadius(92f);
        this.setTransparentCircleRadius(81f);
        this.setDrawCenterText(true);
        this.setRotationAngle(0);
        this.setRotationEnabled(false);
        this.setHighlightPerTapEnabled(true);
        this.animateY(1400, Easing.EasingOption.EaseInOutQuad);
        this.getLegend().setEnabled(false);
        this.setEntryLabelColor(Color.WHITE);
        this.setEntryLabelTextSize(12f);
        this.setMarker(mMarkView);
    }

    private void setData(ArrayList<InvestmentPieEntry> entries) {
        ArrayList<PieEntry> pieEntries = new ArrayList<PieEntry>();

        if (entries.size() == 1) {
            SpannableString detailText = new SpannableString("");
            int color = Color.argb(255, 166, 208, 69);
            entries.add(new InvestmentPieEntry(15, detailText, color, null, true));
            isOneInvestmentOnly = true;
        }

        pieEntries.addAll(entries);

        PieDataSet dataSet = new PieDataSet(pieEntries, "");
        dataSet.setSliceSpace(20f);
        dataSet.setSelectionShift(10f);
        setGraphColors(dataSet, entries);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.TRANSPARENT);
        this.setData(data);
        this.invalidate();
    }

    private void setGraphColors(PieDataSet dataSet, ArrayList<InvestmentPieEntry> entries) {
        ArrayList<Integer> colors = new ArrayList<Integer>();

        for (InvestmentPieEntry entry : entries) {
            colors.add(entry.getColor());
        }

        dataSet.setColors(colors);
    }

}
