
package com.github.mikephil.charting.data;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;

import com.github.mikephil.charting.components.YAxis.AxisDependency;
import com.github.mikephil.charting.interfaces.datainterfaces.datasets.IDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.formatter.DefaultValueFormatter;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.util.ArrayList;
import java.util.List;

/**
 * The DataSet class represents one group or type of entries (Entry) in the
 * Chart that belong together. It is designed to logically separate different
 * groups of values inside the Chart (e.g. the values for a specific line in the
 * LineChart, or the values of a specific group of bars in the BarChart).
 *
 * @author Philipp Jahoda
 */
public abstract class DataSet<T extends Entry> extends BaseDataSet<T> {

    /**
     * the entries that this dataset represents / holds together
     */
    protected List<T> mYVals = null;

    /**
     * List representing all colors that are used for this DataSet
     */
    protected List<Integer> mColors = null;

    /**
     * label that describes the DataSet or the data the DataSet represents
     */
    private String mLabel = "DataSet";

    /**
     * this specifies which axis this DataSet should be plotted against
     */
    protected AxisDependency mAxisDependency = AxisDependency.LEFT;


    /**
     * Creates a new DataSet object with the given values it represents. Also, a
     * label that describes the DataSet can be specified. The label can also be
     * used to retrieve the DataSet from a ChartData object.
     *
     * @param yVals
     * @param label
     */
    public DataSet(List<T> yVals, String label) {

        this.mLabel = label;
        this.mYVals = yVals;

        if (mYVals == null)
            mYVals = new ArrayList<T>();

        mColors = new ArrayList<Integer>();

        // default color
        mColors.add(Color.rgb(140, 234, 255));

        calcMinMax(mYVals, mLastStart, mLastEnd);
    }

    /**
     * Use this method to tell the data set that the underlying data has changed
     */
    public void notifyDataSetChanged() {
        calcMinMax(mYVals, mLastStart, mLastEnd);
    }

    @Override
    public int getEntryCount() {
        return mYVals.size();
    }

    /**
     * Returns all Entry objects at the given xIndex. INFORMATION: This method
     * does calculations at runtime. Do not over-use in performance critical
     * situations.
     *
     * @param x
     * @return
     */
    public List<T> getEntriesForXIndex(int x) {

        List<T> entries = new ArrayList<T>();

        int low = 0;
        int high = mYVals.size() - 1;

        while (low <= high) {
            int m = (high + low) / 2;
            T entry = mYVals.get(m);

            if (x == entry.getXIndex()) {
                while (m > 0 && mYVals.get(m - 1).getXIndex() == x)
                    m--;

                high = mYVals.size();
                for (; m < high; m++) {
                    entry = mYVals.get(m);
                    if (entry.getXIndex() == x) {
                        entries.add(entry);
                    } else {
                        break;
                    }
                }
            }

            if (x > entry.getXIndex())
                low = m + 1;
            else
                high = m - 1;
        }

        return entries;
    }

    @Override
    public List<T> getYVals() {
        return mYVals;
    }

    @Override
    public float getYMin() {
        return mYMin;
    }

    @Override
    public float getYMax() {
        return mYMax;
    }

    /**
     * Returns the number of entries this DataSet holds.
     *
     * @return
     */
    public int getValueCount() {
        return mYVals.size();
    }

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
    public abstract DataSet<T> copy();

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
        buffer.append("DataSet, label: " + (mLabel == null ? "" : mLabel) + ", entries: " + mYVals.size() + "\n");
        return buffer.toString();
    }

    /**
     * Sets the label string that describes the DataSet.
     *
     * @return
     */
    public void setLabel(String label) {
        mLabel = label;
    }

    @Override
    public String getLabel() {
        return mLabel;
    }

    @Override
    public AxisDependency getAxisDependency() {
        return mAxisDependency;
    }

    /**
     * Set the y-axis this DataSet should be plotted against (either LEFT or
     * RIGHT). Default: LEFT
     *
     * @param dependency
     */
    public void setAxisDependency(AxisDependency dependency) {
        mAxisDependency = dependency;
    }

    /**
     * Adds an Entry to the DataSet dynamically.
     * Entries are added to their appropriate index respective to it's x-index.
     * This will also recalculate the current minimum and maximum
     * values of the DataSet and the value-sum.
     *
     * @param e
     */
    @SuppressWarnings("unchecked")
    public void addEntryOrdered(Entry e) {

        if (e == null)
            return;

        float val = e.getVal();

        if (mYVals == null) {
            mYVals = new ArrayList<T>();
        }

        if (mYVals.size() == 0) {
            mYMax = val;
            mYMin = val;
        } else {
            if (mYMax < val)
                mYMax = val;
            if (mYMin > val)
                mYMin = val;
        }

        if (mYVals.size() > 0 && mYVals.get(mYVals.size() - 1).getXIndex() > e.getXIndex()) {
            int closestIndex = getEntryIndex(e.getXIndex());
            if (mYVals.get(closestIndex).getXIndex() < e.getXIndex())
                closestIndex++;
            mYVals.add(closestIndex, (T) e);
            return;
        }

        mYVals.add((T) e);
    }

    /**
     * Removes the first Entry (at index 0) of this DataSet from the entries array.
     * Returns true if successful, false if not.
     *
     * @return
     */
    public boolean removeFirst() {

        T entry = mYVals.remove(0);

        boolean removed = entry != null;

        if (removed) {
            calcMinMax(mYVals, mLastStart, mLastEnd);
        }

        return removed;
    }

    /**
     * Removes the last Entry (at index size-1) of this DataSet from the entries array.
     * Returns true if successful, false if not.
     *
     * @return
     */
    public boolean removeLast() {

        if (mYVals.size() <= 0)
            return false;

        T entry = mYVals.remove(mYVals.size() - 1);

        boolean removed = entry != null;

        if (removed) {
            calcMinMax(mYVals, mLastStart, mLastEnd);
        }

        return removed;
    }

    /** BELOW THIS COLOR HANDLING */

    /**
     * Sets the colors that should be used fore this DataSet. Colors are reused
     * as soon as the number of Entries the DataSet represents is higher than
     * the size of the colors array. If you are using colors from the resources,
     * make sure that the colors are already prepared (by calling
     * getResources().getColor(...)) before adding them to the DataSet.
     *
     * @param colors
     */
    public void setColors(List<Integer> colors) {
        this.mColors = colors;
    }

    /**
     * Sets the colors that should be used fore this DataSet. Colors are reused
     * as soon as the number of Entries the DataSet represents is higher than
     * the size of the colors array. If you are using colors from the resources,
     * make sure that the colors are already prepared (by calling
     * getResources().getColor(...)) before adding them to the DataSet.
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

        List<Integer> clrs = new ArrayList<Integer>();

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

    @Override
    public List<Integer> getColors() {
        return mColors;
    }

    @Override
    public int getColor() {
        return mColors.get(0);
    }

    @Override
    public int getColor(int index) {
        return mColors.get(index % mColors.size());
    }

    /**
     * Resets all colors of this DataSet and recreates the colors array.
     */
    public void resetColors() {
        mColors = new ArrayList<Integer>();
    }

    @Override
    public boolean contains(Entry e) {

        for (Entry entry : mYVals) {
            if (entry.equals(e))
                return true;
        }

        return false;
    }

    /**
     * Removes all values from this DataSet and recalculates min and max value.
     */
    public void clear() {
        mYVals.clear();
        mLastStart = 0;
        mLastEnd = 0;
        notifyDataSetChanged();
    }
}
