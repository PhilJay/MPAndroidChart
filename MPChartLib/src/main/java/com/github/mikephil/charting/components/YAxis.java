package com.github.mikephil.charting.components;

import android.graphics.Color;
import android.graphics.Paint;

import com.github.mikephil.charting.utils.Utils;

/**
 * Class representing the y-axis labels settings and its entries. Only use the setter methods to
 * modify it. Do not
 * access public variables directly. Be aware that not all features the YLabels class provides
 * are suitable for the
 * RadarChart. Customizations that affect the value range of the axis need to be applied before
 * setting data for the
 * chart.
 *
 * @author Philipp Jahoda
 */
public class YAxis extends AxisBase {

    /**
     * indicates if the bottom y-label entry is drawn or not
     */
    private boolean mDrawBottomYLabelEntry = true;

    /**
     * indicates if the top y-label entry is drawn or not
     */
    private boolean mDrawTopYLabelEntry = true;

    /**
     * flag that indicates if the axis is inverted or not
     */
    protected boolean mInverted = false;

    /**
     * flag that indicates if the zero-line should be drawn regardless of other grid lines
     */
    protected boolean mDrawZeroLine = false;

    /**
     * flag indicating that auto scale min restriction should be used
     */
    private boolean mUseAutoScaleRestrictionMin = false;

    /**
     * flag indicating that auto scale max restriction should be used
     */
    private boolean mUseAutoScaleRestrictionMax = false;

    /**
     * Color of the zero line
     */
    protected int mZeroLineColor = Color.GRAY;

    /**
     * Width of the zero line in pixels
     */
    protected float mZeroLineWidth = 1f;

    /**
     * axis space from the largest value to the top in percent of the total axis range
     */
    protected float mSpacePercentTop = 10f;

    /**
     * axis space from the smallest value to the bottom in percent of the total axis range
     */
    protected float mSpacePercentBottom = 10f;

    /**
     * the position of the y-labels relative to the chart
     */
    private YAxisLabelPosition mPosition = YAxisLabelPosition.OUTSIDE_CHART;

    /**
     * enum for the position of the y-labels relative to the chart
     */
    public enum YAxisLabelPosition {
        OUTSIDE_CHART, INSIDE_CHART
    }

    /**
     * the side this axis object represents
     */
    private AxisDependency mAxisDependency;

    /**
     * the minimum width that the axis should take (in dp).
     * <p/>
     * default: 0.0
     */
    protected float mMinWidth = 0.f;

    /**
     * the maximum width that the axis can take (in dp).
     * use Inifinity for disabling the maximum
     * default: Float.POSITIVE_INFINITY (no maximum specified)
     */
    protected float mMaxWidth = Float.POSITIVE_INFINITY;

    /**
     * Enum that specifies the axis a DataSet should be plotted against, either LEFT or RIGHT.
     *
     * @author Philipp Jahoda
     */
    public enum AxisDependency {
        LEFT, RIGHT
    }

    public YAxis() {
        super();

        // default left
        this.mAxisDependency = AxisDependency.LEFT;
        this.mYOffset = 0f;
    }

    public YAxis(AxisDependency position) {
        super();
        this.mAxisDependency = position;
        this.mYOffset = 0f;
    }

    public AxisDependency getAxisDependency() {
        return mAxisDependency;
    }

    /**
     * @return the minimum width that the axis should take (in dp).
     */
    public float getMinWidth() {
        return mMinWidth;
    }

    /**
     * Sets the minimum width that the axis should take (in dp).
     *
     * @param minWidth
     */
    public void setMinWidth(float minWidth) {
        mMinWidth = minWidth;
    }

    /**
     * @return the maximum width that the axis can take (in dp).
     */
    public float getMaxWidth() {
        return mMaxWidth;
    }

    /**
     * Sets the maximum width that the axis can take (in dp).
     *
     * @param maxWidth
     */
    public void setMaxWidth(float maxWidth) {
        mMaxWidth = maxWidth;
    }

    /**
     * returns the position of the y-labels
     */
    public YAxisLabelPosition getLabelPosition() {
        return mPosition;
    }

    /**
     * sets the position of the y-labels
     *
     * @param pos
     */
    public void setPosition(YAxisLabelPosition pos) {
        mPosition = pos;
    }

    /**
     * returns true if drawing the top y-axis label entry is enabled
     *
     * @return
     */
    public boolean isDrawTopYLabelEntryEnabled() {
        return mDrawTopYLabelEntry;
    }

    /**
     * returns true if drawing the bottom y-axis label entry is enabled
     *
     * @return
     */
    public boolean isDrawBottomYLabelEntryEnabled() {
        return mDrawBottomYLabelEntry;
    }

    /**
     * set this to true to enable drawing the top y-label entry. Disabling this can be helpful
     * when the top y-label and
     * left x-label interfere with each other. default: true
     *
     * @param enabled
     */
    public void setDrawTopYLabelEntry(boolean enabled) {
        mDrawTopYLabelEntry = enabled;
    }

    /**
     * If this is set to true, the y-axis is inverted which means that low values are on top of
     * the chart, high values
     * on bottom.
     *
     * @param enabled
     */
    public void setInverted(boolean enabled) {
        mInverted = enabled;
    }

    /**
     * If this returns true, the y-axis is inverted.
     *
     * @return
     */
    public boolean isInverted() {
        return mInverted;
    }

    /**
     * This method is deprecated.
     * Use setAxisMinimum(...) / setAxisMaximum(...) instead.
     *
     * @param startAtZero
     */
    @Deprecated
    public void setStartAtZero(boolean startAtZero) {
        if (startAtZero)
            setAxisMinimum(0f);
        else
            resetAxisMinimum();
    }

    /**
     * Sets the top axis space in percent of the full range. Default 10f
     *
     * @param percent
     */
    public void setSpaceTop(float percent) {
        mSpacePercentTop = percent;
    }

    /**
     * Returns the top axis space in percent of the full range. Default 10f
     *
     * @return
     */
    public float getSpaceTop() {
        return mSpacePercentTop;
    }

    /**
     * Sets the bottom axis space in percent of the full range. Default 10f
     *
     * @param percent
     */
    public void setSpaceBottom(float percent) {
        mSpacePercentBottom = percent;
    }

    /**
     * Returns the bottom axis space in percent of the full range. Default 10f
     *
     * @return
     */
    public float getSpaceBottom() {
        return mSpacePercentBottom;
    }

    public boolean isDrawZeroLineEnabled() {
        return mDrawZeroLine;
    }

    /**
     * Set this to true to draw the zero-line regardless of weather other
     * grid-lines are enabled or not. Default: false
     *
     * @param mDrawZeroLine
     */
    public void setDrawZeroLine(boolean mDrawZeroLine) {
        this.mDrawZeroLine = mDrawZeroLine;
    }

    public int getZeroLineColor() {
        return mZeroLineColor;
    }

    /**
     * Sets the color of the zero line
     *
     * @param color
     */
    public void setZeroLineColor(int color) {
        mZeroLineColor = color;
    }

    public float getZeroLineWidth() {
        return mZeroLineWidth;
    }

    /**
     * Sets the width of the zero line in dp
     *
     * @param width
     */
    public void setZeroLineWidth(float width) {
        this.mZeroLineWidth = Utils.convertDpToPixel(width);
    }

    /**
     * This is for normal (not horizontal) charts horizontal spacing.
     *
     * @param p
     * @return
     */
    public float getRequiredWidthSpace(Paint p) {

        p.setTextSize(mTextSize);

        String label = getLongestLabel();
        float width = (float) Utils.calcTextWidth(p, label) + getXOffset() * 2f;

        float minWidth = getMinWidth();
        float maxWidth = getMaxWidth();

        if (minWidth > 0.f)
            minWidth = Utils.convertDpToPixel(minWidth);

        if (maxWidth > 0.f && maxWidth != Float.POSITIVE_INFINITY)
            maxWidth = Utils.convertDpToPixel(maxWidth);

        width = Math.max(minWidth, Math.min(width, maxWidth > 0.0 ? maxWidth : width));

        return width;
    }

    /**
     * This is for HorizontalBarChart vertical spacing.
     *
     * @param p
     * @return
     */
    public float getRequiredHeightSpace(Paint p) {

        p.setTextSize(mTextSize);

        String label = getLongestLabel();
        return (float) Utils.calcTextHeight(p, label) + getYOffset() * 2f;
    }

    /**
     * Returns true if this axis needs horizontal offset, false if no offset is needed.
     *
     * @return
     */
    public boolean needsOffset() {
        if (isEnabled() && isDrawLabelsEnabled() && getLabelPosition() == YAxisLabelPosition
                .OUTSIDE_CHART)
            return true;
        else
            return false;
    }

    /**
     * Returns true if autoscale restriction for axis min value is enabled
     */
    @Deprecated
    public boolean isUseAutoScaleMinRestriction( ) {
        return mUseAutoScaleRestrictionMin;
    }

    /**
     * Sets autoscale restriction for axis min value as enabled/disabled
     */
    @Deprecated
    public void setUseAutoScaleMinRestriction( boolean isEnabled ) {
        mUseAutoScaleRestrictionMin = isEnabled;
    }

    /**
     * Returns true if autoscale restriction for axis max value is enabled
     */
    @Deprecated
    public boolean isUseAutoScaleMaxRestriction() {
        return mUseAutoScaleRestrictionMax;
    }

    /**
     * Sets autoscale restriction for axis max value as enabled/disabled
     */
    @Deprecated
    public void setUseAutoScaleMaxRestriction( boolean isEnabled ) {
        mUseAutoScaleRestrictionMax = isEnabled;
    }


    @Override
    public void calculate(float dataMin, float dataMax) {

        float min = dataMin;
        float max = dataMax;

        float range = Math.abs(max - min);

        // in case all values are equal
        if (range == 0f) {
            max = max + 1f;
            min = min - 1f;
        }

        // recalculate
        range = Math.abs(max - min);

        // calc extra spacing
        this.mAxisMinimum = mCustomAxisMin ? this.mAxisMinimum : min - (range / 100f) * getSpaceBottom();
        this.mAxisMaximum = mCustomAxisMax ? this.mAxisMaximum : max + (range / 100f) * getSpaceTop();

        this.mAxisRange = Math.abs(this.mAxisMinimum - this.mAxisMaximum);
    }
}
