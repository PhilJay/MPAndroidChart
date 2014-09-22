
package com.github.mikephil.charting.data;

import java.util.ArrayList;

/**
 * Data object that represents all data for the BarChart.
 * 
 * @author Philipp Jahoda
 */
public class BarData extends BarLineScatterCandleData<BarDataSet> {

    /** the space that is left between groups of bars */
    private float mGroupSpace = 0.8f;

    public BarData(ArrayList<String> xVals, ArrayList<BarDataSet> dataSets) {
        super(xVals, dataSets);
    }

    public BarData(String[] xVals, ArrayList<BarDataSet> dataSets) {
        super(xVals, dataSets);
    }

    public BarData(ArrayList<String> xVals, BarDataSet dataSet) {
        super(xVals, toArrayList(dataSet));
    }

    public BarData(String[] xVals, BarDataSet dataSet) {
        super(xVals, toArrayList(dataSet));
    }
    
    private static ArrayList<BarDataSet> toArrayList(BarDataSet dataSet) {
        ArrayList<BarDataSet> sets = new ArrayList<BarDataSet>();
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
}
