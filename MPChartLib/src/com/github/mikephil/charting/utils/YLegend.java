
package com.github.mikephil.charting.utils;

/**
 * Class representing the y-legend and its entries.
 * 
 * @author Philipp Jahoda
 */
public class YLegend {

    /** the actual array of entries */
    public float[] mEntries = new float[] {};

    /** the number of entries the legend contains */
    public int mEntryCount;

    /** the number of decimal digits to use */
    public int mDecimals;
    
    public enum YLegendPosition {
        LEFT, RIGHT, BOTH_SIDED
    }
}
