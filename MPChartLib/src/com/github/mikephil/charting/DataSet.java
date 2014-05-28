
package com.github.mikephil.charting;

import java.util.ArrayList;

/**
 * The DataSet class represents one group or type of entries (Series) in the
 * Chart that belong together.
 * 
 * @author Philipp Jahoda
 */
public class DataSet {

    /** the series that this dataset represents / holds together */
    private ArrayList<Series> mYVals;

    /** maximum y-value in the y-value array */
    private float mYMax = 0.0f;

    /** the minimum y-value in the y-value array */
    private float mYMin = 0.0f;

    /** the total sum of all y-values */
    private float mYValueSum = 0f;

    /** type, used for identification amongst other DataSets */
    private int mType = 0;

    /**
     * Creates a new DataSet object with the given values it represents and a
     * type for indentification amongst other DataSet objects (the type can be
     * chosen freely and must not be equal to another type in the ChartData
     * object).
     * 
     * @param yVals
     * @param type
     */
    public DataSet(ArrayList<Series> yVals, int type) {
        this.mType = type;
        this.mYVals = yVals;

        calcMinMax();
        calcYValueSum();
    }

    /**
     * calc minimum and maximum y value
     */
    private void calcMinMax() {

        mYMin = mYVals.get(0).getVal();
        mYMax = mYVals.get(0).getVal();

        for (int i = 0; i < mYVals.size(); i++) {
            if (mYVals.get(i).getVal() < mYMin)
                mYMin = mYVals.get(i).getVal();

            if (mYVals.get(i).getVal() > mYMax)
                mYMax = mYVals.get(i).getVal();
        }
    }

    /**
     * calculates the sum of all y-values
     */
    private void calcYValueSum() {

        mYValueSum = 0;

        for (int i = 0; i < mYVals.size(); i++) {
            mYValueSum += Math.abs(mYVals.get(i).getVal());
        }
    }

    /**
     * returns the number of y-values this DataSet represents
     * 
     * @return
     */
    public int getSeriesCount() {
        return mYVals.size();
    }

    /**
     * Returns the value of the Series object at the given xIndex. Returns
     * Float.NaN if no value is at the given x-index. INFORMATION: This method does calculations at runtime. Do not over-use in performance critical situations.
     * 
     * @param xIndex
     * @return
     */
    public float getYValForXIndex(int xIndex) {

        Series s = getSeriesForXIndex(xIndex);

        if (s != null)
            return s.getVal();
        else
            return Float.NaN;
    }

    /**
     * Returns the Series object at the given xIndex. INFORMATION: This method does calculations at runtime. Do not over-use in performance critical situations.
     * 
     * @param xIndex
     * @return
     */
    public Series getSeriesForXIndex(int xIndex) {

        for (int i = 0; i < mYVals.size(); i++) {
            if (xIndex == mYVals.get(i).getXIndex())
                return mYVals.get(i);
        }

        return null;
    }

    public ArrayList<Series> getYVals() {
        return mYVals;
    }

    public float getYValueSum() {
        return mYValueSum;
    }

    public float getYMin() {
        return mYMin;
    }

    public float getYMax() {
        return mYMax;
    }

    public int getType() {
        return mType;
    }

    /**
     * Convenience method to create multiple DataSets of different types with
     * various double value arrays. Each double array represents the data of one
     * DataSet with a type created by this method, starting at 0 (and
     * incremented).
     * 
     * @param yValues
     * @return
     */
    public static ArrayList<DataSet> makeDataSets(ArrayList<Double[]> yValues) {

        ArrayList<DataSet> dataSets = new ArrayList<DataSet>();

        for (int i = 0; i < yValues.size(); i++) {

            Double[] curValues = yValues.get(i);

            ArrayList<Series> series = new ArrayList<Series>();

            for (int j = 0; j < curValues.length; j++) {
                series.add(new Series(curValues[j].floatValue(), j));
            }

            dataSets.add(new DataSet(series, i));
        }

        return dataSets;
    }
}
