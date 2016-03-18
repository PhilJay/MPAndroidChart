package com.github.mikephil.charting.components;

import android.graphics.Color;
import android.graphics.Paint;

import com.github.mikephil.charting.formatter.DefaultValueFormatter;
import com.github.mikephil.charting.formatter.DefaultYAxisValueFormatter;
import com.github.mikephil.charting.formatter.YAxisValueFormatter;
import com.github.mikephil.charting.utils.Utils;

/**
 * Class representing the y-axis labels settings and its entries. Only use the setter methods to modify it. Do not
 * access public variables directly. Be aware that not all features the YLabels class provides are suitable for the
 * RadarChart. Customizations that affect the value range of the axis need to be applied before setting data for the
 * chart.
 *
 * @author Philipp Jahoda
 */
public class YAxis extends AxisBase {

    /**
     * custom formatter that is used instead of the auto-formatter if set
     */
    protected YAxisValueFormatter mYAxisValueFormatter;

    /**
     * the actual array of entries
     */
    public float[] mEntries = new float[]{};

    /**
     * the number of entries the legend contains
     */
    public int mEntryCount;

    /**
     * the number of decimal digits to use
     */
    public int mDecimals;

    /**
     * the number of y-label entries the y-labels should have, default 6
     */
    private int mLabelCount = 6;

    /**
     * indicates if the top y-label entry is drawn or not
     */
    private boolean mDrawTopYLabelEntry = true;

    /**
     * if true, the y-labels show only the minimum and maximum value
     */
    protected boolean mShowOnlyMinMax = false;

    /**
     * flag that indicates if the axis is inverted or not
     */
    protected boolean mInverted = false;

    /**
     * if true, the set number of y-labels will be forced
     */
    protected boolean mForceLabels = false;

    /**
     * flag that indicates if the zero-line should be drawn regardless of other grid lines
     */
    protected boolean mDrawZeroLine = false;

    /**
     * Color of the zero line
     */
    protected int mZeroLineColor = Color.GRAY;

    /**
     * Width of the zero line in pixels
     */
    protected float mZeroLineWidth = 1f;

    /**
     * custom minimum value this axis represents
     */
    protected float mCustomAxisMin = Float.NaN;

    /**
     * custom maximum value this axis represents
     */
    protected float mCustomAxisMax = Float.NaN;

    /**
     * axis space from the largest value to the top in percent of the total axis range
     */
    protected float mSpacePercentTop = 10f;

    /**
     * axis space from the smallest value to the bottom in percent of the total axis range
     */
    protected float mSpacePercentBottom = 10f;

    public float mAxisMaximum = 0f;
    public float mAxisMinimum = 0f;

    /**
     * the total range of values this axis covers
     */
    public float mAxisRange = 0f;

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
     *
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
     * When true, axis labels are controlled by the `granularity` property.
     * When false, axis values could possibly be repeated.
     * This could happen if two adjacent axis values are rounded to same value.
     * If using granularity this could be avoided by having fewer axis values visible.
     */
    protected boolean mGranularityEnabled = true;

    /**
     * the minimum interval between axis values
     */
    protected float mGranularity = 1.0f;

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
     * @param maxWidth
     */
    public void setMaxWidth(float maxWidth) {
        mMaxWidth = maxWidth;
    }

    /**
     * @return true if granularity is enabled
     */
    public boolean isGranularityEnabled() {
        return mGranularityEnabled;
    }

    /**
     * Enabled/disable granularity control on axis value intervals
     * @param enabled
     */
    public void setGranularityEnabled(boolean enabled) {
        mGranularityEnabled = true;
    }

    /**
     * @return the minimum interval between axis values
     */
    public float getGranularity() {
        return mGranularity;
    }

    /**
     * set the minimum interval between axis values
     * @param granularity
     */
    public void setGranularity(float granularity) {
        mGranularity = granularity;
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
     * set this to true to enable drawing the top y-label entry. Disabling this can be helpful when the top y-label and
     * left x-label interfere with each other. default: true
     *
     * @param enabled
     */
    public void setDrawTopYLabelEntry(boolean enabled) {
        mDrawTopYLabelEntry = enabled;
    }

    /**
     * sets the number of label entries for the y-axis max = 25, min = 2, default: 6, be aware that this number is not
     * fixed (if force == false) and can only be approximated.
     *
     * @param count the number of y-axis labels that sould be displayed
     * @param force if enabled, the set label count will be forced, meaning that the exact specified count of labels will
     *              be drawn and evenly distributed alongside the axis - this might cause labels to have uneven values
     */
    public void setLabelCount(int count, boolean force) {

        if (count > 25)
            count = 25;
        if (count < 2)
            count = 2;

        mLabelCount = count;
        mForceLabels = force;
    }

    /**
     * Returns the number of label entries the y-axis should have
     *
     * @return
     */
    public int getLabelCount() {
        return mLabelCount;
    }

    /**
     * Returns true if focing the y-label count is enabled. Default: false
     *
     * @return
     */
    public boolean isForceLabelsEnabled() {
        return mForceLabels;
    }

    /**
     * If enabled, the YLabels will only show the minimum and maximum value of the chart. This will ignore/override the
     * set label count.
     *
     * @param enabled
     */
    public void setShowOnlyMinMax(boolean enabled) {
        mShowOnlyMinMax = enabled;
    }

    /**
     * Returns true if showing only min and max value is enabled.
     *
     * @return
     */
    public boolean isShowOnlyMinMaxEnabled() {
        return mShowOnlyMinMax;
    }

    /**
     * If this is set to true, the y-axis is inverted which means that low values are on top of the chart, high values
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
     * Use setAxisMinValue(...) / setAxisMaxValue(...) instead.
     *
     * @param startAtZero
     */
    @Deprecated
    public void setStartAtZero(boolean startAtZero) {
        if (startAtZero)
            setAxisMinValue(0f);
        else
            resetAxisMinValue();
    }

    public float getAxisMinValue() {
        return mCustomAxisMin;
    }

    /**
     * Set a custom minimum value for this axis. If set, this value will not be calculated automatically depending on
     * the provided data. Use resetAxisMinValue() to undo this. Do not forget to call setStartAtZero(false) if you use
     * this method. Otherwise, the axis-minimum value will still be forced to 0.
     *
     * @param min
     */
    public void setAxisMinValue(float min) {
        mCustomAxisMin = min;
    }

    /**
     * By calling this method, any custom minimum value that has been previously set is reseted, and the calculation is
     * done automatically.
     */
    public void resetAxisMinValue() {
        mCustomAxisMin = Float.NaN;
    }

    public float getAxisMaxValue() {
        return mCustomAxisMax;
    }

    /**
     * Set a custom maximum value for this axis. If set, this value will not be calculated automatically depending on
     * the provided data. Use resetAxisMaxValue() to undo this.
     *
     * @param max
     */
    public void setAxisMaxValue(float max) {
        mCustomAxisMax = max;
    }

    /**
     * By calling this method, any custom maximum value that has been previously set is reseted, and the calculation is
     * done automatically.
     */
    public void resetAxisMaxValue() {
        mCustomAxisMax = Float.NaN;
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

    @Override
    public String getLongestLabel() {

        String longest = "";

        for (int i = 0; i < mEntries.length; i++) {
            String text = getFormattedLabel(i);

            if (longest.length() < text.length())
                longest = text;
        }

        return longest;
    }

    /**
     * Returns the formatted y-label at the specified index. This will either use the auto-formatter or the custom
     * formatter (if one is set).
     *
     * @param index
     * @return
     */
    public String getFormattedLabel(int index) {

        if (index < 0 || index >= mEntries.length)
            return "";
        else
            return getValueFormatter().getFormattedValue(mEntries[index], this);
    }

    /**
     * Sets the formatter to be used for formatting the axis labels. If no formatter is set, the chart will
     * automatically determine a reasonable formatting (concerning decimals) for all the values that are drawn inside
     * the chart. Use chart.getDefaultValueFormatter() to use the formatter calculated by the chart.
     *
     * @param f
     */
    public void setValueFormatter(YAxisValueFormatter f) {

        if (f == null)
            mYAxisValueFormatter = new DefaultYAxisValueFormatter(mDecimals);
        else
            mYAxisValueFormatter = f;
    }

    /**
     * Returns the formatter used for formatting the axis labels.
     *
     * @return
     */
    public YAxisValueFormatter getValueFormatter() {

        if (mYAxisValueFormatter == null)
            mYAxisValueFormatter = new DefaultYAxisValueFormatter(mDecimals);

        return mYAxisValueFormatter;
    }

    /**
     * If this component has no YAxisValueFormatter or is only equipped with the default one (no custom set), return true.
     *
     * @return
     */
    public boolean needsDefaultFormatter() {
        if (mYAxisValueFormatter == null)
            return true;
        if (mYAxisValueFormatter instanceof DefaultValueFormatter)
            return true;

        return false;
    }

    /**
     * Returns true if this axis needs horizontal offset, false if no offset is needed.
     *
     * @return
     */
    public boolean needsOffset() {
        if (isEnabled() && isDrawLabelsEnabled() && getLabelPosition() == YAxisLabelPosition.OUTSIDE_CHART)
            return true;
        else
            return false;
    }
}
