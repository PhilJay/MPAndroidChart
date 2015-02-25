
package com.github.mikephil.charting.components;

import android.graphics.Color;

import com.github.mikephil.charting.utils.Utils;

/**
 * Baseclass of all labels.
 * 
 * @author Philipp Jahoda
 */
public abstract class AxisBase extends ComponentBase {
    
    private int mGridColor = Color.GRAY;

    private int mAxisLineColor = Color.GRAY;

    private float mAxisLineWidth = 1f;

    /** flag that indicates if this axis is enabled or not */
    protected boolean mEnabled = true;

    /** flag indicating if the grid lines for this axis should be drawn */
    protected boolean mDrawGridLines = true;

    /** flag that indicates if the line alongside the axis is drawn or not */
    protected boolean mDrawAxisLine = true;

    /** default constructor */
    public AxisBase() {
        this.mTextSize = Utils.convertDpToPixel(10f);
        this.mXOffset = Utils.convertDpToPixel(5f);
        this.mYOffset = Utils.convertDpToPixel(5f);
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

    /**
     * Returns the longest formatted label (in terms of characters), this axis contains.
     * 
     * @return
     */
    public abstract String getLongestLabel();
}
