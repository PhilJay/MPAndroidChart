
package com.github.mikephil.charting.utils;

/**
 * Class representing the y-axis labels settings and its entries. Only use the
 * setter methods to modify it. Do not access public variables directly. Be
 * aware that not all features the YLabels class provides are suitable for the
 * RadarChart.
 * 
 * @author Philipp Jahoda
 */
public class YLabels extends LabelBase {

    /** the actual array of entries */
    public float[] mEntries = new float[] {};

    /** the number of entries the legend contains */
    public int mEntryCount;

    /** the number of decimal digits to use */
    public int mDecimals;

    /** the number of y-label entries the y-labels should have, default 6 */
    private int mLabelCount = 6;

    /**
     * if true, units are drawn next to the values of the y-axis labels
     */
    private boolean mDrawUnitsInLabels = true;

    /** indicates if the top y-label entry is drawn or not */
    private boolean mDrawTopYLabelEntry = true;

    /** the position of the y-labels relative to the chart */
    private YLabelPosition mPosition = YLabelPosition.LEFT;

    /** enum for the position of the y-labels relative to the chart */
    public enum YLabelPosition {
        LEFT, RIGHT, BOTH_SIDED, LEFT_INSIDE, RIGHT_INSIDE
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

    /**
     * returns true if drawing units in y-axis labels is enabled
     * 
     * @return
     */
    public boolean isDrawUnitsInYLabelEnabled() {
        return mDrawUnitsInLabels;
    }

    /**
     * if set to true, units are drawn next to y-label values, default: true
     * 
     * @param enabled
     */
    public void setDrawUnitsInYLabel(boolean enabled) {
        mDrawUnitsInLabels = enabled;
    }

    /**
     * returns true if drawing the top y-axis label entry is enabled
     * 
     * @return
     */
    public boolean isDrawTopYLabelEntryEnabled() {
        return mDrawTopYLabelEntry;
    }

    /**
     * set this to true to enable drawing the top y-label entry. Disabling this
     * can be helpful when the top y-label and left x-label interfere with each
     * other. default: true
     * 
     * @param enabled
     */
    public void setDrawTopYLabelEntry(boolean enabled) {
        mDrawTopYLabelEntry = enabled;
    }

    /**
     * sets the number of label entries for the y-axis max = 15, min = 2,
     * default: 6, be aware that this number is not fixed and can only be
     * approximated
     * 
     * @param yCount
     */
    public void setLabelCount(int yCount) {

        if (yCount > 15)
            yCount = 15;
        if (yCount < 2)
            yCount = 2;

        mLabelCount = yCount;
    }

    /**
     * Returns the number of label entries the y-axis should have
     * 
     * @return
     */
    public int getLabelCount() {
        return mLabelCount;
    }
}
