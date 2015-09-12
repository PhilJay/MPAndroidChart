
package com.github.mikephil.charting.utils;

import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

/**
 * Interface for providing a custom logic to where the filling line of a LineDataSet
 * should end. This of course only works if setFillEnabled(...) is set to true.
 * 
 * @author Philipp Jahoda
 */
public interface FillFormatter {

    /**
     * Returns the vertical (y-axis) position where the filled-line of the
     * LineDataSet should end.
     * 
     * @param dataSet
     * @param data
     * @param chartMaxY
     * @param chartMinY
     * @return
     */
    public float getFillLinePosition(LineDataSet dataSet, LineData data, float chartMaxY,
            float chartMinY);
}
