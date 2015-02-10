
package com.github.mikephil.charting.utils;

import android.graphics.Color;
import android.graphics.Typeface;

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

    /** the color of the axis lines */
    private int mAxisColor = Color.BLACK;

    private int mGridColor = Color.GRAY;

    /** flag indicating if the grid lines for this axis should be drawn */
    private boolean mDrawGridLines = true;

    /** default constructor */
    public AxisBase() {
        mTextSize = Utils.convertDpToPixel(10f);
    }

    /**
     * sets the size of the label text in pixels min = 6f, max = 16f, default
     * 10f
     * 
     * @param size
     */
    public void setTextSize(float size) {

        if (size > 16f)
            size = 16f;
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
     * Returns the color of the axis line.
     * 
     * @return
     */
    public int getAxisColor() {
        return mAxisColor;
    }

    /**
     * Sets the color of the axis line.
     * 
     * @param color
     */
    public void setAxisColor(int color) {
        mAxisColor = color;
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
     * Sets the color of the grid lines for this axis.
     * 
     * @param color
     */
    public void setGridColor(int color) {
        mGridColor = color;
    }

    /**
     * Returns the color of the grid lines for this axis.
     * 
     * @return
     */
    public int getGridColor() {
        return mGridColor;
    }
}
