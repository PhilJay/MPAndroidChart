package com.github.mikephil.charting.data;

import android.annotation.SuppressLint;

/**
 * Entry class for the BarChart. (especially stacked bars)
 *
 * @author Philipp Jahoda
 */
@SuppressLint("ParcelCreator")
public class BarEntry extends Entry {

    /**
     * the values the stacked barchart holds
     */
    private float[] mYVals;

    /**
     * the sum of all negative values this entry (if stacked) contains
     */
    private float mNegativeSum;

    /**
     * the sum of all positive values this entry (if stacked) contains
     */
    private float mPositiveSum;

    /**
     * Constructor for stacked bar entries.
     *
     * @param x
     * @param vals - the stack values, use at lest 2
     */
    public BarEntry(float x, float[] vals) {
        super(x, calcSum(vals));

        this.mYVals = vals;
        calcPosNegSum();
    }

    /**
     * Constructor for normal bars (not stacked).
     *
     * @param x
     * @param y
     */
    public BarEntry(float x, float y) {
        super(x, y);
    }

    /**
     * Constructor for stacked bar entries.
     *
     * @param x
     * @param vals  - the stack values, use at least 2
     * @param label Additional description label.
     */
    public BarEntry(float x, float[] vals, String label) {
        super(x, calcSum(vals), label);

        this.mYVals = vals;
        calcPosNegSum();
    }

    /**
     * Constructor for normal bars (not stacked).
     *
     * @param x
     * @param y
     * @param data Spot for additional data this Entry represents.
     */
    public BarEntry(float x, float y, Object data) {
        super(x, y, data);
    }

    /**
     * Returns an exact copy of the BarEntry.
     */
    public BarEntry copy() {

        BarEntry copied = new BarEntry(getX(), getY(), getData());
        copied.setVals(mYVals);
        return copied;
    }

    /**
     * Returns the stacked values this BarEntry represents, or null, if only a single value is represented (then, use
     * getY()).
     *
     * @return
     */
    public float[] getYVals() {
        return mYVals;
    }

    /**
     * Set the array of values this BarEntry should represent.
     *
     * @param vals
     */
    public void setVals(float[] vals) {
        setY(calcSum(vals));
        mYVals = vals;
        calcPosNegSum();
    }

    /**
     * Returns the value of this BarEntry. If the entry is stacked, it returns the positive sum of all values.
     *
     * @return
     */
    @Override
    public float getY() {
        return super.getY();
    }

    /**
     * Returns true if this BarEntry is stacked (has a values array), false if not.
     *
     * @return
     */
    public boolean isStacked() {
        return mYVals != null;
    }

    public float getBelowSum(int stackIndex) {

        if (mYVals == null)
            return 0;

        float remainder = 0f;
        int index = mYVals.length - 1;

        while (index > stackIndex && index >= 0) {
            remainder += mYVals[index];
            index--;
        }

        return remainder;
    }

    /**
     * Reuturns the sum of all positive values this entry (if stacked) contains.
     *
     * @return
     */
    public float getPositiveSum() {
        return mPositiveSum;
    }

    /**
     * Returns the sum of all negative values this entry (if stacked) contains. (this is a positive number)
     *
     * @return
     */
    public float getNegativeSum() {
        return mNegativeSum;
    }

    private void calcPosNegSum() {

        if (mYVals == null) {
            mNegativeSum = 0;
            mPositiveSum = 0;
            return;
        }

        float sumNeg = 0f;
        float sumPos = 0f;

        for (float f : mYVals) {
            if (f <= 0f)
                sumNeg += Math.abs(f);
            else
                sumPos += f;
        }

        mNegativeSum = sumNeg;
        mPositiveSum = sumPos;
    }

    /**
     * Calculates the sum across all values of the given stack.
     *
     * @param vals
     * @return
     */
    private static float calcSum(float[] vals) {

        if (vals == null)
            return 0f;

        float sum = 0f;

        for (float f : vals)
            sum += f;

        return sum;
    }
}
