
package com.github.mikephil.charting.data;

import java.util.ArrayList;

import com.github.mikephil.charting.utils.Highlight;

/**
 * Class that holds all relevant data that represents the chart. That involves
 * at least one (or more) DataSets, and an array of x-values.
 * 
 * @author Philipp Jahoda
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

    /** array that holds all the different labels that are in the DataSet array */
    private ArrayList<String> mDiffLabels;

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

    /**
     * constructor that takes string array instead of arraylist string
     * 
     * @param xVals The values describing the x-axis. Must be at least as long
     *            as the highest xIndex in the Entry objects across all
     *            DataSets.
     * @param dataSets all DataSet objects the chart needs to represent
     */
    public ChartData(String[] xVals, ArrayList<DataSet> dataSets) {
        ArrayList<String> newXVals = new ArrayList<String>();
        for (int i = 0; i < xVals.length; i++) {
            newXVals.add(xVals[i]);
        }
        init(newXVals, dataSets);
    }

    /**
     * Constructor that takes only one DataSet
     * 
     * @param xVals
     * @param data
     */
    public ChartData(ArrayList<String> xVals, DataSet data) {

        ArrayList<DataSet> sets = new ArrayList<DataSet>();
        sets.add(data);
        init(xVals, sets);
    }

    private void init(ArrayList<String> xVals, ArrayList<DataSet> dataSets) {
        this.mXVals = xVals;
        this.mDataSets = dataSets;

        calcTypes();
        calcMinMax();
        calcYValueSum();

        for (int i = 0; i < mDataSets.size(); i++) {
            if (mDataSets.get(i)
                    .getYVals()
                    .size() > xVals.size()) {
                throw new IllegalArgumentException(
                        "One or more of the DataSet Entry arrays are longer than the x-values array.");
            }
        }
    }

    /**
     * Call this method to let the CartData know that the underlying data has
     * changed.
     */
    public void notifyDataChanged() {
        doCalculations();
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
     * calculates all different labels that occur in the DataSets and stores
     * them for fast access
     */
    private void calcTypes() {
        mDiffLabels = new ArrayList<String>();

        // check which dataset to use
        ArrayList<DataSet> dataSets = mDataSets;

        for (int i = 0; i < dataSets.size(); i++) {

            String label = dataSets.get(i).getLabel();

            if (!alreadyCounted(mDiffLabels, label)) {
                mDiffLabels.add(label);
            }
        }
    }

    /**
     * calc minimum and maximum y value over all datasets
     */
    private void calcMinMax() {
        // check which dataset to use
        ArrayList<DataSet> dataSets = mDataSets;

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
        ArrayList<DataSet> dataSets = mDataSets;
        for (int i = 0; i < dataSets.size(); i++) {
            mYValueSum += Math.abs(dataSets.get(i).getYValueSum());
        }
    }

    private boolean alreadyCounted(ArrayList<String> countedLabels, String label) {
        for (int i = 0; i < countedLabels.size(); i++) {
            if (countedLabels.get(i).equals(label))
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

        if (mYMin == mYMax) {
            mYMin--;
            mYMax++;
        }
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
        if (mXVals == null || mXVals.size() <= 1)
            return false;

        if (mDataSets == null || mDataSets.size() < 1)
            return false;

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
        return mDataSets.get(index).getYVals();
    }

    /**
     * Get the entry for a corresponding highlight object
     * 
     * @param highlight
     * @return the entry that is highlighted
     */
    public Entry getEntryForHighlight(Highlight highlight) {
        return getDataSetByIndex(highlight.getDataSetIndex()).getEntryForXIndex(
                highlight.getXIndex());
    }

    /**
     * returns the dataset at the given index.
     * 
     * @param index
     * @return
     */
    public DataSet getDataSetByIndex(int index) {
        return mDataSets.get(index);
    }

    /**
     * Retrieve a DataSet with a specific label from the ChartData. Search can
     * be case sensitive or not. IMPORTANT: This method does calculations at
     * runtime, do not over-use in performance critical situations.
     * 
     * @param type
     * @param ignorecase if true, the search is not case-sensitive
     * @return
     */
    public DataSet getDataSetByLabel(String label, boolean ignorecase) {
        // check which dataset to use
        ArrayList<DataSet> dataSets = mDataSets;

        if (ignorecase) {
            for (int i = 0; i < dataSets.size(); i++)
                if (label.equalsIgnoreCase(dataSets.get(i).getLabel()))
                    return dataSets.get(i);
        } else {
            for (int i = 0; i < dataSets.size(); i++)
                if (label.equals(dataSets.get(i).getLabel()))
                    return dataSets.get(i);
        }

        return null;
    }

    /**
     * returns all DataSet objects the ChartData represents. If a filter is set,
     * the filtered DataSets are returned
     * 
     * @return
     */
    public ArrayList<DataSet> getDataSets() {
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
     * returns all the different DataSet labels the chartdata represents
     * 
     * @return
     */
    public ArrayList<String> getLabels() {
        return mDiffLabels;
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
        ArrayList<DataSet> dataSets = mDataSets;

        for (int i = 0; i < dataSets.size(); i++) {
            count += dataSets.get(i).getEntryCount();
        }

        return count;
    }

    /**
     * Returns the labels of all DataSets as a string array.
     * 
     * @return
     */
    public String[] getDataSetLabels() {

        String[] types = new String[mDataSets.size()];

        for (int i = 0; i < mDataSets.size(); i++) {
            types[i] = mDataSets.get(i).getLabel();
        }

        return types;
    }

    /**
     * Generates an x-values array filled with numbers in range specified by the
     * parameters. Can be used for convenience.
     * 
     * @return
     */
    public static ArrayList<String> generateXVals(int from, int to) {

        ArrayList<String> xvals = new ArrayList<String>();

        for (int i = from; i < to; i++) {
            xvals.add("" + i);
        }

        return xvals;
    }
}
