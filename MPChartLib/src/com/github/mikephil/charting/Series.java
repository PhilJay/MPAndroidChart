package com.github.mikephil.charting;

public abstract class Series {

    private float mVal = 0f;
    
    public Series(float val) {
        mVal = val;
    }
    
    public float getVal() {
        return mVal;
    }
}
