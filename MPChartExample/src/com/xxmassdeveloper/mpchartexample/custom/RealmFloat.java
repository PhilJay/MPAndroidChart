package com.xxmassdeveloper.mpchartexample.custom;

import io.realm.RealmObject;

/**
 * Created by Philipp Jahoda on 09/11/15.
 */
@SuppressWarnings("unused")
public class RealmFloat extends RealmObject {

    private float floatValue;

    public RealmFloat() {

    }

    public RealmFloat(float floatValue) {
        this.floatValue = floatValue;
    }

    public float getFloatValue() {
        return floatValue;
    }

    public void setFloatValue(float value) {
        this.floatValue = value;
    }
}
