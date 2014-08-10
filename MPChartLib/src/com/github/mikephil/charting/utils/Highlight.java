
package com.github.mikephil.charting.utils;

/**
 * Contains information needed to determine the highlighted value.
 * 
 * @author Philipp Jahoda
 */
public class Highlight {

    /** the x-index of the highlighted value */
    private int mXIndex;

    /** the value (on y-axis) representing the touch position */
    private float mVal;

    /** the index of the dataset the highlighted value is in */
    private int mDataSetIndex;

    /**
     * constructor
     * 
     * @param x the index of the highlighted value on the x-axis
     * @param val the value at the position the user touched
     * @param dataSet the index of the DataSet the highlighted value belongs to
     */
    public Highlight(int x, float val, int dataSet) {
        this.mXIndex = x;
        this.mVal = val;
        this.mDataSetIndex = dataSet;
    }

    /**
     * returns the index of the DataSet the highlighted value is in
     * 
     * @return
     */
    public int getDataSetIndex() {
        return mDataSetIndex;
    }

    /**
     * returns the index of the highlighted value on the x-axis
     * 
     * @return
     */
    public int getXIndex() {
        return mXIndex;
    }

    /**
     * returns the value (on the y-axis) that was highlighted (the value
     * representing the touch position)
     * 
     * @return
     */
    public float getVal() {
        return mVal;
    }

    /**
     * returns true if this highlight object is equal to the other (compares
     * xIndex and dataSetIndex)
     * 
     * @param h
     * @return
     */
    public boolean equalTo(Highlight h) {

        if (h == null)
            return false;
        else {
            if (this.mDataSetIndex == h.mDataSetIndex && this.mXIndex == h.mXIndex)
                return true;
            else
                return false;
        }
    }
}
