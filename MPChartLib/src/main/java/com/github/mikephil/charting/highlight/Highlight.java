
package com.github.mikephil.charting.highlight;

import com.github.mikephil.charting.components.YAxis;

/**
 * Contains information needed to determine the highlighted value.
 *
 * @author Philipp Jahoda
 */
public class Highlight {

    /**
     * the x-value of the highlighted value
     */
    private float mX = Float.NaN;

    /**
     * the y-value of the highlighted value
     */
    private float mY = Float.NaN;

    /**
     * the x-pixel of the highlight
     */
    private float mXPx;

    /**
     * the y-pixel of the highlight
     */
    private float mYPx;

    /**
     * the index of the data object - in case it refers to more than one
     */
    private int mDataIndex = -1;

    /**
     * the index of the dataset the highlighted value is in
     */
    private int mDataSetIndex;

    /**
     * index which value of a stacked bar entry is highlighted, default -1
     */
    private int mStackIndex = -1;

    /**
     * the range of the bar that is selected (only for stacked-barchart)
     */
    private Range mRange;

    /**
     * the axis the highlighted value belongs to
     */
    private YAxis.AxisDependency axis;

    public Highlight(float x, int dataSetIndex) {
        this.mX = x;
        this.mDataSetIndex = dataSetIndex;
    }

    /**
     * constructor
     *
     * @param x            the x-value of the highlighted value
     * @param y            the y-value of the highlighted value
     * @param dataSetIndex the index of the DataSet the highlighted value belongs to
     */
    public Highlight(float x, float y, float xPx, float yPx, int dataSetIndex, YAxis.AxisDependency axis) {
        this.mX = x;
        this.mY = y;
        this.mXPx = xPx;
        this.mYPx = yPx;
        this.mDataSetIndex = dataSetIndex;
        this.axis = axis;
    }

    /**
     * Constructor, only used for stacked-barchart.
     *
     * @param x            the index of the highlighted value on the x-axis
     * @param y            the y-value of the highlighted value
     * @param dataSetIndex the index of the DataSet the highlighted value belongs to
     * @param stackIndex   references which value of a stacked-bar entry has been
     *                     selected
     * @param range        the range the selected stack-value is in
     */
    public Highlight(float x, float y, float xPx, float yPx, int dataSetIndex, int stackIndex, Range range,
                     YAxis.AxisDependency axis) {
        this(x, y, xPx, yPx, dataSetIndex, axis);
        this.mStackIndex = stackIndex;
        this.mRange = range;
    }

    /**
     * returns the x-value of the highlighted value
     *
     * @return
     */
    public float getX() {
        return mX;
    }

    /**
     * returns the y-value of the highlighted value
     *
     * @return
     */
    public float getY() {
        return mY;
    }

    /**
     * returns the x-position of the highlight in pixels
     */
    public float getXPx() {
        return mXPx;
    }

    /**
     * returns the y-position of the highlight in pixels
     */
    public float getYPx() {
        return mYPx;
    }

    /**
     * the index of the data object - in case it refers to more than one
     *
     * @return
     */
    public int getDataIndex() {
        return mDataIndex;
    }

    public void setDataIndex(int mDataIndex) {
        this.mDataIndex = mDataIndex;
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
     *
     * @return
     */
    public Range getRange() {
        return mRange;
    }

    public boolean isStacked() {
        return mStackIndex >= 0;
    }

    /**
     * Returns the axis the highlighted value belongs to.
     *
     * @return
     */
    public YAxis.AxisDependency getAxis() {
        return axis;
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
                    && this.mStackIndex == h.mStackIndex && this.mDataIndex == h.mDataIndex)
                return true;
            else
                return false;
        }
    }

    @Override
    public String toString() {
        return "Highlight, x: " + mX + "y: " + mY + ", dataSetIndex: " + mDataSetIndex
                + ", stackIndex (only stacked barentry): " + mStackIndex;
    }
}
