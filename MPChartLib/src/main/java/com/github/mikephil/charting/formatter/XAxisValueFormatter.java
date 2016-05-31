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
     * Returns the customized label that is drawn on the x-axis.
     * For performance reasons, avoid excessive calculations
     * and memory allocations inside this method.
     *
     * @param xValue          the original x-value
     * @param xRange          the total range of the x-values
     * @param xPosition       the position on the x-axis where the value is drawn (in pixels)
     * @param viewPortHandler provides information about the current chart state (scale, translation, ...)
     * @return
     */
    String getXValue(float xValue, float xRange, float xPosition, ViewPortHandler viewPortHandler);
}
