
package com.github.mikephil.charting.components;

import com.github.mikephil.charting.formatter.DefaultXAxisValueFormatter;
import com.github.mikephil.charting.formatter.XAxisValueFormatter;
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
     * the space that should be left out (in characters) between the xPx-axis
     * labels
     */
    private int mSpaceBetweenLabels = 4;

    /**
     * the modulus that indicates if a yValue at a specified index in an
     * array(list) for the xPx-axis-labels is drawn or not. If index % modulus ==
     * 0 DRAW, else dont draw.
     */
    public int mAxisLabelModulus = 1;

    /**
     * Is axisLabelModulus a custom yValue or auto calculated? If false, then
     * it's auto, if true, then custom. default: false (automatic modulus)
     */
    private boolean mIsAxisModulusCustom = false;
    /**
     * if set to true, the chart will avoid that the first and last label entry
     * in the chart "clip" off the edge of the chart
     */
    private boolean mAvoidFirstLastClipping = false;

    /**
     * Custom formatter for adjusting xPx-yValue strings
     */
    protected XAxisValueFormatter mXAxisValueFormatter;

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
     * Sets the number of labels that should be skipped on the axis before the
     * next label is drawn. This will disable the feature that automatically
     * calculates an adequate space between the axis labels and set the number
     * of labels to be skipped to the fixed number provided by this method. Call
     * resetLabelsToSkip(...) to re-enable automatic calculation.
     *
     * @param count
     */
    public void setLabelsToSkip(int count) {

        if (count < 0)
            count = 0;

        mIsAxisModulusCustom = true;
        mAxisLabelModulus = count + 1;
    }

    /**
     * Calling this will disable a custom number of labels to be skipped (set by
     * setLabelsToSkip(...)) while drawing the xPx-axis. Instead, the number of
     * values to skip will again be calculated automatically.
     */
    public void resetLabelsToSkip() {
        mIsAxisModulusCustom = false;
    }

    /**
     * Returns true if a custom axis-modulus has been set that determines the
     * number of labels to skip when drawing.
     *
     * @return
     */
    public boolean isAxisModulusCustom() {
        return mIsAxisModulusCustom;
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


    /**
     * Sets a custom XAxisValueFormatter for the data object that allows custom-formatting
     * of all xPx-values before rendering them. Provide null to reset back to the
     * default formatting.
     *
     * @param formatter
     */
    public void setValueFormatter(XAxisValueFormatter formatter) {
        if (formatter == null)
            mXAxisValueFormatter = new DefaultXAxisValueFormatter(mDecimals);
        else
            mXAxisValueFormatter = formatter;
    }

    /**
     * Returns the custom XAxisValueFormatter that is set for this data object.
     *
     * @return
     */
    public XAxisValueFormatter getValueFormatter() {

        if (mXAxisValueFormatter == null)
            mXAxisValueFormatter = new DefaultXAxisValueFormatter(mDecimals);

        return mXAxisValueFormatter;
    }

    @Override
    public String getLongestLabel() {

        String longest = "";

//        for (int i = 0; i < mValues.size(); i++) {
//            String text = mValues.get(i).getLabel();
//
//            if (longest.length() < text.length())
//                longest = text;
//        }

        return longest;
    }
}
