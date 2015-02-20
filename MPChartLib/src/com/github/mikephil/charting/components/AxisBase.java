
package com.github.mikephil.charting.components;

import android.graphics.Color;
import android.graphics.Typeface;

import com.github.mikephil.charting.utils.Utils;

/**
 * Baseclass of all labels.
 * 
 * @author Philipp Jahoda
 */
public abstract class AxisBase {

    /** the typeface to use for the labels */
    private Typeface mTypeface;

    /** the size of the label text */
    private float mTextSize = 10f;

    /** the text color to use */
    private int mTextColor = Color.BLACK;

    private int mGridColor = Color.GRAY;

    private int mAxisLineColor = Color.GRAY;

    private float mAxisLineWidth = 1f;

    /** flag that indicates if this axis is enabled or not */
    protected boolean mEnabled = true;

    /** flag indicating if the grid lines for this axis should be drawn */
    protected boolean mDrawGridLines = true;

    /** flag that indicates if the line alongside the axis is drawn or not */
    protected boolean mDrawAxisLine = false;

    /** default constructor */
    public AxisBase() {
        mTextSize = Utils.convertDpToPixel(10f);
    }

    /**
     * sets the size of the label text in pixels min = 6f, max = 24f, default
     * 10f
     * 
     * @param size
     */
    public void setTextSize(float size) {

        if (size > 24f)
            size = 24f;
        if (size < 6f)
            size = 6f;

        mTextSize = Utils.convertDpToPixel(size);
    }

    /**
     * returns the text size that is currently set for the labels
     * 
     * @return
     */
    public float getTextSize() {
        return mTextSize;
    }

    /**
     * sets the typeface that should be used for the labels
     * 
     * @param t
     */
    public void setTypeface(Typeface t) {
        mTypeface = t;
    }

    /**
     * returns the typeface that is used for the labels
     * 
     * @return
     */
    public Typeface getTypeface() {
        return mTypeface;
    }

    /**
     * Sets the text color to use for the labels. Make sure to use
     * getResources().getColor(...) when using a color from the resources.
     * 
     * @param color
     */
    public void setTextColor(int color) {
        mTextColor = color;
    }

    /**
     * Returns the text color that is set for the labels.
     * 
     * @return
     */
    public int getTextColor() {
        return mTextColor;
    }

    /**
     * Set this to true to enable drawing the grid lines for this axis.
     * 
     * @param enabled
     */
    public void setDrawGridLines(boolean enabled) {
        mDrawGridLines = enabled;
    }

    /**
     * Returns true if drawing grid lines is enabled for this axis.
     * 
     * @return
     */
    public boolean isDrawGridLinesEnabled() {
        return mDrawGridLines;
    }

    /**
     * Set this to true if the line alongside the axis should be drawn or not.
     * 
     * @param enabled
     */
    public void setDrawAxisLine(boolean enabled) {
        mDrawAxisLine = enabled;
    }

    /**
     * Returns true if the line alongside the axis should be drawn.
     * 
     * @return
     */
    public boolean isDrawAxisLineEnabled() {
        return mDrawAxisLine;
    }

    /**
     * Sets the color of the grid lines for this axis (the horizontal lines
     * coming from each label).
     * 
     * @param color
     */
    public void setGridColor(int color) {
        mGridColor = color;
    }

    /**
     * Returns the color of the grid lines for this axis (the horizontal lines
     * coming from each label).
     * 
     * @return
     */
    public int getGridColor() {
        return mGridColor;
    }

    /**
     * Sets the width of the border surrounding the chart in dp.
     * 
     * @param width
     */
    public void setAxisLineWidth(float width) {
        mAxisLineWidth = Utils.convertDpToPixel(width);
    }

    /**
     * Returns the width of the axis line (line alongside the axis).
     * 
     * @return
     */
    public float getAxisLineWidth() {
        return mAxisLineWidth;
    }

    /**
     * Sets the color of the border surrounding the chart.
     * 
     * @param color
     */
    public void setAxisLineColor(int color) {
        mAxisLineColor = color;
    }

    /**
     * Returns the color of the axis line (line alongside the axis).
     * 
     * @return
     */
    public int getAxisLineColor() {
        return mAxisLineColor;
    }

    /**
     * Set this to true to enable this axis from being drawn to the screen.
     * 
     * @param enabled
     */
    public void setEnabled(boolean enabled) {
        mEnabled = enabled;
    }

    /**
     * Returns true if the axis is enabled (will be drawn).
     * 
     * @return
     */
    public boolean isEnabled() {
        return mEnabled;
    }
}
