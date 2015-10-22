package com.xxmassdeveloper.mpchartexample.custom;


import io.realm.RealmObject;

public class RealmDemoData extends RealmObject {

    private float value;
    private int xIndex;

    // ofc there could me more fields here...

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
}