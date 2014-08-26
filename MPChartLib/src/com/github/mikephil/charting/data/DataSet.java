
package com.github.mikephil.charting.data;

import android.content.Context;
import android.graphics.Color;

import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

/**
 * The DataSet class represents one group or type of entries (Entry) in the
 * Chart that belong together. It is designed to logically separate different
 * groups of values inside the Chart (e.g. the values for a specific line in the
 * LineChart, or the values of a specific group of bars in the BarChart).
 * 
 * @author Philipp Jahoda
 */
public abstract class DataSet {

    /** arraylist representing all colors that are used for this DataSet */
    protected ArrayList<Integer> mColors = null;

    /** the entries that this dataset represents / holds together */
    protected ArrayList<? extends Entry> mYVals = null;

    /** maximum y-value in the y-value array */
    protected float mYMax = 0.0f;

    /** the minimum y-value in the y-value array */
    protected float mYMin = 0.0f;

    /** the total sum of all y-values */
    private float mYValueSum = 0f;

    /** label that describes the DataSet or the data the DataSet represents */
    private String mLabel = "DataSet";

    /**
     * Creates a new DataSet object with the given values it represents. Also, a
     * label that describes the DataSet can be specified. The label can also be
     * used to retrieve the DataSet from a ChartData object.
     * 
     * @param yVals
     * @param label
     */
    public DataSet(ArrayList<? extends Entry> yVals, String label) {

        this.mLabel = label;
        this.mYVals = yVals;

        // if (yVals.size() <= 0) {
        // return;
        // }

        mColors = new ArrayList<Integer>();

        // default colors
        // mColors.add(Color.rgb(192, 255, 140));
        // mColors.add(Color.rgb(255, 247, 140));
        mColors.add(Color.rgb(140, 234, 255));

        calcMinMax();
        calcYValueSum();
    }

    /**
     * Use this method to tell the data set that the underlying data has changed
     */
    public void notifyDataSetChanged() {
        calcMinMax();
        calcYValueSum();
    }

    /**
     * calc minimum and maximum y value
     */
    protected void calcMinMax() {
        if (mYVals.size() == 0) {
            return;
        }

        mYMin = mYVals.get(0).getVal();
        mYMax = mYVals.get(0).getVal();

        for (int i = 0; i < mYVals.size(); i++) {

            Entry e = mYVals.get(i);

            if (e.getVal() < mYMin)
                mYMin = e.getVal();

            if (e.getVal() > mYMax)
                mYMax = e.getVal();
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
    public int getEntryCount() {
        return mYVals.size();
    }

    /**
     * Returns the value of the Entry object at the given xIndex. Returns
     * Float.NaN if no value is at the given x-index. INFORMATION: This method
     * does calculations at runtime. Do not over-use in performance critical
     * situations.
     * 
     * @param xIndex
     * @return
     */
    public float getYValForXIndex(int xIndex) {

        Entry e = getEntryForXIndex(xIndex);

        if (e != null)
            return e.getVal();
        else
            return Float.NaN;
    }

    /**
     * Returns the first Entry object found at the given xIndex with binary
     * search. Returns null if no Entry object at that index. INFORMATION: This
     * method does calculations at runtime. Do not over-use in performance
     * critical situations.
     * 
     * @param xIndex
     * @return
     */
    public Entry getEntryForXIndex(int x) {

//        for (int i = 0; i < mYVals.size(); i++) {
//            if (xIndex == mYVals.get(i).getXIndex())
//                return mYVals.get(i);
//        }

        int low = 0;
        int high = mYVals.size();

        while (low <= high) {
            int m = (high + low) / 2;

            if (x == mYVals.get(m).getXIndex()) {
                return mYVals.get(m);
            }

            if (x > mYVals.get(m).getXIndex())
                low = m + 1;
            else
                high = m - 1;
        }

        return null;
    }

    /**
     * Returns all Entry objects at the given xIndex. INFORMATION: This method
     * does calculations at runtime. Do not over-use in performance critical
     * situations.
     * 
     * @param xIndex
     * @return
     */
    public ArrayList<Entry> getEntriesForXIndex(int xIndex) {

        ArrayList<Entry> entries = new ArrayList<Entry>();

        for (int i = 0; i < mYVals.size(); i++) {
            if (xIndex == mYVals.get(i).getXIndex())
                entries.add(mYVals.get(i));
        }

        return entries;
    }

    /**
     * returns the DataSets Entry array
     * 
     * @return
     */
    public ArrayList<? extends Entry> getYVals() {
        return mYVals;
    }

    /**
     * gets the sum of all y-values
     * 
     * @return
     */
    public float getYValueSum() {
        return mYValueSum;
    }

    /**
     * returns the minimum y-value this DataSet holds
     * 
     * @return
     */
    public float getYMin() {
        return mYMin;
    }

    /**
     * returns the maximum y-value this DataSet holds
     * 
     * @return
     */
    public float getYMax() {
        return mYMax;
    }

    /**
     * returns the type of the DataSet, specified via constructor
     * 
     * @return
     */
    // public int getType() {
    // return mType;
    // }

    /**
     * The xIndex of an Entry object is provided. This method returns the actual
     * index in the Entry array of the DataSet. IMPORTANT: This method does
     * calculations at runtime, do not over-use in performance critical
     * situations.
     * 
     * @param xIndex
     * @return
     */
    public int getIndexInEntries(int xIndex) {

        for (int i = 0; i < mYVals.size(); i++) {
            if (xIndex == mYVals.get(i).getXIndex())
                return i;
        }

        return -1;
    }

    /**
     * Provides an exact copy of the DataSet this method is used on.
     * 
     * @return
     */
    public abstract DataSet copy();

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append(toSimpleString());
        for (int i = 0; i < mYVals.size(); i++) {
            buffer.append(mYVals.get(i).toString() + " ");
        }
        return buffer.toString();
    }

    /**
     * Returns a simple string representation of the DataSet with the type and
     * the number of Entries.
     * 
     * @return
     */
    public String toSimpleString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("DataSet, label: " + mLabel + ", entries: " + mYVals.size() + "\n");
        return buffer.toString();
    }

    /**
     * Returns the label string that describes the DataSet.
     * 
     * @return
     */
    public String getLabel() {
        return mLabel;
    }

    // /**
    // * Adds an Entry dynamically.
    // *
    // * @param d
    // */
    // public void addEntry(Entry e) {
    //
    // float sum = e.getSum();
    //
    // if(mYVals == null || mYVals.size() <= 0) {
    //
    // mYVals = new ArrayList<Entry>();
    // mYMax = sum;
    // mYMin = sum;
    // } else {
    //
    // if(mYMax < sum) mYMax = sum;
    // if(mYMin > sum) mYMin = sum;
    // }
    //
    // mYVals.add(e);
    //
    // mYValueSum += sum;
    // }

    /** BELOW THIS COLOR HANDLING */

    /**
     * Sets the colors that should be used fore this DataSet. Colors are reused
     * as soon as the number of Entries the DataSet represents is higher than
     * the size of the colors array. Make sure that the colors are already
     * prepared (by calling getResources().getColor(...)) before adding them to
     * the DataSet.
     * 
     * @param colors
     */
    public void setColors(ArrayList<Integer> colors) {
        this.mColors = colors;
    }

    /**
     * Sets the colors that should be used fore this DataSet. Colors are reused
     * as soon as the number of Entries the DataSet represents is higher than
     * the size of the colors array. Make sure that the colors are already
     * prepared (by calling getResources().getColor(...)) before adding them to
     * the DataSet.
     * 
     * @param colors
     */
    public void setColors(int[] colors) {
        this.mColors = ColorTemplate.createColors(colors);
    }

    /**
     * Sets the colors that should be used fore this DataSet. Colors are reused
     * as soon as the number of Entries the DataSet represents is higher than
     * the size of the colors array. You can use
     * "new int[] { R.color.red, R.color.green, ... }" to provide colors for
     * this method. Internally, the colors are resolved using
     * getResources().getColor(...)
     * 
     * @param colors
     */
    public void setColors(int[] colors, Context c) {

        ArrayList<Integer> clrs = new ArrayList<Integer>();

        for (int color : colors) {
            clrs.add(c.getResources().getColor(color));
        }

        mColors = clrs;
    }

    /**
     * Adds a new color to the colors array of the DataSet.
     * 
     * @param color
     */
    public void addColor(int color) {
        if (mColors == null)
            mColors = new ArrayList<Integer>();
        mColors.add(color);
    }

    /**
     * Sets the one and ONLY color that should be used for this DataSet.
     * Internally, this recreates the colors array and adds the specified color.
     * 
     * @param color
     */
    public void setColor(int color) {
        resetColors();
        mColors.add(color);
    }

    /**
     * returns all the colors that are set for this DataSet
     * 
     * @return
     */
    public ArrayList<Integer> getColors() {
        return mColors;
    }

    /**
     * Returns the color at the given index of the DataSet's color array.
     * Performs a IndexOutOfBounds check by modulus.
     * 
     * @param index
     * @return
     */
    public int getColor(int index) {
        return mColors.get(index % mColors.size());
    }

    /**
     * Resets all colors of this DataSet and recreates the colors array.
     */
    public void resetColors() {
        mColors = new ArrayList<Integer>();
    }

    // /**
    // * Convenience method to create multiple DataSets of different types with
    // * various double value arrays. Each double array represents the data of
    // one
    // * DataSet with a type created by this method, starting at 0 (and
    // * incremented).
    // *
    // * @param yValues
    // * @return
    // */
    // public static ArrayList<DataSet> makeDataSets(ArrayList<Double[]>
    // yValues) {
    //
    // ArrayList<DataSet> dataSets = new ArrayList<DataSet>();
    //
    // for (int i = 0; i < yValues.size(); i++) {
    //
    // Double[] curValues = yValues.get(i);
    //
    // ArrayList<Entry> entries = new ArrayList<Entry>();
    //
    // for (int j = 0; j < curValues.length; j++) {
    // entries.add(new Entry(curValues[j].floatValue(), j));
    // }
    //
    // dataSets.add(new DataSet(entries, "DS " + i));
    // }
    //
    // return dataSets;
    // }
}
