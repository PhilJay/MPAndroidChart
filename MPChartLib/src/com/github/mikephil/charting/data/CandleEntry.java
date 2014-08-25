
package com.github.mikephil.charting.data;

/**
 * Subclass of Entry that holds all values for one entry in a CandleStickChart.
 * 
 * @author Philipp Jahoda
 */
public class CandleEntry extends Entry {

    private float mShadowHigh = 0f;

    private float mShadowLow = 0f;

    private float mClose = 0f;

    private float mOpen = 0f;

    public CandleEntry(int xIndex, float shadowH, float shadowL, float open, float close) {
        super((shadowH + shadowL) / 2f, xIndex);

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
     * Returns the center value of the candle. (Middle value between high and
     * low)
     */
    @Override
    public float getVal() {
        return super.getVal();
    }

    public CandleEntry copy() {

        CandleEntry c = new CandleEntry(getXIndex(), mShadowHigh, mShadowLow, mOpen,
                mClose);

        return c;
    }

    public float getHigh() {
        return mShadowHigh;
    }

    public void setHigh(float mShadowHigh) {
        this.mShadowHigh = mShadowHigh;
    }

    public float getLow() {
        return mShadowLow;
    }

    public void setLow(float mShadowLow) {
        this.mShadowLow = mShadowLow;
    }

    public float getClose() {
        return mClose;
    }

    public void setClose(float mClose) {
        this.mClose = mClose;
    }

    public float getOpen() {
        return mOpen;
    }

    public void setOpen(float mOpen) {
        this.mOpen = mOpen;
    }
}
