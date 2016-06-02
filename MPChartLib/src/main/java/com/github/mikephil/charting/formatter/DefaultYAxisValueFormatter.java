package com.github.mikephil.charting.formatter;

import com.github.mikephil.charting.components.YAxis;

import java.text.DecimalFormat;

/**
 * Created by Philipp Jahoda on 20/09/15.
 * Default formatter used for formatting labels of the YAxis. Uses a DecimalFormat with
 * pre-calculated number of digits (depending on max and min yValue).
 */
public class DefaultYAxisValueFormatter extends DefaultAxisValueFormatter implements YAxisValueFormatter {

    public DefaultYAxisValueFormatter(int digits) {
        super(digits);
    }

    @Override
    public String getFormattedValue(float value, YAxis yAxis) {
        // avoid memory allocations here (for performance)
        return mFormat.format(value);
    }
}
