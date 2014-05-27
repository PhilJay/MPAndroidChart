
package com.github.mikephil.charting;

import java.util.ArrayList;

/**
 * Class that holds all relevant data that represents the chart
 * 
 * @author Philipp
 */
public class ChartData {

    /** maximum y-value in the y-value array */
    private float mYMax = 0.0f;

    /** the minimum y-value in the y-value array */
    private float mYMin = 0.0f;

    /** the total sum of all y-values */
    private float mYValueSum = 0f;

    /** holds all x-values the chart represents */
    private ArrayList<String> mXVals;

    /** holds all the datasets (e.g. different lines) the chart represents */
    private ArrayList<DataSet> mDataSets;

    /** array that holds all the different type ids that are in the series array */
    private ArrayList<Integer> mDiffTypes;

    /**
     * constructor for chart data
     * 
     * @param xVals must be at least as long as the longest series array of one
     *            type
     * @param dataSets all DataSet objects the chart needs to represent
     */
    public ChartData(ArrayList<String> xVals, ArrayList<DataSet> dataSets) {
        this.mXVals = xVals;
        this.mDataSets = dataSets;

        calcTypes();
        calcMinMax();
        calcYValueSum();

        for (int i = 0; i < mDataSets.size(); i++) {
            if (mDataSets.get(i).getYVals().size() > xVals.size()) {
                throw new IllegalArgumentException(
                        "x values are smaller than the largest y series array of one type");
            }
        }
    }

    /**
     * calculates all different types that occur in the datasets and stores them
     * for fast access
     */
    private void calcTypes() {
        mDiffTypes = new ArrayList<Integer>();

        for (int i = 0; i < mDataSets.size(); i++) {

            int type = mDataSets.get(i).getType();

            if (!alreadyCounted(mDiffTypes, type)) {
                mDiffTypes.add(type);
            }
        }
    }

    /**
     * calc minimum and maximum y value over all datasets
     */
    private void calcMinMax() {

        mYMin = mDataSets.get(0).getYMin();
        mYMax = mDataSets.get(0).getYMax();

        for (int i = 0; i < mDataSets.size(); i++) {
            if (mDataSets.get(i).getYMin() < mYMin)
                mYMin = mDataSets.get(i).getYMin();

            if (mDataSets.get(i).getYMax() > mYMax)
                mYMax = mDataSets.get(i).getYMax();
        }
    }

    /**
     * calculates the sum of all y-values in all datasets
     */
    private void calcYValueSum() {

        mYValueSum = 0;

        for (int i = 0; i < mDataSets.size(); i++) {
            mYValueSum += Math.abs(mDataSets.get(i).getYValueSum());
        }
    }

    private boolean alreadyCounted(ArrayList<Integer> countedTypes, int type) {
        for (int i = 0; i < countedTypes.size(); i++) {
            if (countedTypes.get(i) == type)
                return true;
        }

        return false;
    }

    public int getDataSetCount() {
        return mDataSets.size();
    }

    public float getYMin() {
        return mYMin;
    }

    public float getYMax() {
        return mYMax;
    }

    public float getYValueSum() {
        return mYValueSum;
    }

    /**
     * Checks if the ChartData object contains valid data
     * 
     * @return
     */
    public boolean isValid() {

        if (mXVals == null || mXVals.size() <= 1 || mDataSets == null || mDataSets.size() < 1
                || mDataSets.get(0).getYVals().size() <= 1)
            return false;
        else
            return true;
    }

    /**
     * returns the x-values the chart represents
     * 
     * @return
     */
    public ArrayList<String> getXVals() {
        return mXVals;
    }

    /**
     * returns the series object at the given dataset index
     * 
     * @param index
     * @return
     */
    public ArrayList<Series> getYVals(int index) {
        return mDataSets.get(index).getYVals();
    }

    /**
     * returns the dataset at the given index
     * 
     * @param index
     * @return
     */
    public DataSet getDataSetByIndex(int index) {
        return mDataSets.get(index);
    }

    /**
     * retrieve a dataset with a specific type from the chartdata
     * 
     * @param type
     * @return
     */
    public DataSet getDataSetByType(int type) {
        for (int i = 0; i < mDataSets.size(); i++)
            if (type == mDataSets.get(i).getType())
                return mDataSets.get(i);

        return null;
    }
    
    /**
     * returns all DataSet objects the ChartData represents.
     * @return
     */
    public ArrayList<DataSet> getDataSets() {
        return mDataSets;
    }

    /**
     * returns all the different DataSet types the chartdata represents
     * 
     * @return
     */
    public ArrayList<Integer> getTypes() {
        return mDiffTypes;
    }

    /**
     * returns the total number of x-values this chartdata represents (the size
     * of the xvals array)
     * 
     * @return
     */
    public int getXValCount() {
        return mXVals.size();
    }

    /**
     * returns the total number of y-values across all DataSets the chartdata
     * represents
     * 
     * @return
     */
    public int getYValCount() {
        int count = 0;
        for (int i = 0; i < mDataSets.size(); i++) {
            count += mDataSets.get(i).getYValCount();
        }

        return count;
    }
}
