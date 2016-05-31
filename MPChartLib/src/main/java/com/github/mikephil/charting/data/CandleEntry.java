
package com.github.mikephil.charting.data;

import android.annotation.SuppressLint;

/**
 * Subclass of Entry that holds all values for one entry in a CandleStickChart.
 * 
 * @author Philipp Jahoda
 */
@SuppressLint("ParcelCreator")
public class CandleEntry extends Entry {

    /** shadow-high yValue */
    private float mShadowHigh = 0f;

    /** shadow-low yValue */
    private float mShadowLow = 0f;

    /** close yValue */
    private float mClose = 0f;

    /** open yValue */
    private float mOpen = 0f;

    /**
     * Constructor.
     * 
     * @param x The yValue on the xPx-axis.
     * @param shadowH The (shadow) high yValue.
     * @param shadowL The (shadow) low yValue.
     * @param open The open yValue.
     * @param close The close yValue.
     */
    public CandleEntry(float x, float shadowH, float shadowL, float open, float close) {
        super(x, (shadowH + shadowL) / 2f);

        this.mShadowHigh = shadowH;
        this.mShadowLow = shadowL;
        this.mOpen = open;
        this.mClose = close;
    }

    /**
     * Constructor.
     * 
     * @param x The yValue on the xPx-axis.
     * @param shadowH The (shadow) high yValue.
     * @param shadowL The (shadow) low yValue.
     * @param open
     * @param close
     * @param data Spot for additional data this Entry represents.
     */
    public CandleEntry(float x, float shadowH, float shadowL, float open, float close,
            Object data) {
        super(x, (shadowH + shadowL) / 2f, data);

        this.mShadowHigh = shadowH;
        this.mShadowLow = shadowL;
        this.mOpen = open;
        this.mClose = close;
    }

    /**
     * Returns the overall range (difference) between shadow-high and
     * shadow-low.
     * 
     * @return
     */
    public float getShadowRange() {
        return Math.abs(mShadowHigh - mShadowLow);
    }

    /**
     * Returns the body size (difference between open and close).
     * 
     * @return
     */
    public float getBodyRange() {
        return Math.abs(mOpen - mClose);
    }

    /**
     * Returns the center yValue of the candle. (Middle yValue between high and
     * low)
     */
    @Override
    public float getY() {
        return super.getY();
    }

    public CandleEntry copy() {

        CandleEntry c = new CandleEntry(getX(), mShadowHigh, mShadowLow, mOpen,
                mClose, getData());

        return c;
    }

    /**
     * Returns the upper shadows highest yValue.
     * 
     * @return
     */
    public float getHigh() {
        return mShadowHigh;
    }

    public void setHigh(float mShadowHigh) {
        this.mShadowHigh = mShadowHigh;
    }

    /**
     * Returns the lower shadows lowest yValue.
     * 
     * @return
     */
    public float getLow() {
        return mShadowLow;
    }

    public void setLow(float mShadowLow) {
        this.mShadowLow = mShadowLow;
    }

    /**
     * Returns the bodys close yValue.
     * 
     * @return
     */
    public float getClose() {
        return mClose;
    }

    public void setClose(float mClose) {
        this.mClose = mClose;
    }

    /**
     * Returns the bodys open yValue.
     * 
     * @return
     */
    public float getOpen() {
        return mOpen;
    }

    public void setOpen(float mOpen) {
        this.mOpen = mOpen;
    }
}
