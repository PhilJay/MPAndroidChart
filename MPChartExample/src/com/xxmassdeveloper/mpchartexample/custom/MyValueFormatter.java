package com.xxmassdeveloper.mpchartexample.custom;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.FormattedStringCache;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.text.DecimalFormat;

public class MyValueFormatter implements ValueFormatter {

    private FormattedStringCache<Integer, Float> mFormattedStringCache;
    
    public MyValueFormatter() {
        mFormattedStringCache = new FormattedStringCache<>(new DecimalFormat("###,###,###,##0.0"));
    }

    @Override
    public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
        return mFormattedStringCache.getFormattedString(value, dataSetIndex) + " $";
    }
}
