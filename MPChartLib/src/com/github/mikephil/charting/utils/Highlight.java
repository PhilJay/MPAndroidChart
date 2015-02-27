
package com.github.mikephil.charting.utils;

/**
 * Contains information needed to determine the highlighted value.
 * 
 * @author Philipp Jahoda
 */
public class Highlight {

    /** the x-index of the highlighted value */
    private int mXIndex;

    /** the index of the dataset the highlighted value is in */
    private int mDataSetIndex;

    /** index which value of a stacked bar entry is highlighted, default -1 */
    private int mStackIndex = -1;

    /**
     * constructor
     * 
     * @param x the index of the highlighted value on the x-axis
     * @param val the value at the position the user touched
     * @param dataSet the index of the DataSet the highlighted value belongs to
     */
    public Highlight(int x, int dataSet) {
        this.mXIndex = x;
        this.mDataSetIndex = dataSet;
    }

    /**
     * Constructor, only used for stacked-barchart.
     * 
     * @param x the index of the highlighted value on the x-axis
     * @param val the value at the position the user touched
     * @param dataSet the index of the DataSet the highlighted value belongs to
     * @param stackIndex references which value of a stacked-bar entry has been
     *            selected
     */
    public Highlight(int x, int dataSet, int stackIndex) {
        this(x, dataSet);
        mStackIndex = stackIndex;
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
     * Only needed if a stacked-barchart entry was highlighted. References the
     * selected value within the stacked-entry.
     * 
     * @return
     */
    public int getStackIndex() {
        return mStackIndex;
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
            if (this.mDataSetIndex == h.mDataSetIndex && this.mXIndex == h.mXIndex
                    && this.mStackIndex == h.mStackIndex)
                return true;
            else
                return false;
        }
    }

    @Override
    public String toString() {
        return "Highlight, xIndex: " + mXIndex + ", dataSetIndex: " + mDataSetIndex
                + ", stackIndex (only stacked barentry): " + mStackIndex;
    }
}
