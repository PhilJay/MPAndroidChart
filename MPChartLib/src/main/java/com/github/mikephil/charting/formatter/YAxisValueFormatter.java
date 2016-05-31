package com.github.mikephil.charting.formatter;

import com.github.mikephil.charting.components.YAxis;

/**
 * Created by Philipp Jahoda on 20/09/15.
 * Custom formatter interface that allows formatting of
 * YAxis labels before they are being drawn.
 */
public interface YAxisValueFormatter {

    /**
     * Called when a yValue from the YAxis is formatted
     * before being drawn. For performance reasons, avoid excessive calculations
     * and memory allocations inside this method.
     *
     * @param value the YAxis yValue to be formatted
     * @param yAxis the YAxis object the yValue belongs to
     * @return
     */
    String getFormattedValue(float value, YAxis yAxis);
}
