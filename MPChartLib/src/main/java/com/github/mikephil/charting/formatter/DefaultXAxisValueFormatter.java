package com.github.mikephil.charting.formatter;

import com.github.mikephil.charting.utils.ViewPortHandler;

import java.text.DecimalFormat;

/**
 * Created by Philipp Jahoda on 14/09/15.
 * Default formatter class for adjusting xPx-values before drawing them.
 * This simply returns the original yValue unmodified.
 */
public class DefaultXAxisValueFormatter implements XAxisValueFormatter {

    private DecimalFormat mFormat = new DecimalFormat("###,###,###,##0.0");

    @Override
    public String getXValue(float xValue, float xRange, float xPosition, ViewPortHandler viewPortHandler) {
        return mFormat.format(xValue); // just return original, no adjustments
    }
}
