
package com.github.mikephil.charting.components;

import java.util.ArrayList;

/**
 * Class representing the x-axis labels settings. Only use the setter methods to
 * modify it. Do not access public variables directly. Be aware that not all
 * features the XLabels class provides are suitable for the RadarChart.
 * 
 * @author Philipp Jahoda
 */
public class XAxis extends AxisBase {

    /** the arraylist containing all the x-axis labels */
    protected ArrayList<String> mValues = new ArrayList<String>();

    /**
     * width of the x-axis labels in pixels - this is calculated by the
     * calcTextWidth() method of the utils
     */
    public int mLabelWidth = 1;

    /**
     * height of the x-axis labels in pixels - this is calculated by the
     * calcTextHeight() method of the utils
     */
    public int mLabelHeight = 1;

    /**
     * the space that should be left out (in characters) between the x-axis
     * labels
     */
    private int mSpaceBetweenLabels = 4;

    /**
     * the modulus that indicates if a value at a specified index in an
     * array(list) for the x-axis-labels is drawn or not. If index % modulus ==
     * 0 DRAW, else dont draw.
     */
    public int mAxisLabelModulus = 1;

    /**
     * the modulus that indicates if a value at a specified index in an
     * array(list) for the y-axis-labels is drawn or not. If index % modulus ==
     * 0 DRAW, else dont draw. THIS IS ONLY FOR HORIZONTAL BARCHART.
     */
    public int mYAxisLabelModulus = 1;

    /** if true, x-axis label text is centered when using barcharts */
    private boolean mCenterXAxisLabels = false;

    /**
     * if set to true, the chart will avoid that the first and last label entry
     * in the chart "clip" off the edge of the chart
     */
    private boolean mAvoidFirstLastClipping = false;

    /**
     * if set to true, the x-axis label entries will adjust themselves when
     * scaling the graph
     */
    protected boolean mAdjustXAxisLabels = true;

    /** the position of the x-labels relative to the chart */
    private XAxisPosition mPosition = XAxisPosition.TOP;

    /** enum for the position of the x-labels relative to the chart */
    public enum XAxisPosition {
        TOP, BOTTOM, BOTH_SIDED, TOP_INSIDE, BOTTOM_INSIDE
    }

    /**
     * returns true if centering x-axis labels when using barcharts is enabled,
     * false if not
     * 
     * @return
     */
    public boolean isCenterXLabelsEnabled() {
        return mCenterXAxisLabels;
    }

    /**
     * set this to true to center the x-label text when using barcharts ,
     * default: false
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
    public XAxisPosition getPosition() {
        return mPosition;
    }

    /**
     * sets the position of the x-labels
     * 
     * @param pos
     */
    public void setPosition(XAxisPosition pos) {
        mPosition = pos;
    }

    /**
     * Sets the space (in characters) that should be left out between the x-axis
     * labels, default 4
     * 
     * @param space
     */
    public void setSpaceBetweenLabels(int space) {
        mSpaceBetweenLabels = space;
    }

    /**
     * Returns the space (in characters) that should be left out between the
     * x-axis labels
     * 
     * @param space
     */
    public int getSpaceBetweenLabels() {
        return mSpaceBetweenLabels;
    }

    /**
     * if set to true, the chart will avoid that the first and last label entry
     * in the chart "clip" off the edge of the chart or the screen
     * 
     * @param enabled
     */
    public void setAvoidFirstLastClipping(boolean enabled) {
        mAvoidFirstLastClipping = enabled;
    }

    /**
     * returns true if avoid-first-lastclipping is enabled, false if not
     * 
     * @return
     */
    public boolean isAvoidFirstLastClippingEnabled() {
        return mAvoidFirstLastClipping;
    }

    /**
     * Sets the labels for this axis.
     * 
     * @param values
     */
    public void setValues(ArrayList<String> values) {
        mValues = values;
    }

    /**
     * Returns the labels for this axis.
     * 
     * @return
     */
    public ArrayList<String> getValues() {
        return mValues;
    }
}
