package com.github.mikephil.charting.components;


/**
 * The limit zone is an additional feature for all Line-, Bar- and
 * ScatterCharts. It allows the displaying of an additional zone in the chart
 * that marks a certain maximum-minimum / limit.
 *
 * @author Mesrop Davoyan
 */
public class LimitZone {
    /**
     * maximum position
     */
    private float mMaxValue;

    /**
     * minimum position
     */
    private float mMinValue;

    /**
     * the background of the limit zone
     */
    private int mBgColor;

    /**
     * Default constructor
     */
    public LimitZone() {

    }

    /**
     * Constructor with maximum, minimum ang background color values
     *
     * @param maxValue max value
     * @param minValue min value
     * @param bgColor  background color
     */
    public LimitZone(float maxValue, float minValue, int bgColor) {
        mMaxValue = maxValue;
        mMinValue = minValue;
        mBgColor = bgColor;
    }

    /**
     * Returns the max value
     *
     * @return
     */
    public float getMaxValue() {
        return mMaxValue;
    }

    /**
     * Sets the max value
     *
     * @param maxValue
     */
    public void setMaxValue(float maxValue) {
        this.mMaxValue = maxValue;
    }

    /**
     * Returns the min value
     *
     * @return
     */
    public float getMinValue() {
        return mMinValue;
    }

    /**
     * Sets the min value
     *
     * @param minValue
     */
    public void setMinValue(float minValue) {
        this.mMinValue = minValue;
    }

    /**
     * Returns the background color
     *
     * @return
     */
    public int getBgColor() {
        return mBgColor;
    }

    /**
     * Sets the background color
     *
     * @param bgColor
     */
    public void setBgColor(int bgColor) {
        this.mBgColor = bgColor;
    }
}