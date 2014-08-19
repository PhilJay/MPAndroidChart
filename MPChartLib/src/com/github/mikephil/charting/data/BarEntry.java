
package com.github.mikephil.charting.data;

/**
 * Entry class for the BarChart. (especially stacked bars)
 * 
 * @author Philipp Jahoda
 */
public class BarEntry extends Entry {

    /** the values the stacked barchart holds */
    private float[] mVals;

    /**
     * Constructor for stacked bar entries.
     * 
     * @param vals
     * @param xIndex
     */
    public BarEntry(float[] vals, int xIndex) {
        super(calcSum(vals), xIndex);
        
        this.mVals = vals;
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
     * Returns an exact copy of the BarEntry.
     */
    public BarEntry copy() {

        BarEntry copied = new BarEntry(getVal(), getXIndex());
        copied.mVals = mVals;
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
        mVals = vals;
    }

    /**
     * Returns the closest value inside the values array (for stacked barchart)
     * to the value given as a parameter. The closest value must be higher
     * (above) the provided value.
     * 
     * @param val
     * @return
     */
    public int getClosestIndexAbove(float val) {

        if (mVals == null)
            return 0;

        float dist = 0f;
        int closestIndex = 0;

        for (int i = 0; i < mVals.length; i++) {

            float newDist = Math.abs((getVal() - mVals[i]) - val);

            if (newDist < dist && mVals[i] > val) {
                dist = newDist;
                closestIndex = i;
            }
        }

        return closestIndex;
    }

    /**
     * Calculates the sum across all values.
     * 
     * @param vals
     * @return
     */
    public static float calcSum(float[] vals) {

        float sum = 0f;

        for (float f : vals)
            sum += f;

        return sum;
    }
}
