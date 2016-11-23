package com.github.mikephil.charting.components;

import android.graphics.Color;

/**
 * Created by admin on 2016-09-09.
 */
public class LimitRectangle extends ComponentBase {

    /** limit / maximum (the y-value or xIndex) */
    private float mLimitStart = 0f;
    private float mLimitEnd = 0f;

    /** the color of the limit rect */
    private int mRectColor = Color.rgb(237, 91, 91);
    private int mAlpha = 0;

    public LimitRectangle( float limitStart, float limitEnd, int color, int alpha ){
        mLimitStart= limitStart;
        mLimitEnd = limitEnd;
        mRectColor = color;
        mAlpha = alpha;
    }

    public float getLimitStart(){ return mLimitStart; }
    public float getLimitEnd() {return mLimitEnd;}
    public int getRectColor() {return mRectColor;}
    public int getAlpha() { return mAlpha; }
}
