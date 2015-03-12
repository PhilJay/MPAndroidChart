
package com.github.mikephil.charting.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Data object that represents all data for the BarChart.
 * 
 * @author Philipp Jahoda
 */
public class BarData extends BarLineScatterCandleData<BarDataSet> {

    /** the space that is left between groups of bars */
    private float mGroupSpace = 0.8f;

    // /**
    // * The maximum space (in pixels on the screen) a single bar can consume.
    // */
    // private float mMaximumBarWidth = 100f;

    public BarData() {
        super();
    }

    public BarData(List<String> xVals) {
        super(xVals);
    }

    public BarData(String[] xVals) {
        super(xVals);
    }

    public BarData(List<String> xVals, List<BarDataSet> dataSets) {
        super(xVals, dataSets);
    }

    public BarData(String[] xVals, List<BarDataSet> dataSets) {
        super(xVals, dataSets);
    }

    public BarData(List<String> xVals, BarDataSet dataSet) {
        super(xVals, toList(dataSet));
    }

    public BarData(String[] xVals, BarDataSet dataSet) {
        super(xVals, toList(dataSet));
    }

    private static List<BarDataSet> toList(BarDataSet dataSet) {
        List<BarDataSet> sets = new ArrayList<BarDataSet>();
        sets.add(dataSet);
        return sets;
    }

    /**
     * Returns the space that is left out between groups of bars. Always returns
     * 0 if the BarData object only contains one DataSet (because for one
     * DataSet, there is no group-space needed).
     * 
     * @return
     */
    public float getGroupSpace() {

        if (mDataSets.size() <= 1)
            return 0f;
        else
            return mGroupSpace;
    }

    /**
     * Sets the space between groups of bars of different datasets in percent of
     * the total width of one bar. 100 = space is exactly one bar width,
     * default: 80
     * 
     * @param percent
     */
    public void setGroupSpace(float percent) {
        mGroupSpace = percent / 100f;
    }

    /**
     * Returns true if this BarData object contains grouped DataSets (more than
     * 1 DataSet).
     * 
     * @return
     */
    public boolean isGrouped() {
        return mDataSets.size() > 1 ? true : false;
    }
    
    //
    // /**
    // * Sets the maximum width (in density pixels) a single bar in the barchart
    // * should consume.
    // *
    // * @param max
    // */
    // public void setBarWidthMaximum(float max) {
    // mMaximumBarWidth = Utils.convertDpToPixel(max);
    // }
    //
    // /**
    // * Returns the maximum width (in density pixels) a single bar in the
    // * barchart should consume.
    // *
    // * @return
    // */
    // public float getBarWidthMaximum() {
    // return mMaximumBarWidth;
    // }
}
