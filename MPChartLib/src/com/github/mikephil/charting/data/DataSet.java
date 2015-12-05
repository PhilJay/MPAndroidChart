
package com.github.mikephil.charting.data;

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
     * maximum y-value in the y-value array
     */
    protected float mYMax = 0.0f;

    /**
     * the minimum y-value in the y-value array
     */
    protected float mYMin = 0.0f;


    /**
     * Creates a new DataSet object with the given values it represents. Also, a
     * label that describes the DataSet can be specified. The label can also be
     * used to retrieve the DataSet from a ChartData object.
     *
     * @param yVals
     * @param label
     */
    public DataSet(List<T> yVals, String label) {
        super(label);
        this.mYVals = yVals;

        if (mYVals == null)
            mYVals = new ArrayList<T>();

        calcMinMax(0, mYVals.size());
    }

    @Override
    public void calcMinMax(int start, int end) {

        if (mYVals == null)
            return;

        final int yValCount = mYVals.size();

        if (yValCount == 0)
            return;

        int endValue;

        if (end == 0 || end >= yValCount)
            endValue = yValCount - 1;
        else
            endValue = end;

        mYMin = Float.MAX_VALUE;
        mYMax = -Float.MAX_VALUE;

        for (int i = start; i <= endValue; i++) {

            T e = mYVals.get(i);

            if (e != null && !Float.isNaN(e.getVal())) {

                if (e.getVal() < mYMin)
                    mYMin = e.getVal();

                if (e.getVal() > mYMax)
                    mYMax = e.getVal();
            }
        }

        if (mYMin == Float.MAX_VALUE) {
            mYMin = 0.f;
            mYMax = 0.f;
        }
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

    /**
     * Returns the array of y-values that this DataSet represents.
     *
     * @return
     */
    public List<T> getYVals() {
        return mYVals;
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
        buffer.append("DataSet, label: " + (getLabel() == null ? "" : getLabel()) + ", entries: " + mYVals.size() + "\n");
        return buffer.toString();
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
            int closestIndex = getEntryIndex(e.getXIndex(), Rounding.UP);
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
            calcMinMax(0, mYVals.size());
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
            calcMinMax(0, mYVals.size());
        }

        return removed;
    }

    /**
     * Removes all values from this DataSet and recalculates min and max value.
     */
    public void clear() {
        mYVals.clear();
        notifyDataSetChanged();
    }

    @Override
    public boolean addEntry(T e) {

        if (e == null)
            return false;

        float val = e.getVal();

        List<T> yVals = getYVals();
        if (yVals == null) {
            yVals = new ArrayList<T>();
        }

        if (yVals.size() == 0) {
            mYMax = val;
            mYMin = val;
        } else {
            if (mYMax < val)
                mYMax = val;
            if (mYMin > val)
                mYMin = val;
        }

        // add the entry
        yVals.add(e);
        return true;
    }

    @Override
    public boolean removeEntry(T e) {

        if (e == null)
            return false;

        if (mYVals == null)
            return false;

        // remove the entry
        boolean removed = mYVals.remove(e);

        if (removed) {
            calcMinMax(0, mYVals.size());
        }

        return removed;
    }

    /**
     * Removes the Entry object that has the given xIndex from the DataSet.
     * Returns true if an Entry was removed, false if no Entry could be removed.
     *
     * @param xIndex
     */
    public boolean removeEntry(int xIndex) {

        T e = getEntryForXIndex(xIndex);
        return removeEntry(e);
    }

    /**
     * Checks if this DataSet contains the specified Entry. Returns true if so,
     * false if not. NOTE: Performance is pretty bad on this one, do not
     * over-use in performance critical situations.
     *
     * @param e
     * @return
     */
    public boolean contains(Entry e) {

        List<T> values = getYVals();

        for (Entry entry : values) {
            if (entry.equals(e))
                return true;
        }

        return false;
    }

    @Override
    public int getEntryIndex(Entry e) {
        return mYVals.indexOf(e);
//
//        for (int i = 0; i < mYVals.size(); i++) {
//            if (e.equalTo(mYVals.get(i)))
//                return i;
//        }
//
//        return -1;
    }

    @Override
    public T getEntryForXIndex(int xIndex, Rounding rounding) {

        int index = getEntryIndex(xIndex, rounding);
        if (index > -1)
            return mYVals.get(index);
        return null;
    }

    @Override
    public T getEntryForXIndex(int xIndex) {
        return getEntryForXIndex(xIndex, Rounding.CLOSEST);
    }

    @Override
    public T getEntryForIndex(int index) {
        return mYVals.get(index);
    }

    @Override
    public int getEntryIndex(int x, Rounding rounding) {

        int low = 0;
        int high = mYVals.size() - 1;
        int closest = -1;

        while (low <= high) {
            int m = (high + low) / 2;

            if (x == mYVals.get(m).getXIndex()) {
                while (m > 0 && mYVals.get(m - 1).getXIndex() == x)
                    m--;

                return m;
            }

            if (x > mYVals.get(m).getXIndex())
                low = m + 1;
            else
                high = m - 1;

            closest = m;
        }

        if (closest != -1) {
            int closestXIndex = mYVals.get(closest).getXIndex();
            if (rounding == Rounding.UP) {
                if (closestXIndex < x && closest < mYVals.size() - 1) {
                    ++closest;
                }
            } else if (rounding == Rounding.DOWN) {
                if (closestXIndex > x && closest > 0) {
                    --closest;
                }
            }
        }

        return closest;
    }

    @Override
    public float getYValForXIndex(int xIndex) {

        Entry e = getEntryForXIndex(xIndex);

        if (e != null && e.getXIndex() == xIndex)
            return e.getVal();
        else
            return Float.NaN;
    }

    /**
     * Determines how to round DataSet index values for
     * {@link DataSet#getEntryIndex(int, Rounding)} DataSet.getEntryIndex()}
     * when an exact x-index is not found.
     */
    public enum Rounding {
        UP,
        DOWN,
        CLOSEST,
    }
}
