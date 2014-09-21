
package com.github.mikephil.charting.utils;

/**
 * Interface that allows custom formatting of all values displayed on the y-axis
 * (YLabels).
 * 
 * @author Philipp Jahoda
 */
public interface YLabelFormatter {

    /**
     * Called when a label is customly-formatted before being drawn.
     * 
     * @param value
     * @return the formatted label for the y-axis
     */
    public String getFormattedLabel(float value);

//    /**
//     * Should return the width of the largest label in pixels.
//     * 
//     * @return
//     */
//    public int getLabelWidth();
}
