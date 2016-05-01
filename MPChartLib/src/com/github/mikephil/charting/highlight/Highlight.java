
package com.github.mikephil.charting.highlight;

/**
 * Contains information needed to determine the highlighted value.
 * 
 * @author Philipp Jahoda
 */
public class Highlight {

    /** the x-index of the highlighted value */
    private int mXIndex;

    /** the y-value of the highlighted value */
    private float mValue = Float.NaN;

    /** the index of the data object - in case it refers to more than one */
    private int mDataIndex;

    /** the index of the dataset the highlighted value is in */
    private int mDataSetIndex;

    /** index which value of a stacked bar entry is highlighted, default -1 */
    private int mStackIndex = -1;

    /** the range of the bar that is selected (only for stacked-barchart) */
    private Range mRange;

    /**
     * constructor
     *
     * @param x the index of the highlighted value on the x-axis
     * @param value the y-value of the highlighted value
     * @param dataIndex the index of the Data the highlighted value belongs to
     * @param dataSetIndex the index of the DataSet the highlighted value belongs to
     */
    public Highlight(int x, float value, int dataIndex, int dataSetIndex) {
        this.mXIndex = x;
        this.mValue = value;
        this.mDataIndex = dataIndex;
        this.mDataSetIndex = dataSetIndex;
    }
    /**
     * Constructor, only used for stacked-barchart.
     * 
     * @param x the index of the highlighted value on the x-axis
     * @param value the y-value of the highlighted value
     * @param dataIndex the index of the Data the highlighted value belongs to
     * @param dataSetIndex the index of the DataSet the highlighted value belongs to
     * @param stackIndex references which value of a stacked-bar entry has been
     *            selected
     */
    public Highlight(int x, float value, int dataIndex, int dataSetIndex, int stackIndex) {
        this(x, value, dataIndex, dataSetIndex);
        mStackIndex = stackIndex;
    }

    /**
     * Constructor, only used for stacked-barchart.
     *
     * @param x the index of the highlighted value on the x-axis
     * @param value the y-value of the highlighted value
     * @param dataIndex the index of the Data the highlighted value belongs to
     * @param dataSetIndex the index of the DataSet the highlighted value belongs to
     * @param stackIndex references which value of a stacked-bar entry has been
     *            selected
     * @param range the range the selected stack-value is in
     */
    public Highlight(int x, float value, int dataIndex, int dataSetIndex, int stackIndex, Range range) {
        this(x, value, dataIndex, dataSetIndex, stackIndex);
        this.mRange = range;
    }

    /**
     * Constructor, only used for stacked-barchart.
     *
     * @param x the index of the highlighted value on the x-axis
     * @param dataSetIndex the index of the DataSet the highlighted value belongs to
     */
    public Highlight(int x, int dataSetIndex) {
        this(x, Float.NaN, 0, dataSetIndex, -1);
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
     * returns the y-value of the highlighted value
     *
     * @return
     */
    public float getValue() {
        return mValue;
    }

    /**
     * the index of the data object - in case it refers to more than one
     *
     * @return
     */
    public int getDataIndex() {
        return mDataIndex;
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
     * Only needed if a stacked-barchart entry was highlighted. References the
     * selected value within the stacked-entry.
     * 
     * @return
     */
    public int getStackIndex() {
        return mStackIndex;
    }

    /**
     * Returns the range of values the selected value of a stacked bar is in. (this is only relevant for stacked-barchart)
     * @return
     */
    public Range getRange() {
        return mRange;
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
