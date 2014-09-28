
package com.xxmassdeveloper.mpchartexample;

import com.github.mikephil.charting.utils.ValueFormatter;

import java.text.DecimalFormat;

public class MyValueFormatter implements ValueFormatter {

    DecimalFormat mFormatter = new DecimalFormat("###,###,###");

    @Override
    public String getFormattedValue(float value) {
        // do here whatever you want, avoid excessive calculations and memory
        // allocations
        return mFormatter.format(value);
    }
}
