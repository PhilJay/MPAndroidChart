package com.xxmassdeveloper.mpchartexample.custom;


import io.realm.RealmObject;
import io.realm.annotations.Ignore;

public class RealmDemoData extends RealmObject {

    private float value;
    private int xIndex;

    private String someStringField;

    // ofc there could me more fields here...

    public RealmDemoData() {

    }

    public RealmDemoData(float value, int xIndex) {
        this.value = value;
        this.xIndex = xIndex;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }

    public int getxIndex() {
        return xIndex;
    }

    public void setxIndex(int xIndex) {
        this.xIndex = xIndex;
    }

    public String getSomeStringField() {
        return someStringField;
    }

    public void setSomeStringField(String someStringField) {
        this.someStringField = someStringField;
    }
}