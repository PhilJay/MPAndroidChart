
package com.github.mikephil.charting.data;

import com.github.mikephil.charting.utils.Highlight;
import com.github.mikephil.charting.utils.LimitLine;

import java.util.ArrayList;

/**
 * Class that holds all relevant data that represents the chart. That involves
 * at least one (or more) DataSets, and an array of x-values.
 * 
 * @author Philipp Jahoda
 */
public abstract class ChartData {

    /** maximum y-value in the y-value array */
    protected float mYMax = 0.0f;

    /** the minimum y-value in the y-value array */
    protected float mYMin = 0.0f;

    /** the total sum of all y-values */
    private float mYValueSum = 0f;

    /** total number of y-values across all DataSet objects */
    private int mYValCount = 0;

    /**
     * contains the average length (in characters) an entry in the x-vals array
     * has
     */
    private int mXValAverageLength = 0;

    /** holds all x-values the chart represents */
    protected ArrayList<String> mXVals;

    /** array that holds all DataSets the ChartData object represents */
    protected ArrayList<? extends DataSet> mDataSets;

    /**
     * constructor for chart data
     * 
     * @param xVals The values describing the x-axis. Must be at least as long
     *            as the highest xIndex in the Entry objects across all
     *            DataSets.
     * @param sets the dataset array
     */
    public ChartData(ArrayList<String> xVals, ArrayList<? extends DataSet> sets) {
        this.mXVals = xVals;
        this.mDataSets = sets;

        init();
    }

    /**
     * constructor that takes string array instead of arraylist string
     * 
     * @param xVals The values describing the x-axis. Must be at least as long
     *            as the highest xIndex in the Entry objects across all
     *            DataSets.
     * @param sets the dataset array
     */
    public ChartData(String[] xVals, ArrayList<? extends DataSet> sets) {
        ArrayList<String> newXVals = new ArrayList<String>();
        for (int i = 0; i < xVals.length; i++) {
            newXVals.add(xVals[i]);
        }
        this.mXVals = newXVals;
        this.mDataSets = sets;

        init();
    }

    /**
     * performs all kinds of initialization calculations, such as min-max and
     * value count and sum
     */
    private void init() {

        isLegal(mDataSets);

        calcMinMax(mDataSets);
        calcYValueSum(mDataSets);
        calcYValueCount(mDataSets);

        calcXValAverageLength();
    }

    /**
     * calculates the average length (in characters) across all x-value strings
     */
    private void calcXValAverageLength() {

        int sum = 0;

        for (int i = 0; i < mXVals.size(); i++) {
            sum += mXVals.get(i).length();
        }

        mXValAverageLength = sum / mXVals.size();
    }

    protected static ArrayList<? extends DataSet> toArrayList(DataSet dataSet) {
        ArrayList<DataSet> sets = new ArrayList<DataSet>();
        sets.add(dataSet);
        return sets;
    }

    /**
     * Checks if the combination of x-values array and DataSet array is legal or
     * not.
     * 
     * @param dataSets
     */
    private void isLegal(ArrayList<? extends DataSet> dataSets) {

        for (int i = 0; i < dataSets.size(); i++) {
            if (dataSets.get(i)
                    .getYVals()
                    .size() > mXVals.size()) {
                throw new IllegalArgumentException(
                        "One or more of the DataSet Entry arrays are longer than the x-values array of this ChartData object.");
            }
        }
    }

    /**
     * Call this method to let the CartData know that the underlying data has
     * changed.
     */
    public void notifyDataChanged() {
        init();
    }

    /**
     * calc minimum and maximum y value over all datasets
     */
    protected void calcMinMax(ArrayList<? extends DataSet> dataSets) {

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
    protected void calcYValueSum(ArrayList<? extends DataSet> dataSets) {

        mYValueSum = 0;

        for (int i = 0; i < dataSets.size(); i++) {
            mYValueSum += Math.abs(dataSets.get(i).getYValueSum());
        }
    }

    /**
     * Calculates the total number of y-values across all DataSets the ChartData
     * represents.
     * 
     * @return
     */
    protected void calcYValueCount(ArrayList<? extends DataSet> dataSets) {
        int count = 0;

        for (int i = 0; i < dataSets.size(); i++) {
            count += dataSets.get(i).getEntryCount();
        }

        mYValCount = count;
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

    /** ONLY GETTERS AND SETTERS BELOW THIS */

    /**
     * returns the number of LineDataSets this object contains
     * 
     * @return
     */
    public int getDataSetCount() {
        return mDataSets.size();
    }

    public float getYMin() {
        return mYMin;
    }

    public float getYMax() {
        return mYMax;
    }

    /**
     * returns the average length (in characters) across all values in the
     * x-vals array
     * 
     * @return
     */
    public int getXValAverageLength() {
        return mXValAverageLength;
    }

    /**
     * Returns the total y-value sum across all DataSet objects the this object
     * represents.
     * 
     * @return
     */
    public float getYValueSum() {
        return mYValueSum;
    }

    /**
     * Returns the total number of y-values across all DataSet objects the this
     * object represents.
     * 
     * @return
     */
    public int getYValCount() {
        return mYValCount;
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
     * Returns an the array of DataSets this object holds.
     * 
     * @return
     */
    public ArrayList<? extends DataSet> getDataSets() {
        return mDataSets;
    }

    // /**
    // * returns the Entries array from the DataSet at the given index. If a
    // * filter is set, the filtered Entries are returned
    // *
    // * @param index
    // * @return
    // */
    // public ArrayList<Entry> getYVals(int index) {
    // return mDataSets.get(index).getYVals();
    // }

    /**
     * Retrieve the index of a DataSet with a specific label from the ChartData.
     * Search can be case sensitive or not. IMPORTANT: This method does
     * calculations at runtime, do not over-use in performance critical
     * situations.
     * 
     * @param dataSets the DataSet array to search
     * @param type
     * @param ignorecase if true, the search is not case-sensitive
     * @return
     */
    protected int getDataSetIndexByLabel(ArrayList<? extends DataSet> dataSets, String label,
            boolean ignorecase) {

        if (ignorecase) {
            for (int i = 0; i < dataSets.size(); i++)
                if (label.equalsIgnoreCase(dataSets.get(i).getLabel()))
                    return i;
        } else {
            for (int i = 0; i < dataSets.size(); i++)
                if (label.equals(dataSets.get(i).getLabel()))
                    return i;
        }

        return -1;
    }

    /**
     * returns the total number of x-values this ChartData object represents
     * (the size of the x-values array)
     * 
     * @return
     */
    public int getXValCount() {
        return mXVals.size();
    }

    /**
     * Returns the labels of all DataSets as a string array.
     * 
     * @return
     */
    protected String[] getDataSetLabels() {

        String[] types = new String[mDataSets.size()];

        for (int i = 0; i < mDataSets.size(); i++) {
            types[i] = mDataSets.get(i).getLabel();
        }

        return types;
    }

    /**
     * Get the Entry for a corresponding highlight object
     * 
     * @param highlight
     * @return the entry that is highlighted
     */
    public Entry getEntryForHighlight(Highlight highlight) {
        return mDataSets.get(highlight.getDataSetIndex()).getEntryForXIndex(
                highlight.getXIndex());
    }

    /**
     * Returns the DataSet object with the given label. Search can be case
     * sensitive or not. IMPORTANT: This method does calculations at runtime.
     * Use with care in performance critical situations.
     * 
     * @param label
     * @param ignorecase
     * @return
     */
    public DataSet getDataSetByLabel(String label, boolean ignorecase) {

        int index = getDataSetIndexByLabel(mDataSets, label, ignorecase);

        if (index <= 0 || index >= mDataSets.size())
            return null;
        else
            return mDataSets.get(index);
    }

    /**
     * Returns the DataSet object at the given index.
     * 
     * @param index
     * @return
     */
    public DataSet getDataSetByIndex(int index) {
        return mDataSets.get(index);
    }

    /**
     * Adds a DataSet dynamically.
     * 
     * @param d
     */
    public void addDataSet(DataSet d) {
        if (mDataSets == null)
            mDataSets = new ArrayList<DataSet>();
        ((ArrayList<DataSet>) mDataSets).add(d);

        mYValCount += d.getEntryCount();
        mYValueSum += d.getYValueSum();

        if (mYMax < d.getYMax())
            mYMax = d.getYMax();
        if (mYMin > d.getYMin())
            mYMin = d.getYMin();
    }

    /**
     * Returns all colors used across all DataSet objects this object
     * represents.
     * 
     * @return
     */
    public int[] getColors() {

        int clrcnt = 0;

        for (int i = 0; i < mDataSets.size(); i++) {
            clrcnt += mDataSets.get(i).getColors().size();
        }

        int[] colors = new int[clrcnt];
        int cnt = 0;

        for (int i = 0; i < mDataSets.size(); i++) {

            ArrayList<Integer> clrs = mDataSets.get(i).getColors();

            for (Integer clr : clrs) {
                colors[cnt] = clr;
                cnt++;
            }
        }

        return colors;
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
