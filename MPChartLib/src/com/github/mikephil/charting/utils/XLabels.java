package com.github.mikephil.charting.utils;

import android.graphics.Typeface;

/**
 * Class representing the x-axis labels settings. Only use the setter methods
 * to modify it. Do not access public variables directly.
 * 
 * @author Philipp Jahoda
 */
public class XLabels {
    
    /**
     * width of the x-axis labels in pixels - this is calculated by the
     * calcTextWidth() method of the utils
     */
    public int mXLabelWidth = 1;
    
    /**
     * height of the x-axis labels in pixels - this is calculated by the
     * calcTextHeight() method of the utils
     */
    public int mXLabelHeight= 1;
    
    /**
     * the modulus that indicates if a value at a specified index in an
     * array(list) for the x-axis-labels is drawn or not. If index % modulus ==
     * 0 DRAW, else dont draw.
     */
    public int mXAxisLabelModulus = 1;
    
    /** the typeface to use for the labels */
    private Typeface mTypeface;
    
    /** if true, x-axis label text is centered when using barcharts */
    private boolean mCenterXAxisLabels = false;
    
    /**
     * if set to true, the x-axis label entries will adjust themselves when
     * scaling the graph
     */
    protected boolean mAdjustXAxisLabels = true;
    
    /** the position of the x-labels relative to the chart */
    private XLabelPosition mPosition = XLabelPosition.TOP;

    /** enum for the position of the x-labels relative to the chart */
    public enum XLabelPosition {
        TOP, BOTTOM, BOTH_SIDED
    }
    
    /**
     * returns true if centering x-axis labels when using barcharts is enabled, false if not
     * @return
     */
    public boolean isCenterXLabelsEnabled() {
        return mCenterXAxisLabels;
    }
    
    /**
     * set this to true to center the x-label text when using barcharts , default: false
     * 
     * @param enabled
     */
    public void setCenterXLabelText(boolean enabled) {
        mCenterXAxisLabels = enabled;
    }
    
    /**
     * if set to true, the x-label entries will adjust themselves when scaling
     * the graph default: true
     * 
     * @param enabled
     */
    public void setAdjustXLabels(boolean enabled) {
        mAdjustXAxisLabels = enabled;
    }

    /**
     * returns true if the x-labels adjust themselves when scaling the graph,
     * false if not
     * 
     * @return
     */
    public boolean isAdjustXLabelsEnabled() {
        return mAdjustXAxisLabels;
    }
    
    /**
     * returns the position of the x-labels
     */
    public XLabelPosition getPosition() {
        return mPosition;
    }

    /**
     * sets the position of the x-labels
     * 
     * @param pos
     */
    public void setPosition(XLabelPosition pos) {
        mPosition = pos;
    }
    
    /**
     * sets the typeface that should be used for the x-labels
     * 
     * @param t
     */
    public void setTypeface(Typeface t) {
        mTypeface = t;
    }
    
    /**
     * returns the typeface that is used for the x-labels
     * @return
     */
    public Typeface getTypeface() {
        return mTypeface;
    }
}
