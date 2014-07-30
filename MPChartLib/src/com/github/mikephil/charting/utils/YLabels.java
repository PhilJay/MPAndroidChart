
package com.github.mikephil.charting.utils;

/**
 * Class representing the y-legend and its entries. Only use the setter methods
 * to modify it. Do not access public variables directly.
 * 
 * @author Philipp Jahoda
 */
public class YLabels {

    /** the actual array of entries */
    public float[] mEntries = new float[] {};

    /** the number of entries the legend contains */
    public int mEntryCount;

    /** the number of decimal digits to use */
    public int mDecimals;

    /** the position of the y-labels relative to the chart */
    private YLabelPosition mPosition = YLabelPosition.LEFT;

    /** enum for the position of the y-labels relative to the chart */
    public enum YLabelPosition {
        LEFT, RIGHT, BOTH_SIDED
    }

    /**
     * returns the position of the y-labels
     */
    public YLabelPosition getPosition() {
        return mPosition;
    }

    /**
     * sets the position of the y-labels
     * 
     * @param pos
     */
    public void setPosition(YLabelPosition pos) {
        mPosition = pos;
    }
}
