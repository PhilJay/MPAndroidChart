package com.xxmassdeveloper.mpchartexample.custom;


import io.realm.RealmList;
import io.realm.RealmObject;

public class RealmDemoData extends RealmObject {

    private float value;
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
        this.stackValues = new RealmList<>();

        for(float val : stackValues) {
            this.stackValues.add(new RealmFloat(val));
        }
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

    public String getSomeStringField() {
        return someStringField;
    }

    public void setSomeStringField(String someStringField) {
        this.someStringField = someStringField;
    }
}