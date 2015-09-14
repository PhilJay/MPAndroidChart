package com.github.mikephil.charting.utils;

/**
 * Interface that allows custom formatting of all values and value-labels
 * displayed inside the chart. Simply create your own formatting class and let
 * it implement ValueFormatter. Then override the getFormattedLabel(...) method
 * and return whatever you want.
 * 
 * @author Philipp Jahoda
 */
public interface ValueFormatter {

    /**
     * Called when a value (from labels, or inside the chart) is formatted
     * before being drawn. For performance reasons, avoid excessive calculations
     * and memory allocations inside this method.
     * 
     * @param value the value to be formatted
     * @return the formatted label ready for being drawn
     */
    String getFormattedValue(float value);
}
