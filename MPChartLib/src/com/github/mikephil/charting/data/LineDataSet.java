
package com.github.mikephil.charting.data;

import android.content.Context;
import android.graphics.Color;
import android.graphics.DashPathEffect;

import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.Utils;

import java.util.ArrayList;

public class LineDataSet extends BarLineScatterCandleDataSet {

    /** arraylist representing all colors that are used for the circles */
    private ArrayList<Integer> mCircleColors = null;

    /** the color that is used for filling the line surface */
    private int mFillColor = Color.rgb(140, 234, 255);

    /** transparency used for filling line surface */
    private int mFillAlpha = 85;

    /** the radius of the circle-shaped value indicators */
    private float mCircleSize = 4f;

    /** the width of the drawn data lines */
    private float mLineWidth = 1f;

    /** sets the intensity of the cubic lines */
    private float mCubicIntensity = 0.2f;

    /** the path effect of this DataSet that makes dashed lines possible */
    private DashPathEffect mDashPathEffect = null;

    /** if true, drawing circles is enabled */
    private boolean mDrawCircles = true;

    /** if true, the data will also be drawn filled */
    private boolean mDrawFilled = false;

    /** if true, cubic lines are drawn instead of linear */
    private boolean mDrawCubic = false;

    public LineDataSet(ArrayList<Entry> yVals, String label) {
        super(yVals, label);

        // mCircleSize = Utils.convertDpToPixel(4f);
        // mLineWidth = Utils.convertDpToPixel(1f);

        mCircleColors = new ArrayList<Integer>();

        // default colors
        // mColors.add(Color.rgb(192, 255, 140));
        // mColors.add(Color.rgb(255, 247, 140));
        mCircleColors.add(Color.rgb(140, 234, 255));
    }

    @Override
    public DataSet copy() {

        ArrayList<Entry> yVals = new ArrayList<Entry>();

        for (int i = 0; i < mYVals.size(); i++) {
            yVals.add(mYVals.get(i).copy());
        }

        LineDataSet copied = new LineDataSet(yVals, getLabel());
        copied.mColors = mColors;
        copied.mLineWidth = mLineWidth;
        copied.mCircleSize = mCircleSize;
        copied.mCircleColors = mCircleColors;
        copied.mDashPathEffect = mDashPathEffect;
        copied.mDrawCircles = mDrawCircles;
        copied.mDrawFilled = mDrawFilled;
        copied.mDrawCubic = mDrawCubic;
        copied.mHighLightColor = mHighLightColor;

        return copied;
    }

    /**
     * returns the color that is used for filling the line surface
     * 
     * @return
     */
    public int getFillColor() {
        return mFillColor;
    }

    /**
     * sets the color that is used for filling the line surface
     * 
     * @param color
     */
    public void setFillColor(int color) {
        mFillColor = color;
    }

    /**
     * returns the alpha value that is used for filling the line surface,
     * default: 85
     * 
     * @return
     */
    public int getFillAlpha() {
        return mFillAlpha;
    }

    /**
     * sets the alpha value (transparency) that is used for filling the line
     * surface (0-255), default: 85
     * 
     * @param color
     */
    public void setFillAlpha(int alpha) {
        mFillAlpha = alpha;
    }

    /**
     * set the line width of the chart (min = 0.2f, max = 10f); default 1f NOTE:
     * thinner line == better performance, thicker line == worse performance
     * 
     * @param width
     */
    public void setLineWidth(float width) {

        if (width < 0.2f)
            width = 0.5f;
        if (width > 10.0f)
            width = 10.0f;
        mLineWidth = Utils.convertDpToPixel(width);
    }

    /**
     * returns the width of the drawn chart line
     * 
     * @return
     */
    public float getLineWidth() {
        return mLineWidth;
    }

    /**
     * Sets the intensity for cubic lines (if enabled). Max = 1f = very cubic,
     * Min = 0.05f = low cubic effect, Default: 0.2f
     * 
     * @param intensity
     */
    public void setCubicIntensity(float intensity) {

        if (intensity > 1f)
            intensity = 1f;
        if (intensity < 0.05f)
            intensity = 0.05f;

        mCubicIntensity = intensity;
    }

    /**
     * Returns the intensity of the cubic lines (the effect intensity).
     * 
     * @return
     */
    public float getCubicIntensity() {
        return mCubicIntensity;
    }

    /**
     * sets the size (radius) of the circle shpaed value indicators, default
     * size = 4f
     * 
     * @param size
     */
    public void setCircleSize(float size) {
        mCircleSize = Utils.convertDpToPixel(size);
    }

    /**
     * returns the circlesize
     */
    public float getCircleSize() {
        return mCircleSize;
    }

    /**
     * Enables the line to be drawn in dashed mode, e.g. like this "- - - - - -"
     * 
     * @param lineLength the length of the line pieces
     * @param spaceLength the length of space inbetween the pieces
     * @param phase offset, in degrees (normally, use 0)
     */
    public void enableDashedLine(float lineLength, float spaceLength, float phase) {
        mDashPathEffect = new DashPathEffect(new float[] {
                lineLength, spaceLength
        }, phase);
    }

    /**
     * Disables the line to be drawn in dashed mode.
     */
    public void disableDashedLine() {
        mDashPathEffect = null;
    }

    /**
     * Returns true if the dashed-line effect is enabled, false if not.
     * 
     * @return
     */
    public boolean isDashedLineEnabled() {
        return mDashPathEffect == null ? false : true;
    }

    /**
     * returns the DashPathEffect that is set for this DataSet
     * 
     * @return
     */
    public DashPathEffect getDashPathEffect() {
        return mDashPathEffect;
    }

    /**
     * set this to true to enable the drawing of circle indicators for this
     * DataSet, default true
     * 
     * @param enabled
     */
    public void setDrawCircles(boolean enabled) {
        this.mDrawCircles = enabled;
    }

    /**
     * returns true if drawing circles for this DataSet is enabled, false if not
     * 
     * @return
     */
    public boolean isDrawCirclesEnabled() {
        return mDrawCircles;
    }

    /**
     * Set to true if the DataSet should be drawn filled (surface), and not just
     * as a line, disabling this will give up to 20% performance boost on large
     * datasets, default: false
     * 
     * @param filled
     */
    public void setDrawFilled(boolean filled) {
        mDrawFilled = filled;
    }

    /**
     * returns true if filled drawing is enabled, false if not
     * 
     * @return
     */
    public boolean isDrawFilledEnabled() {
        return mDrawFilled;
    }

    /**
     * If set to true, the linechart lines are drawn in cubic-style instead of
     * linear. Default: false
     * 
     * @param enabled
     */
    public void setDrawCubic(boolean enabled) {
        mDrawCubic = enabled;
    }

    /**
     * returns true if drawing cubic lines is enabled, false if not.
     * 
     * @return
     */
    public boolean isDrawCubicEnabled() {
        return mDrawCubic;
    }

    /** ALL CODE BELOW RELATED TO CIRCLE-COLORS */

    /**
     * returns all colors specified for the circles
     * 
     * @return
     */
    public ArrayList<Integer> getCircleColors() {
        return mCircleColors;
    }

    /**
     * Returns the color at the given index of the DataSet's circle-color array.
     * Performs a IndexOutOfBounds check by modulus.
     * 
     * @param index
     * @return
     */
    public int getCircleColor(int index) {
        return mCircleColors.get(index % mCircleColors.size());
    }

    /**
     * Sets the colors that should be used for the circles of this DataSet.
     * Colors are reused as soon as the number of Entries the DataSet represents
     * is higher than the size of the colors array. Make sure that the colors
     * are already prepared (by calling getResources().getColor(...)) before
     * adding them to the DataSet.
     * 
     * @param colors
     */
    public void setCircleColors(ArrayList<Integer> colors) {
        mCircleColors = colors;
    }

    /**
     * Sets the colors that should be used for the circles of this DataSet.
     * Colors are reused as soon as the number of Entries the DataSet represents
     * is higher than the size of the colors array. Make sure that the colors
     * are already prepared (by calling getResources().getColor(...)) before
     * adding them to the DataSet.
     * 
     * @param colors
     */
    public void setCircleColors(int[] colors) {
        this.mCircleColors = ColorTemplate.createColors(colors);
    }

    /**
     * ets the colors that should be used for the circles of this DataSet.
     * Colors are reused as soon as the number of Entries the DataSet represents
     * is higher than the size of the colors array. You can use
     * "new String[] { R.color.red, R.color.green, ... }" to provide colors for
     * this method. Internally, the colors are resolved using
     * getResources().getColor(...)
     * 
     * @param colors
     */
    public void setCircleColors(int[] colors, Context c) {

        ArrayList<Integer> clrs = new ArrayList<Integer>();

        for (int color : colors) {
            clrs.add(c.getResources().getColor(color));
        }

        mCircleColors = clrs;
    }

    /**
     * Sets the one and ONLY color that should be used for this DataSet.
     * Internally, this recreates the colors array and adds the specified color.
     * 
     * @param color
     */
    public void setCircleColor(int color) {
        resetCircleColors();
        mCircleColors.add(color);
    }

    /**
     * resets the circle-colors array and creates a new one
     */
    public void resetCircleColors() {
        mCircleColors = new ArrayList<Integer>();
    }
}
