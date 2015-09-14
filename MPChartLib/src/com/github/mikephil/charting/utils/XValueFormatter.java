package com.github.mikephil.charting.utils;

/**
 * Created by Philipp Jahoda on 14/09/15.
 * An interface for providing custom x-axis Strings.
 *
 * @author Philipp Jahoda
 */
public interface XValueFormatter {

    /**
     * Returns the customized label that is drawn on the x-axis.
     *
     * @param original        the original x-axis label to be drawn
     * @param index           the x-index that is currently being drawn
     * @param viewPortHandler provides information about the current chart state (scale, translation, ...)
     * @return
     */
    String getXValue(String original, int index, ViewPortHandler viewPortHandler);
}
