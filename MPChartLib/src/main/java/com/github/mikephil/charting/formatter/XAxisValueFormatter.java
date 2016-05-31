package com.github.mikephil.charting.formatter;

import com.github.mikephil.charting.utils.ViewPortHandler;

/**
 * Created by Philipp Jahoda on 14/09/15.
 * An interface for providing custom xPx-axis Strings.
 *
 * @author Philipp Jahoda
 */
public interface XAxisValueFormatter {

    /**
     * Returns the customized label that is drawn on the xPx-axis.
     * For performance reasons, avoid excessive calculations
     * and memory allocations inside this method.
     *
     * @param original        the original xPx-axis label to be drawn
     * @param index           the xPx-index that is currently being drawn
     * @param viewPortHandler provides information about the current chart state (scale, translation, ...)
     * @return
     */
    String getXValue(String original, int index, ViewPortHandler viewPortHandler);
}
