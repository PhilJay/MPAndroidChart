package com.github.mikephil.charting.components;

import android.graphics.Color;

/**
 * Created by admin on 2016-09-09.
 */
public class LimitRectangle extends ComponentBase {

    /** limit starting point (ie. minimum) */
    private float mLimitStart = 0f;
    /** limit end point (ie. maximum) */
    private float mLimitEnd = 0f;

    /** the color of the limit rect */
    private int mRectColor = Color.rgb(237, 91, 91);
    /** alpha value of the color, from 0 to 255 **/
    private int mAlpha = 110;

    /**
     * Constructor with limits.
     *
     * @param limitStart - limit starting point (ie. minimum)
     * @param limitEnd - limit end point (ie. maximum)
     */
    public LimitRectangle( float limitStart, float limitEnd ){
        mLimitStart= limitStart;
        mLimitEnd = limitEnd;
    }

    /**
     * Constructor with limits and colors
     *
     * @param limitStart - limit starting point (ie. minimum)
     * @param limitEnd - limit end point (ie. maximum)
     * @param color - fill color of the rectangle
     * @param alpha - alpha value for the color of the rectangle where 0 is transparent, 255 is opaque
     */
    public LimitRectangle( float limitStart, float limitEnd, int color, int alpha ){
        mLimitStart= limitStart;
        mLimitEnd = limitEnd;
        mRectColor = color;
        mAlpha = alpha;
    }

    /**
     * Sets the limit starting point (ie. minimum).
     *
     * @param limitStart
     */
    public void setLimitStart( float limitStart ){
        mLimitStart = limitStart;
    }
    /**
     * Returns the limit start (ie. minimum) that is set for this rectangle.
     *
     * @return
     */
     public float getLimitStart(){ return mLimitStart; }

    /**
     * Sets the limit end point (ie. maximum).
     *
     * @param limitEnd
     */
    public void setLimitEnd( float limitEnd){
        mLimitEnd = limitEnd;
    }
    /**
     * Returns the limit end (ie. maximum) that is set for this rectangle.
     *
     * @return
     */
     public float getLimitEnd() {return mLimitEnd;}

    /**
     * Sets the fill for this LimitRectangle. Make sure to use
     * getResources().getColor(...)
     *
     * @param color
     */
    public void setRectColor(int color) {
        mRectColor = color;
    }
    /**
     * Returns the fill color that is used for this LimitRectangle.
     *
     * @return
     */
    public int getRectColor() {return mRectColor;}

    /**
     * Sets the fill alpha for this LimitRectangle where 0 is transparent, 255 is opaque.
     *
     * @param alpha
     */
    public void setAlpha(int alpha) {
        mAlpha = alpha;
    }
    /**
     * Returns the fill color alpha that is used for this LimitRectangle.
     *
     * @return
     */
    public int getAlpha() { return mAlpha; }
}
