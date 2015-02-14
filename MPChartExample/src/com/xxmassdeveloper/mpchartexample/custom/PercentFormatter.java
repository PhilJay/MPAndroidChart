package com.xxmassdeveloper.mpchartexample.custom;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.utils.ValueFormatter;

import java.text.DecimalFormat;

public class PercentFormatter implements ValueFormatter {

    private DecimalFormat mFormat;
    private PieChart mChart;
    
    public PercentFormatter(PieChart chart) {
        mFormat = new DecimalFormat("#,##0.0");
        this.mChart = chart;
    }
    
    @Override
    public String getFormattedValue(float value) {
        return mFormat.format(mChart.getPercentOfTotal(value)) + " %";
    }

}
