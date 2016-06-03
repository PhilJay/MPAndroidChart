package com.xxmassdeveloper.mpchartexample.custom;


import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Demo class that encapsulates data stored in realm.io database.
 * This class represents data suitable for all chart-types.
 */
public class RealmDemoData extends RealmObject {

    private float yValue;
    private float xValue;

    private float open, close, high, low;

    private float bubbleSize;

    private RealmList<RealmFloat> stackValues;

    private String xAxisLabel;
    private double xAxisPosition;

    private String someStringField;

    // ofc there could me more fields here...

    public RealmDemoData() {

    }

    public RealmDemoData(float xValue, float yValue, double xAxisPosition, String xAxisLabel) {
        this.xValue = xValue;
        this.yValue = yValue;
        this.xAxisPosition = xAxisPosition;
        this.xAxisLabel = xAxisLabel;
    }

    /**
     * Constructor for stacked bars.
     *
     * @param xValue
     * @param stackValues
     * @param xAxisPosition
     * @param xAxisLabel
     */
    public RealmDemoData(float xValue, float[] stackValues, double xAxisPosition, String xAxisLabel) {
        this.xValue = xValue;
        this.xAxisPosition = xAxisPosition;
        this.xAxisLabel = xAxisLabel;
        this.stackValues = new RealmList<RealmFloat>();

        for (float val : stackValues) {
            this.stackValues.add(new RealmFloat(val));
        }
    }

    /**
     * Constructor for candles.
     *
     * @param xValue
     * @param high
     * @param low
     * @param open
     * @param close
     * @param xAxisPosition
     * @param xAxisLabel
     */
    public RealmDemoData(float xValue, float high, float low, float open, float close, double xAxisPosition, String
            xAxisLabel) {
        this.yValue = (high + low) / 2f;
        this.high = high;
        this.low = low;
        this.open = open;
        this.close = close;
        this.xValue = xValue;
        this.xAxisPosition = xAxisPosition;
        this.xAxisLabel = xAxisLabel;
    }

    /**
     * Constructor for bubbles.
     *
     * @param xValue
     * @param yValue
     * @param bubbleSize
     * @param xAxisPosition
     * @param xAxisLabel
     */
    public RealmDemoData(float xValue, float yValue, float bubbleSize, double xAxisPosition, String xAxisLabel) {
        this.xValue = xValue;
        this.yValue = yValue;
        this.bubbleSize = bubbleSize;
        this.xAxisPosition = xAxisPosition;
        this.xAxisLabel = xAxisLabel;
    }

    public float getyValue() {
        return yValue;
    }

    public void setyValue(float yValue) {
        this.yValue = yValue;
    }

    public float getxValue() {
        return xValue;
    }

    public void setxValue(float xValue) {
        this.xValue = xValue;
    }

    public RealmList<RealmFloat> getStackValues() {
        return stackValues;
    }

    public void setStackValues(RealmList<RealmFloat> stackValues) {
        this.stackValues = stackValues;
    }

    public float getOpen() {
        return open;
    }

    public void setOpen(float open) {
        this.open = open;
    }

    public float getClose() {
        return close;
    }

    public void setClose(float close) {
        this.close = close;
    }

    public float getHigh() {
        return high;
    }

    public void setHigh(float high) {
        this.high = high;
    }

    public float getLow() {
        return low;
    }

    public void setLow(float low) {
        this.low = low;
    }

    public float getBubbleSize() {
        return bubbleSize;
    }

    public void setBubbleSize(float bubbleSize) {
        this.bubbleSize = bubbleSize;
    }

    public String getSomeStringField() {
        return someStringField;
    }

    public void setSomeStringField(String someStringField) {
        this.someStringField = someStringField;
    }

    public double getxAxisPosition() {
        return xAxisPosition;
    }

    public void setxAxisPosition(double xAxisPosition) {
        this.xAxisPosition = xAxisPosition;
    }

    public String getxAxisLabel() {
        return xAxisLabel;
    }

    public void setxAxisLabel(String xAxisLabel) {
        this.xAxisLabel = xAxisLabel;
    }
}