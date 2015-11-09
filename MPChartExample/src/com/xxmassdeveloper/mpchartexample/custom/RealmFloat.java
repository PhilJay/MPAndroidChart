package com.xxmassdeveloper.mpchartexample.custom;

import io.realm.RealmObject;

/**
 * Created by Philipp Jahoda on 09/11/15.
 */
public class RealmFloat extends RealmObject {

    private float value;

    public RealmFloat() {

    }

    public RealmFloat(float value) {
        this.value = value;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }
}
