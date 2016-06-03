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

    private String someStringField;

    /**
     * label for pie entries
     */
    private String label;

    // ofc there could me more fields here...

    public RealmDemoData() {

    }

    public RealmDemoData(float yValue) {
        this.yValue = yValue;
    }

    public RealmDemoData(float xValue, float yValue) {
        this.xValue = xValue;
        this.yValue = yValue;
    }

    /**
     * Constructor for stacked bars.
     *
     * @param xValue
     * @param stackValues
     */
    public RealmDemoData(float xValue, float[] stackValues) {
        this.xValue = xValue;
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
     */
    public RealmDemoData(float xValue, float high, float low, float open, float close) {
        this.yValue = (high + low) / 2f;
        this.high = high;
        this.low = low;
        this.open = open;
        this.close = close;
        this.xValue = xValue;
    }

    /**
     * Constructor for bubbles.
     *
     * @param xValue
     * @param yValue
     * @param bubbleSize
     */
    public RealmDemoData(float xValue, float yValue, float bubbleSize) {
        this.xValue = xValue;
        this.yValue = yValue;
        this.bubbleSize = bubbleSize;
    }

    /**
     * Constructor for pie chart.
     *
     * @param yValue
     * @param label
     */
    public RealmDemoData(float yValue, String label) {
        this.yValue = yValue;
        this.label = label;
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

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}