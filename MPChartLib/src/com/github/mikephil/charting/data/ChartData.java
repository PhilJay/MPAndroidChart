
package com.github.mikephil.charting.data;

import android.util.Log;

import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.data.filter.Approximator;
import com.github.mikephil.charting.data.filter.Approximator.ApproximatorType;

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

    /** if true, there are approximated DataSets */
    private boolean mApproximatedData = false;

    /** the approximator, used to filter values */
    private Approximator mApproximator;

    /**
     * the filtered values, stored separately to not change the data set by the
     * user
     */
    private ArrayList<DataSet> mApproximatedDataSets;

    /** array that holds all the different type ids that are in the series array */
    private ArrayList<Integer> mDiffTypes;

    /**
     * constructor for chart data
     * 
     * @param xVals The values describing the x-axis. Must be at least as long
     *            as the highest xIndex in the Entry objects across all
     *            DataSets.
     * @param dataSets all DataSet objects the chart needs to represent
     */
    public ChartData(ArrayList<String> xVals, ArrayList<DataSet> dataSets) {
        init(xVals, dataSets);
    }

    public ChartData(String[] xVals, ArrayList<DataSet> dataSets) {
        ArrayList<String> newXVals = new ArrayList<String>();
        for (int i = 0; i < xVals.length; i++) {
            newXVals.add(xVals[i]);
        }
        init(newXVals, dataSets);
    }

    private void init(ArrayList<String> xVals, ArrayList<DataSet> dataSets) {
        this.mXVals = xVals;
        this.mDataSets = dataSets;
        mApproximator = new Approximator();

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
     * Call this method to let the CartData know that the underlying data has
     * changed.
     */
    public void notifyDataChanged() {
        if (mApproximatedData) {
            filter();
        } else {
            doCalculations();
        }
    }

    /**
     * Does all necessary calculations, if the underlying data has changed
     */
    private void doCalculations() {
        calcTypes();
        calcMinMax();
        calcYValueSum();
    }

    /**
     * calculates all different types that occur in the datasets and stores them
     * for fast access
     */
    private void calcTypes() {
        mDiffTypes = new ArrayList<Integer>();

        // check which dataset to use
        ArrayList<DataSet> dataSets = mApproximatedData ? mApproximatedDataSets : mDataSets;

        for (int i = 0; i < dataSets.size(); i++) {

            int type = dataSets.get(i).getType();

            if (!alreadyCounted(mDiffTypes, type)) {
                mDiffTypes.add(type);
            }
        }
    }

    /**
     * calc minimum and maximum y value over all datasets
     */
    private void calcMinMax() {
        // check which dataset to use
        ArrayList<DataSet> dataSets = mApproximatedData ? mApproximatedDataSets : mDataSets;

        mYMin = dataSets.get(0).getYMin();
        mYMax = dataSets.get(0).getYMax();

        for (int i = 0; i < dataSets.size(); i++) {
            if (dataSets.get(i).getYMin() < mYMin)
                mYMin = dataSets.get(i).getYMin();

            if (dataSets.get(i).getYMax() > mYMax)
                mYMax = dataSets.get(i).getYMax();
        }
    }

    /**
     * calculates the sum of all y-values in all datasets
     */
    private void calcYValueSum() {

        mYValueSum = 0;

        // check which dataset to use
        ArrayList<DataSet> dataSets = mApproximatedData ? mApproximatedDataSets : mDataSets;
        for (int i = 0; i < dataSets.size(); i++) {
            mYValueSum += Math.abs(dataSets.get(i).getYValueSum());
        }
    }

    private boolean alreadyCounted(ArrayList<Integer> countedTypes, int type) {
        for (int i = 0; i < countedTypes.size(); i++) {
            if (countedTypes.get(i) == type)
                return true;
        }

        return false;
    }

    /**
     * Corrects all values that are kept as member variables after a new entry
     * was added. This saves recalculating all values.
     * 
     * @param entry the new entry
     */
    public void notifyDataForNewEntry(Entry entry) {
        mYValueSum += Math.abs(entry.getVal());
        if (mYMin > entry.getVal()) {
            mYMin = entry.getVal();
        }
        if (mYMax < entry.getVal()) {
            mYMax = entry.getVal();
        }
    }

    /**
     * Sets a filter on the whole ChartData. If the type is NONE, the filtering
     * is reset. Be aware that the original DataSets are not modified. Instead
     * there are modified copies of the data. All methods return the filtered
     * values if a filter is set. To receive the original values despite a set
     * filter, call getOriginalDataSets().
     * 
     * @param type the filter type. NONE to reset filtering
     * @param tolerance the tolerance
     */
    public void setFilter(ApproximatorType type, double tolerance) {
        mApproximator.setTypeAndTolerance(type, tolerance);

        if (type != ApproximatorType.NONE) {
            mApproximatedData = true;
            // filter values
            filter();
        } else {
            mApproximatedData = false;
            // do calculations, because original values are used now
            doCalculations();
        }
    }

    /**
     * Call this method to filter the data. This is private, because if a user
     * of this library wants to filter again, he should use
     * notifyDataSetChanged() or setFilter() instead.
     */
    private void filter() {
        if (mApproximatedData) {
            mApproximatedDataSets = new ArrayList<DataSet>();
            for (int i = 0; i < mDataSets.size(); i++) {
                DataSet dataSet = mDataSets.get(i);
                ArrayList<Entry> filteredEntries = mApproximator.filter(dataSet.getYVals());
                if (filteredEntries != null) {
                    DataSet approximatedDataSet = new DataSet(filteredEntries, dataSet.getType());
                    mApproximatedDataSets.add(approximatedDataSet);
                } else {
                    // if filtering failed, copy data set
                    DataSet approximatedDataSet = dataSet.cloneDataSet();
                    mApproximatedDataSets.add(approximatedDataSet);
                }
            }
            doCalculations();
        } else {
            Log.e(Chart.LOG_TAG, "No filter set. Call setFilter() first");
        }
    }

    /**
     * if true, the DataSets are approximated. Use getOriginalDataSet() to
     * receive original values, even if filter apply.
     * 
     * @return
     */
    public boolean isApproximatedData() {
        return mApproximatedData;
    }

    public int getDataSetCount() {
        if (mApproximatedData) {
            return mApproximatedDataSets.size();
        }
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
        if (mXVals == null || mXVals.size() <= 1)
            return false;

        if (mApproximatedData) {
            if (mApproximatedDataSets == null || mApproximatedDataSets.size() < 1)
                return false;
        } else {
            if (mDataSets == null || mDataSets.size() < 1)
                return false;
        }
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
     * returns the Entries array from the DataSet at the given index. If a
     * filter is set, the filtered Entries are returned
     * 
     * @param index
     * @return
     */
    public ArrayList<Entry> getYVals(int index) {
        if (mApproximatedData) {
            return mApproximatedDataSets.get(index).getYVals();
        }
        return mDataSets.get(index).getYVals();
    }

    /**
     * returns the dataset at the given index. If a filter is set, the filtered
     * DataSet is returned.
     * 
     * @param index
     * @return
     */
    public DataSet getDataSetByIndex(int index) {
        if (mApproximatedData) {
            return mApproximatedDataSets.get(index);
        }
        return mDataSets.get(index);
    }

    /**
     * retrieve a dataset with a specific type from the chartdata. If a filter
     * is set, the filtered DataSet is returned.
     * 
     * @param type
     * @return
     */
    public DataSet getDataSetByType(int type) {
        // check which dataset to use
        ArrayList<DataSet> dataSets = mApproximatedData ? mApproximatedDataSets : mDataSets;

        for (int i = 0; i < dataSets.size(); i++)
            if (type == dataSets.get(i).getType())
                return dataSets.get(i);

        return null;
    }

    /**
     * returns all DataSet objects the ChartData represents. If a filter is set,
     * the filtered DataSets are returned
     * 
     * @return
     */
    public ArrayList<DataSet> getDataSets() {
        if (mApproximatedData) {
            return mApproximatedDataSets;
        }
        return mDataSets;
    }

    /**
     * This returns the original data set, regardless of any filter options.
     * 
     * @return
     */
    public ArrayList<DataSet> getOriginalDataSets() {
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
     * represents. If a filter is set, the filtered count is returned
     * 
     * @return
     */
    public int getYValCount() {
        int count = 0;
        // check which dataset to use
        ArrayList<DataSet> dataSets = mApproximatedData ? mApproximatedDataSets : mDataSets;

        for (int i = 0; i < dataSets.size(); i++) {
            count += dataSets.get(i).getEntryCount();
        }

        return count;
    }
}
