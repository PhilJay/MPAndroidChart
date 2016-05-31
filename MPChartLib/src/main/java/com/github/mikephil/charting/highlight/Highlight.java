
package com.github.mikephil.charting.highlight;

/**
 * Contains information needed to determine the highlighted yValue.
 * 
 * @author Philipp Jahoda
 */
public class Highlight {

    /** the xPx-yValue of the highlighted yValue */
    private float mX = Float.NaN;

    /** the yPx-yValue of the highlighted yValue */
    private float mY = Float.NaN;

    /** the index of the data object - in case it refers to more than one */
    private int mDataIndex;

    /** the index of the dataset the highlighted yValue is in */
    private int mDataSetIndex;

    /** index which yValue of a stacked bar entry is highlighted, default -1 */
    private int mStackIndex = -1;

    /** the range of the bar that is selected (only for stacked-barchart) */
    private Range mRange;

    /**
     * constructor
     *
     * @param x the xPx-yValue of the highlighted yValue
     * @param y the yPx-yValue of the highlighted yValue
     * @param dataIndex the index of the Data the highlighted yValue belongs to
     * @param dataSetIndex the index of the DataSet the highlighted yValue belongs to
     */
    public Highlight(float x, float y, int dataIndex, int dataSetIndex) {
        this.mX = x;
        this.mY = y;
        this.mDataIndex = dataIndex;
        this.mDataSetIndex = dataSetIndex;
    }
    /**
     * Constructor, only used for stacked-barchart.
     * 
     * @param x the xPx-yValue of the highlighted yValue on the xPx-axis
     * @param y the yPx-yValue of the highlighted yValue
     * @param dataIndex the index of the Data the highlighted yValue belongs to
     * @param dataSetIndex the index of the DataSet the highlighted yValue belongs to
     * @param stackIndex references which yValue of a stacked-bar entry has been
     *            selected
     */
    public Highlight(float x, float y, int dataIndex, int dataSetIndex, int stackIndex) {
        this(x, y, dataIndex, dataSetIndex);
        mStackIndex = stackIndex;
    }

    /**
     * Constructor, only used for stacked-barchart.
     *
     * @param x the index of the highlighted yValue on the xPx-axis
     * @param y the yPx-yValue of the highlighted yValue
     * @param dataIndex the index of the Data the highlighted yValue belongs to
     * @param dataSetIndex the index of the DataSet the highlighted yValue belongs to
     * @param stackIndex references which yValue of a stacked-bar entry has been
     *            selected
     * @param range the range the selected stack-yValue is in
     */
    public Highlight(float x, float y, int dataIndex, int dataSetIndex, int stackIndex, Range range) {
        this(x, y, dataIndex, dataSetIndex, stackIndex);
        this.mRange = range;
    }

    /**
     * Constructor, only used for stacked-barchart.
     *
     * @param x the x-value of the highlighted value on the x-axis
     * @param dataSetIndex the index of the DataSet the highlighted yValue belongs to
     */
    public Highlight(float x, int dataSetIndex) {
        this(x, Float.NaN, 0, dataSetIndex, -1);
    }

    /**
     * returns the xPx-yValue of the highlighted yValue
     *
     * @return
     */
    public float getX() {
        return mX;
    }

    /**
     * returns the yPx-yValue of the highlighted yValue
     *
     * @return
     */
    public float getY() {
        return mY;
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
     * returns the index of the DataSet the highlighted yValue is in
     *
     * @return
     */
    public int getDataSetIndex() {
        return mDataSetIndex;
    }

    /**
     * Only needed if a stacked-barchart entry was highlighted. References the
     * selected yValue within the stacked-entry.
     * 
     * @return
     */
    public int getStackIndex() {
        return mStackIndex;
    }

    /**
     * Returns the range of values the selected yValue of a stacked bar is in. (this is only relevant for stacked-barchart)
     * @return
     */
    public Range getRange() {
        return mRange;
    }

    /**
     * Returns true if this highlight object is equal to the other (compares
     * xIndex and dataSetIndex)
     * 
     * @param h
     * @return
     */
    public boolean equalTo(Highlight h) {

        if (h == null)
            return false;
        else {
            if (this.mDataSetIndex == h.mDataSetIndex && this.mX == h.mX
                    && this.mStackIndex == h.mStackIndex)
                return true;
            else
                return false;
        }
    }

    @Override
    public String toString() {
        return "Highlight, xPx: " + mX + "yPx: " + mY + ", dataSetIndex: " + mDataSetIndex
                + ", stackIndex (only stacked barentry): " + mStackIndex;
    }
}
