
package com.github.mikephil.charting.components;

import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ValueFormatter;

import java.util.ArrayList;

/**
 * Class representing the y-axis labels settings and its entries. Only use the
 * setter methods to modify it. Do not access public variables directly. Be
 * aware that not all features the YLabels class provides are suitable for the
 * RadarChart.
 * 
 * @author Philipp Jahoda
 */
public class YAxis extends AxisBase {

    /** the actual array of entries */
    public float[] mEntries = new float[] {};

    /** the number of entries the legend contains */
    public int mEntryCount;

    /** the number of decimal digits to use */
    public int mDecimals;

    /** the number of y-label entries the y-labels should have, default 6 */
    private int mLabelCount = 6;

    /** indicates if the top y-label entry is drawn or not */
    private boolean mDrawTopYLabelEntry = true;

    /** if true, thousands ylabel values are separated by a dot */
    protected boolean mSeparateTousands = true;

    /** if true, the y-labels show only the minimum and maximum value */
    protected boolean mShowOnlyMinMax = false;

    /** flag that indicates if this axis is enabled or not */
    protected boolean mEnabled = true;

    /** flag that indicates if the axis is inverted or not */
    protected boolean mInverted = false;

    /** if true, the y-label entries will always start at zero */
    protected boolean mStartAtZero = true;

    /** the formatter used to customly format the y-labels */
    private ValueFormatter mFormatter = null;

    private ArrayList<LimitLine> mLimitLines;

    protected float mCustomAxisMin = Float.NaN;

    protected float mCustomAxisMax = Float.NaN;
    
    public float mAxisMaximum = 0f;
    public float mAxisMinimum = 0f;

    /** the position of the y-labels relative to the chart */
    private YLabelPosition mPosition = YLabelPosition.OUTSIDE_CHART;

    /** enum for the position of the y-labels relative to the chart */
    public enum YLabelPosition {
        OUTSIDE_CHART, INSIDE_CHART
    }

    private AxisDependency mAxisDependency;

    public enum AxisDependency {
        LEFT, RIGHT
    }

    public YAxis(AxisDependency position) {
        this.mAxisDependency = position;
        this.mLimitLines = new ArrayList<LimitLine>();
    }

    public AxisDependency getAxisDependency() {
        return mAxisDependency;
    }

    /**
     * returns the position of the y-labels
     */
    public YLabelPosition getLabelPosition() {
        return mPosition;
    }

    /**
     * sets the position of the y-labels
     * 
     * @param pos
     */
    public void setPosition(YLabelPosition pos) {
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
     * set this to true to enable drawing the top y-label entry. Disabling this
     * can be helpful when the top y-label and left x-label interfere with each
     * other. default: true
     * 
     * @param enabled
     */
    public void setDrawTopYLabelEntry(boolean enabled) {
        mDrawTopYLabelEntry = enabled;
    }

    /**
     * sets the number of label entries for the y-axis max = 15, min = 2,
     * default: 6, be aware that this number is not fixed and can only be
     * approximated
     * 
     * @param yCount
     */
    public void setLabelCount(int yCount) {

        if (yCount > 15)
            yCount = 15;
        if (yCount < 2)
            yCount = 2;

        mLabelCount = yCount;
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
     * Set this to true to enable values above 1000 to be separated by a dot.
     * 
     * @param enabled
     */
    public void setSeparateThousands(boolean enabled) {
        mSeparateTousands = enabled;
    }

    /**
     * Returns true if separating thousands is enabled, false if not.
     * 
     * @return
     */
    public boolean isSeparateThousandsEnabled() {
        return mSeparateTousands;
    }

    /**
     * Returns the custom formatter used to format the YLabels.
     * 
     * @return
     */
    public ValueFormatter getFormatter() {
        return mFormatter;
    }

    /**
     * Sets a custom formatter that will be used to format the YLabels.
     * 
     * @param f
     */
    public void setFormatter(ValueFormatter f) {
        this.mFormatter = f;
    }

    /**
     * If enabled, the YLabels will only show the minimum and maximum value of
     * the chart. This will ignore/override the set label count.
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
     * If this is set to true, the y-axis is inverted which means that low
     * values are on top of the chart, high values on bottom.
     * 
     * @param enabled
     */
    public void setInvertAxis(boolean enabled) {
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
     * enable this to force the y-axis labels to always start at zero
     * 
     * @param enabled
     */
    public void setStartAtZero(boolean enabled) {
        this.mStartAtZero = enabled;
    }

    /**
     * returns true if the chart is set to start at zero, false otherwise
     * 
     * @return
     */
    public boolean isStartAtZeroEnabled() {
        return mStartAtZero;
    }

    /**
     * Adds a new LimitLine to this axis.
     * 
     * @param l
     */
    public void addLimitLine(LimitLine l) {
        mLimitLines.add(l);
    }

    /**
     * Removes the specified LimitLine from the axis.
     * 
     * @param l
     */
    public void removeLimitLine(LimitLine l) {
        mLimitLines.remove(l);
    }

    /**
     * Removes all LimitLines from the axis.
     */
    public void removeAllLimitLines() {
        mLimitLines = new ArrayList<LimitLine>();
    }

    /**
     * Returns the LimitLines of this axis.
     * 
     * @return
     */
    public ArrayList<LimitLine> getLimitLines() {
        return mLimitLines;
    }

    public float getAxisMinValue() {
        return mCustomAxisMin;
    }

    public void setAxisMinValue(float min) {
        mCustomAxisMin = min;
    }

    public void resetAxisMinValue() {
        mCustomAxisMin = Float.NaN;
    }

    public float getAxisMaxValue() {
        return mCustomAxisMax;
    }

    public void setAxisMaxValue(float max) {
        mCustomAxisMax = max;
    }

    public void resetAxisMaxValue() {
        mCustomAxisMax = Float.NaN;
    }

    /**
     * Returns the longest formatted label (in terms of characters) the y-labels
     * contain.
     * 
     * @return
     */
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
     * Returns the formatted y-label at the specified index. This will either
     * use the auto-formatter or the custom formatter (if one is set).
     * 
     * @param index
     * @return
     */
    public String getFormattedLabel(int index) {

        if (index < 0)
            return "";

        String text = null;

        // if there is no formatter
        if (getFormatter() == null)
            text = Utils.formatNumber(mEntries[index], mDecimals,
                    isSeparateThousandsEnabled());
        else
            text = getFormatter().getFormattedValue(mEntries[index]);

        return text;
    }
}
