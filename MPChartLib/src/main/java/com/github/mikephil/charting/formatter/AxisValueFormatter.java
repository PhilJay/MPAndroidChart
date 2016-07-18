package com.github.mikephil.charting.formatter;

import com.github.mikephil.charting.components.AxisBase;

/**
 * Created by Philipp Jahoda on 20/09/15.
 * Custom formatter interface that allows formatting of
 * axis labels before they are being drawn.
 */
public interface AxisValueFormatter {

    /**
     * Called when a value from an axis is to be formatted
     * before being drawn. For performance reasons, avoid excessive calculations
     * and memory allocations inside this method.
     *
     * @param value the value to be formatted
     * @param axis  the axis the value belongs to
     * @return
     */
    String getFormattedValue(float value, AxisBase axis);

    /**
     * Returns the number of decimal digits this formatter uses or -1, if unspecified.
     *
     * @return
     */
    int getDecimalDigits();
}
