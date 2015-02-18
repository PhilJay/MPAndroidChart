package com.xxmassdeveloper.mpchartexample.custom;

import com.github.mikephil.charting.utils.ValueFormatter;

import java.text.DecimalFormat;

public class PercentFormatter implements ValueFormatter {

    private DecimalFormat mFormat;
    
    public PercentFormatter() {
        mFormat = new DecimalFormat("#,##0.0");
    }
    
    @Override
    public String getFormattedValue(float value) {
        return mFormat.format(value) + " %";
    }

}
