package com.xxmassdeveloper.mpchartexample.custom;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.AxisValueFormatter;
import com.github.mikephil.charting.formatter.FormattedStringCache;

import java.text.DecimalFormat;

public class MyAxisValueFormatter implements AxisValueFormatter {

    private DecimalFormat mFormat;
    private FormattedStringCache<Float, Float> mFormattedStringCache;

    public MyAxisValueFormatter() {
        mFormattedStringCache = new FormattedStringCache<>(new DecimalFormat("###,###,###,##0.0"));
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        return mFormattedStringCache.getFormattedString(value, value) + " $";
    }

    @Override
    public int getDecimalDigits() {
        return 1;
    }
}
