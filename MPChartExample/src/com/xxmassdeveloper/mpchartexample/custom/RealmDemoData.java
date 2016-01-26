package com.xxmassdeveloper.mpchartexample.custom;


import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Demo class that encapsulates data stored in realm.io database.
 * This class represents data suitable for all chart-types.
 */
public class RealmDemoData extends RealmObject {

    private float value;

    private float open, close, high, low;

    private float bubbleSize;

    private RealmList<RealmFloat> stackValues;

    private int xIndex;

    private String xValue;

    private String someStringField;

    // ofc there could me more fields here...

    public RealmDemoData() {

    }

    public RealmDemoData(float value, int xIndex, String xValue) {
        this.value = value;
        this.xIndex = xIndex;
        this.xValue = xValue;
    }

    /**
     * Constructor for stacked bars.
     *
     * @param stackValues
     * @param xIndex
     * @param xValue
     */
    public RealmDemoData(float[] stackValues, int xIndex, String xValue) {
        this.xIndex = xIndex;
        this.xValue = xValue;
        this.stackValues = new RealmList<RealmFloat>();

        for (float val : stackValues) {
            this.stackValues.add(new RealmFloat(val));
        }
    }

    /**
     * Constructor for candles.
     *
     * @param high
     * @param low
     * @param open
     * @param close
     * @param xIndex
     */
    public RealmDemoData(float high, float low, float open, float close, int xIndex, String xValue) {
        this.value = (high + low) / 2f;
        this.high = high;
        this.low = low;
        this.open = open;
        this.close = close;
        this.xIndex = xIndex;
        this.xValue = xValue;
    }

    /**
     * Constructor for bubbles.
     *
     * @param value
     * @param xIndex
     * @param bubbleSize
     * @param xValue
     */
    public RealmDemoData(float value, int xIndex, float bubbleSize, String xValue) {
        this.value = value;
        this.xIndex = xIndex;
        this.bubbleSize = bubbleSize;
        this.xValue = xValue;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }

    public RealmList<RealmFloat> getStackValues() {
        return stackValues;
    }

    public void setStackValues(RealmList<RealmFloat> stackValues) {
        this.stackValues = stackValues;
    }

    public int getxIndex() {
        return xIndex;
    }

    public void setxIndex(int xIndex) {
        this.xIndex = xIndex;
    }

    public String getxValue() {
        return xValue;
    }

    public void setxValue(String xValue) {
        this.xValue = xValue;
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
}