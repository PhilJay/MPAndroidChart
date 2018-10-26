package com.xxmassdeveloper.mpchartexample.custom;


import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Demo class that encapsulates data stored in realm.io database.
 * This class represents data suitable for all chart-types.
 */
@SuppressWarnings("unused")
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

    public RealmDemoData() {}

    public RealmDemoData(float xValue, float yValue) {
        this.xValue = xValue;
        this.yValue = yValue;
    }

    /**
     * Constructor for stacked bars.
     *
     * @param xValue      x position for bars
     * @param stackValues values of bars in the stack
     */
    public RealmDemoData(float xValue, float[] stackValues) {
        this.xValue = xValue;
        this.stackValues = new RealmList<>();

        for (float val : stackValues) {
            this.stackValues.add(new RealmFloat(val));
        }
    }

    /**
     * Constructor for candles.
     *
     * @param xValue x position of candle
     * @param high   high value for candle
     * @param low    low value for candle
     * @param open   open value for candle
     * @param close  close value for candle
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
     * @param xValue     x position of bubble
     * @param yValue     y position of bubble
     * @param bubbleSize size of bubble
     */
    public RealmDemoData(float xValue, float yValue, float bubbleSize) {
        this.xValue = xValue;
        this.yValue = yValue;
        this.bubbleSize = bubbleSize;
    }

    /**
     * Constructor for pie chart.
     *
     * @param yValue size of pie slice
     * @param label  label for pie slice
     */
    public RealmDemoData(float yValue, String label) {
        this.yValue = yValue;
        this.label = label;
    }

    public float getYValue() {
        return yValue;
    }

    public void setYValue(float yValue) {
        this.yValue = yValue;
    }

    public float getXValue() {
        return xValue;
    }

    public void setXValue(float xValue) {
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
