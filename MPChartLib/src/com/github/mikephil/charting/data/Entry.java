
package com.github.mikephil.charting.data;

/**
 * Class representing one entry in the chart. Might contain multiple values.
 * Might only contain a single value depending on the used constructor.
 * 
 * @author Philipp Jahoda
 */
public class Entry {

    /** the actual value */
    private float mVal = 0f;

    /** the index on the x-axis */
    private int mXIndex = 0;

    /** array of values, needed for stacked-barchart only */
    private float[] mVals;

    /**
     * A Entry represents one single entry in the chart.
     * 
     * @param val the y value (the actual value of the entry)
     * @param xIndex the corresponding index in the x value array (index on the
     *            x-axis of the chart, must NOT be higher than the length of the
     *            x-values String array)
     */
    public Entry(float val, int xIndex) {
        mVal = val;
        mXIndex = xIndex;
    }

    /**
     * A Entry represents one single entry in the chart.
     * 
     * @param vals the y values (the actual value of the entry) this entry
     *            should represent. E.g. multiple values for a stacked BarChart.
     * @param xIndex the corresponding index in the x value array (index on the
     *            x-axis of the chart, must NOT be higher than the length of the
     *            x-values String array)
     */
    public Entry(float[] vals, int xIndex) {
        mVals = vals;
        mXIndex = xIndex;
    }

    /**
     * returns the x-index the value of this object is mapped to
     * 
     * @return
     */
    public int getXIndex() {
        return mXIndex;
    }

    /**
     * sets the x-index for the entry
     * 
     * @param x
     */
    public void setXIndex(int x) {
        this.mXIndex = x;
    }

    /**
     * returns the value the entry represents
     * 
     * @return
     */
    public float getVal() {
        return mVal;
    }

    /**
     * sets the value for the entry
     * 
     * @param val
     */
    public void setVal(float val) {
        this.mVal = val;
    }

    /**
     * Returns the values this Entry reprsents, might return null if only a
     * single value is represented. Then, user getVal() instead.
     * 
     * @return
     */
    public float[] getVals() {
        return mVals;
    }

    /**
     * Set the array of values this Entry should represent.
     * 
     * @param vals
     */
    public void setVals(float[] vals) {
        mVals = vals;
    }

    /**
     * If this Enry represents mulitple values (e.g. Stacked BarChart), it will
     * return the sum of them, otherwise just the one value it represents.
     * 
     * @return
     */
    public float getSum() {
        if (mVals == null)
            return mVal;
        else {

            float sum = 0f;

            for (int i = 0; i < mVals.length; i++)
                sum += mVals[i];

            return sum;
        }
    }

    /**
     * returns an exact copy of the entry
     * 
     * @return
     */
    public Entry copy() {
        Entry e = new Entry(mVal, mXIndex);
        e.setVals(mVals);
        return e;
    }

    /**
     * returns a string representation of the entry containing x-index and value
     */
    @Override
    public String toString() {
        return "Entry, xIndex: " + mXIndex + " val (sum): " + getSum();
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

            float newDist = Math.abs((getSum() - mVals[i]) - val);

            if (newDist < dist && mVals[i] > val) {
                dist = newDist;
                closestIndex = i;
            }
        }

        return closestIndex;
    }
}
