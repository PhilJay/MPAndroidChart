
package com.github.mikephil.charting.highlight;

import android.util.Log;

import com.github.mikephil.charting.components.YAxis;

import java.util.Locale;

/**
 * Contains information needed to determine the highlighted value.
 *
 * @author Philipp Jahoda
 */
public class Highlight {

    /**
     * Enum that specifies with chart object is associated with a highlight.
     * NULL replaces highlight == null, ala kotlin.
     */
    public enum Type {
        VALUE, X_AXIS, LEFT_AXIS, RIGHT_AXIS, NULL
    }

    /**
     * The type of this highlight
     */
    protected Type mType;

    /**
     * the x-value of the highlighted value
     */
    protected float mX = Float.NaN;

    /**
     * the y-value of the highlighted value
     */
    protected float mY = Float.NaN;

    /**
     * the x-pixel of the highlight
     */
    protected float mXPx;

    /**
     * the y-pixel of the highlight
     */
    protected float mYPx;

    /**
     * the index of the data object - in case it refers to more than one
     */
    protected int mDataIndex = -1;

    /**
     * the index of the dataset the highlighted value is in
     */
    protected int mDataSetIndex;

    /**
     * index which value of a stacked bar entry is highlighted, default -1
     */
    protected int mStackIndex = -1;

    /**
     * the axis the highlighted value belongs to
     */
    protected YAxis.AxisDependency axis;

    /**
     * the x-position (pixels) on which this highlight object was last drawn
     */
    protected float mDrawX;

    /**
     * the y-position (pixels) on which this highlight object was last drawn
     */
    protected float mDrawY;

    public Highlight(float x, float y, int dataSetIndex) {
        this.mX = x;
        this.mY = y;
        this.mDataSetIndex = dataSetIndex;
        mDataIndex = -1;
        mStackIndex = -1;
        mType = Type.VALUE;
    }

//    public Highlight(float x, int dataSetIndex, int stackIndex) {
//        this(x, Float.NaN, dataSetIndex);
//        this.mStackIndex = stackIndex;
//    }

    /**
     * constructor
     *
     * @param x            the x-value of the highlighted value
     * @param y            the y-value of the highlighted value
     * @param dataSetIndex the index of the DataSet the highlighted value belongs to
     */
    public Highlight(float x, float y, float xPx, float yPx, int dataSetIndex, int dataIndex, YAxis.AxisDependency axis) {
        this.mX = x;
        this.mY = y;
        this.mXPx = xPx;
        this.mYPx = yPx;
        this.mDataSetIndex = dataSetIndex;
        this.axis = axis;
        this.mDataIndex = dataIndex;
        this.mStackIndex = -1;
        mType = Type.VALUE;
        Log.i("___new Highlight", this.toString());
    }

    /**
     * Constructor, only used for stacked-barchart.
     *
     * @param x            the index of the highlighted value on the x-axis
     * @param y            the y-value of the highlighted value
     * @param dataSetIndex the index of the DataSet the highlighted value belongs to
     * @param stackIndex   references which value of a stacked-bar entry has been
     *                     selected
     */
    public Highlight(float x, float y, float xPx, float yPx, int dataSetIndex, int dataIndex, int stackIndex, YAxis.AxisDependency axis) {
        this(x, y, xPx, yPx, dataSetIndex, dataIndex, axis);
        this.mStackIndex = stackIndex;
        mType = Type.VALUE;
    }

    /**
     * constructor to support axis highlights with touch position
     *
     * @param xVal          x value
     * @param yVal          y value
     * @param highlightType highlight Type
     */
    public Highlight(float xVal, float yVal, Type highlightType, float xPix, float yPix) {
        this(xVal, yVal, -1);
        mXPx = xPix;
        mYPx = yPix;
        mStackIndex = -1;
        mDataIndex = -1;
        mType = highlightType;
        if (highlightType == Type.LEFT_AXIS)
            axis = YAxis.AxisDependency.LEFT;
        else if (highlightType == Type.RIGHT_AXIS)
            axis = YAxis.AxisDependency.RIGHT;
        Log.i("Highlight", String.format(Locale.getDefault(),
                "new %s @ %.1f, %.1f", mType.name(), xVal, yVal));
    }

    /**
     * constructor to support axis highlights where the position is not known
     *
     * @param highlightType type
     */
    public Highlight(Type highlightType) {
        this(Float.NaN, Float.NaN, highlightType != null ? highlightType : Type.VALUE, Float.NaN, Float.NaN);
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
     * Sets the x- and y-position (pixels) where this highlight was last drawn.
     *
     * @param x
     * @param y
     */
    public void setDraw(float x, float y) {
        this.mDrawX = x;
        this.mDrawY = y;
    }

    /**
     * Returns the x-position in pixels where this highlight object was last drawn.
     *
     * @return
     */
    public float getDrawX() {
        return mDrawX;
    }

    /**
     * Returns the y-position in pixels where this highlight object was last drawn.
     *
     * @return
     */
    public float getDrawY() {
        return mDrawY;
    }

    /**
     * Returns the type of this highlight.
     * @return type
     */
    public Type getType() {
        return mType;
    }

    public boolean isValue() { return mType == Type.VALUE; }
    public boolean isXAxis() { return mType == Type.X_AXIS; }
    public boolean isLeftAxis() { return mType == Type.LEFT_AXIS; }
    public boolean isRightAxis() { return mType == Type.RIGHT_AXIS; }
    public boolean isNull() { return mType == Type.NULL; }

    /**
     * Generates a hash code for this Highlight.
     *
     * Heavily influenced by
     * https://stackoverflow.com/questions/113511/best-implementation-for-hashcode-method-for-a-collection
     *
     * @return hash code
     */
    @Override
    public int hashCode() {
        int typeHash = 0;
        // different types have different hash codes.
        //    the values below are primes, chosen kinda randomly.
        if (isNull())           typeHash = 14149;
        else if (isValue())     typeHash = 22501;
        else if (isXAxis())     typeHash = 42719;
        else if (isLeftAxis())  typeHash = 70001;
        else if (isRightAxis()) typeHash = 82837;

        // different x's have different hash codes
        int xHash = Float.floatToIntBits(mX);

        // ignore the y's - probably will not have hundreds of concurrent highlights
        int yHash = 0;

        // different data sets have different hash codes
        int setHash = mDataSetIndex;

        return typeHash + xHash + yHash + setHash;
    }

    /**
     * Returns true if this highlight object is equal to the other
     *
     * @param obj other
     * @return this === other
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Highlight)) return false;
        Highlight h = (Highlight) obj;
        if (isNull() && h.isNull()) return true;
        if (mX != h.mX) return false;
        if (mType != h.getType()) return false;
        if (mDataIndex != h.getDataIndex()) return false;
        if (mDataSetIndex != h.getDataSetIndex()) return false;
        if (mStackIndex != h.getStackIndex()) return false;

        return true;
    }

    /**
     * Returns true if this highlight object is equal to the other (compares
     * xIndex and dataSetIndex)
     *
     * @deprecated use equals
     * @param h
     * @return
     */
    @Deprecated
    public boolean equalTo(Highlight h) {
        return equals(h);
    }

    @Override
    public String toString() {
        return String.format(Locale.getDefault(),
                "Highlight: type= %s, x= %.1f, y= %.1f, setIndex= %d, stackIndex= %d, dataIndex= %d",
                mType.name(), mX, mY, mDataSetIndex, mStackIndex, mDataIndex);
    }
}
