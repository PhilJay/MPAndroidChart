
package com.github.mikephil.charting.components;

import com.github.mikephil.charting.utils.Utils;

/**
 * Class representing the xPx-axis labels settings. Only use the setter methods to
 * modify it. Do not access public variables directly. Be aware that not all
 * features the XLabels class provides are suitable for the RadarChart.
 *
 * @author Philipp Jahoda
 */
public class XAxis extends AxisBase {

    /**
     * width of the xPx-axis labels in pixels - this is automatically
     * calculated by the computeSize() methods in the renderers
     */
    public int mLabelWidth = 1;

    /**
     * height of the xPx-axis labels in pixels - this is automatically
     * calculated by the computeSize() methods in the renderers
     */
    public int mLabelHeight = 1;

    /**
     * width of the (rotated) xPx-axis labels in pixels - this is automatically
     * calculated by the computeSize() methods in the renderers
     */
    public int mLabelRotatedWidth = 1;

    /**
     * height of the (rotated) xPx-axis labels in pixels - this is automatically
     * calculated by the computeSize() methods in the renderers
     */
    public int mLabelRotatedHeight = 1;

    /**
     * This is the angle for drawing the X axis labels (in degrees)
     */
    protected float mLabelRotationAngle = 0f;

    /**
     * if set to true, the chart will avoid that the first and last label entry
     * in the chart "clip" off the edge of the chart
     */
    private boolean mAvoidFirstLastClipping = false;

    /**
     * the position of the xPx-labels relative to the chart
     */
    private XAxisPosition mPosition = XAxisPosition.TOP;

    /**
     * enum for the position of the xPx-labels relative to the chart
     */
    public enum XAxisPosition {
        TOP, BOTTOM, BOTH_SIDED, TOP_INSIDE, BOTTOM_INSIDE
    }

    public XAxis() {
        super();

        mYOffset = Utils.convertDpToPixel(4.f); // -3
    }

    /**
     * returns the position of the xPx-labels
     */
    public XAxisPosition getPosition() {
        return mPosition;
    }

    /**
     * sets the position of the xPx-labels
     *
     * @param pos
     */
    public void setPosition(XAxisPosition pos) {
        mPosition = pos;
    }

    /**
     * returns the angle for drawing the X axis labels (in degrees)
     */
    public float getLabelRotationAngle() {
        return mLabelRotationAngle;
    }

    /**
     * sets the angle for drawing the X axis labels (in degrees)
     *
     * @param angle the angle in degrees
     */
    public void setLabelRotationAngle(float angle) {
        mLabelRotationAngle = angle;
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

//    /**
//     * Sets the labels for this axis.
//     *
//     * @param values
//     */
//    public void setValues(List<XAxisValue> values) {
//        mValues = values;
//    }
//
//    /**
//     * Returns the labels for this axis.
//     *
//     * @return
//     */
//    public List<XAxisValue> getValues() {
//        return mValues;
//    }
//
//    /**
//     * Adds a new xPx-yValue to the chart data.
//     *
//     * @param xVal
//     */
//    public void addXValue(XAxisValue xVal) {
//        mValues.add(xVal);
//    }
//
//    /**
//     * Removes the xPx-yValue at the specified index.
//     *
//     * @param index
//     */
//    public void removeXValue(int index) {
//        mValues.remove(index);
//    }
}
