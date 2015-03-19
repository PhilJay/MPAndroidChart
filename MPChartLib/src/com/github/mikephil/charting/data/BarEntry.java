
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
     * @param vals - the stack values
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
     * Constructor for stacked bar entries.
     * 
     * @param vals - the stack values
     * @param xIndex
     * @param label Additional description label.
     */
    public BarEntry(float[] vals, int xIndex, String label) {
        super(calcSum(vals), xIndex, label);

        this.mVals = vals;
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
        
        int index = mVals.length - 1;
        float remainder = 0f;

        while(index > 0 && val > mVals[index] + remainder) {
            remainder += mVals[index];
            index--;
        }
        
        return index;
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
     * Calculates the sum across all values.
     * 
     * @param vals
     * @return
     */
    private static float calcSum(float[] vals) {

        float sum = 0f;

        for (float f : vals)
            sum += f;

        return sum;
    }
}
