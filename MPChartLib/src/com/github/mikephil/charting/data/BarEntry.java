
package com.github.mikephil.charting.data;

import android.util.Log;

/**
 * Entry class for the BarChart. (especially stacked bars)
 * 
 * @author Philipp Jahoda
 */
public class BarEntry extends Entry {

    /** the values the stacked barchart holds */
    private float[] mVals;

    /** the sum of all negative values this entry (if stacked) contains */
    private float mNegativeSum;

    /**
     * Constructor for stacked bar entries. Don't forget to order the
     * stacked-values in an ascending order e.g. (-2,-1,0,1,2).
     * 
     * @param vals - the stack values
     * @param xIndex
     */
    public BarEntry(float[] vals, int xIndex) {
        super(calcSum(vals), xIndex);

        this.mVals = vals;
        calcNegativeSum();
    }

    /**
     * Constructor for normal bars (not stacked).
     * 
     * @param val
     * @param xIndex
     */
    public BarEntry(float val, int xIndex) {
        super(val, xIndex);
    }

    /**
     * Constructor for stacked bar entries. Don't forget to order the
     * stacked-values in an ascending order e.g. (-2,-1,0,1,2).
     * 
     * @param vals - the stack values
     * @param xIndex
     * @param label Additional description label.
     */
    public BarEntry(float[] vals, int xIndex, String label) {
        super(calcSum(vals), xIndex, label);

        this.mVals = vals;
        calcNegativeSum();
    }

    /**
     * Constructor for normal bars (not stacked).
     * 
     * @param val
     * @param xIndex
     * @param data Spot for additional data this Entry represents.
     */
    public BarEntry(float val, int xIndex, Object data) {
        super(val, xIndex, data);
    }

    /**
     * Returns an exact copy of the BarEntry.
     */
    public BarEntry copy() {

        BarEntry copied = new BarEntry(getVal(), getXIndex(), getData());
        copied.setVals(mVals);
        return copied;
    }

    /**
     * Returns the stacked values this BarEntry represents, or null, if only a
     * single value is represented (then, use getVal()).
     * 
     * @return
     */
    public float[] getVals() {
        return mVals;
    }

    /**
     * Set the array of values this BarEntry should represent.
     * 
     * @param vals
     */
    public void setVals(float[] vals) {
        setVal(calcSum(vals));
        mVals = vals;
        calcNegativeSum();
    }
    
    public float getBelowSum(int stackIndex) {
        
        if (mVals == null)
            return 0;
        
        float remainder = 0f;
        int index = mVals.length - 1;
        
        while(index > stackIndex && index >= 0) {
            remainder += mVals[index];
            index--;
        }
        
        return remainder;
    }

    /**
     * Reuturns the sum of all positive values this entry (if stacked) contains.
     * @return
     */
    public float getPositiveSum() {

        if(mVals == null)
            return 0f;

        float sum = 0f;

        for (float f : mVals) {
            if(f >= 0f)
                sum += f;
        }

        return sum;
    }

    /**
     * Returns the sum of all negative values this entry (if stacked) contains. (this is a positive number)
     * @return
     */
    public float getNegativeSum() {
        return mNegativeSum;
    }

    private void calcNegativeSum() {
        if(mVals == null) {
            mNegativeSum = 0;
            return;
        }

        float sum = 0f;

        for (float f : mVals) {
            if(f <= 0f)
                sum += Math.abs(f);
        }

        mNegativeSum = sum;
    }


    /**
     * Calculates the sum across all values of the given stack.
     *
     * @param vals
     * @return
     */
    private static float calcSum(float[] vals) {

        if(vals == null)
            return 0f;

        float sum = 0f;

        for (float f : vals)
            sum += f;

        return sum;
    }
}
