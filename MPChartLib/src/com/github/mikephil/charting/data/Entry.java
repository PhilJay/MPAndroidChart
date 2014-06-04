
package com.github.mikephil.charting.data;

public class Entry {

    /** the actual value */
    private float mVal = 0f;

    /** the index on the x-axis */
    private int mXIndex = 0;

    /**
     * A Entry represents one single entry in the chart
     * 
     * @param val the y value
     * @param xIndex the corresponding index in the x value array (index on the
     *            x-axis of the chart)
     */
    public Entry(float val, int xIndex) {
        mVal = val;
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

    protected Entry cloneEntry() {
        Entry entry = new Entry(mVal, mXIndex);
        return entry;
    }

    //
    // /**
    // * Convenience method to create a series of double values
    // *
    // * @param yValues
    // * @return
    // */
    // public static ArrayList<Series> makeSeries(double[] yValues) {
    // ArrayList<Series> series = new ArrayList<Series>();
    // for (int i = 0; i < yValues.length; i++) {
    // series.add(new Series((float) yValues[i], 0, i));
    // }
    // return series;
    // }
    //
    // /**
    // * Convenience method to create multiple series of different types of
    // various double value arrays. Each double
    // array
    // * represents one type starting at 0.
    // *
    // * @param yValues
    // * @return
    // */
    // public static ArrayList<Series> makeMultipleSeries(ArrayList<Double[]>
    // yValues) {
    // ArrayList<Series> series = new ArrayList<Series>();
    //
    // int sizeOfFirst = yValues.get(0).length;
    //
    // for (int i = 0; i < yValues.size(); i++) {
    // Double[] curValues = yValues.get(i);
    // if (curValues.length != sizeOfFirst) {
    // throw new IllegalArgumentException("Array sizes do not match");
    // }
    // for (int j = 0; j < curValues.length; j++) {
    // series.add(new Series(curValues[j].floatValue(), i, j));
    // }
    // }
    //
    // return series;
    // }
    //
    // /**
    // * Convenience method to add a series. The new series has to be the same
    // size as the old. If you want to create a
    // * different sized series, please add manually.
    // *
    // * @param series
    // * @param yValues
    // * @param type
    // */
    // public static void addSeries(ArrayList<Series> series, double[] yValues,
    // int type) {
    // for (int i = 0; i < yValues.length; i++) {
    // series.add(new Series((float) yValues[i], type, i));
    // }
    // }

    public float getVal() {
        return mVal;
    }

    @Override
    public String toString() {
        return "xIndex: " + mXIndex + " yVal: " + mVal;
    }
}
