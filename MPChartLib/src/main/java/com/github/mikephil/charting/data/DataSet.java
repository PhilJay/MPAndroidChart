
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
     * the entries that this DataSet represents / holds together
     */
    protected List<T> mValues = null;

    /**
     * maximum y-value in the value array
     */
    protected float mYMax = -Float.MAX_VALUE;

    /**
     * minimum y-value in the value array
     */
    protected float mYMin = Float.MAX_VALUE;

    /**
     * maximum x-value in the value array
     */
    protected float mXMax = -Float.MAX_VALUE;

    /**
     * minimum x-value in the value array
     */
    protected float mXMin = Float.MAX_VALUE;


    /**
     * Creates a new DataSet object with the given values (entries) it represents. Also, a
     * label that describes the DataSet can be specified. The label can also be
     * used to retrieve the DataSet from a ChartData object.
     *
     * @param values
     * @param label
     */
    public DataSet(List<T> values, String label) {
        super(label);
        this.mValues = values;

        if (mValues == null)
            mValues = new ArrayList<T>();

        calcMinMax();
    }

    @Override
    public void calcMinMax() {

        if (mValues == null || mValues.isEmpty())
            return;

        mYMax = -Float.MAX_VALUE;
        mYMin = Float.MAX_VALUE;
        mXMax = -Float.MAX_VALUE;
        mXMin = Float.MAX_VALUE;

        for (T e : mValues) {
            calcMinMax(e);
        }
    }

    /**
     * Updates the min and max x and y value of this DataSet based on the given Entry.
     *
     * @param e
     */
    protected void calcMinMax(T e) {

        if (e == null)
            return;

        if (e.getY() < mYMin)
            mYMin = e.getY();

        if (e.getY() > mYMax)
            mYMax = e.getY();

        if (e.getX() < mXMin)
            mXMin = e.getX();

        if (e.getX() > mXMax)
            mXMax = e.getX();
    }

    @Override
    public int getEntryCount() {
        return mValues.size();
    }

    /**
     * Returns the array of entries that this DataSet represents.
     *
     * @return
     */
    public List<T> getValues() {
        return mValues;
    }

    /**
     * Sets the array of entries that this DataSet represents, and calls notifyDataSetChanged()
     *
     * @return
     */
    public void setValues(List<T> values) {
        mValues = values;
        notifyDataSetChanged();
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
        for (int i = 0; i < mValues.size(); i++) {
            buffer.append(mValues.get(i).toString() + " ");
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
        buffer.append("DataSet, label: " + (getLabel() == null ? "" : getLabel()) + ", entries: " + mValues.size() +
                "\n");
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

    @Override
    public float getXMin() {
        return mXMin;
    }

    @Override
    public float getXMax() {
        return mXMax;
    }

    @Override
    public void addEntryOrdered(T e) {

        if (e == null)
            return;

        if (mValues == null) {
            mValues = new ArrayList<T>();
        }

        calcMinMax(e);

        if (mValues.size() > 0 && mValues.get(mValues.size() - 1).getX() > e.getX()) {
            int closestIndex = getEntryIndex(e.getX(), Rounding.UP);
            mValues.add(closestIndex, e);
        } else {
            mValues.add(e);
        }
    }

    @Override
    public void clear() {
        mValues.clear();
        notifyDataSetChanged();
    }

    @Override
    public boolean addEntry(T e) {

        if (e == null)
            return false;

        List<T> values = getValues();
        if (values == null) {
            values = new ArrayList<T>();
        }

        calcMinMax(e);

        // add the entry
        return values.add(e);
    }

    @Override
    public boolean removeEntry(T e) {

        if (e == null)
            return false;

        if (mValues == null)
            return false;

        // remove the entry
        boolean removed = mValues.remove(e);

        if (removed) {
            calcMinMax();
        }

        return removed;
    }

    @Override
    public int getEntryIndex(Entry e) {
        return mValues.indexOf(e);
    }

    @Override
    public T getEntryForXPos(float xPos, Rounding rounding) {

        int index = getEntryIndex(xPos, rounding);
        if (index > -1)
            return mValues.get(index);
        return null;
    }

    @Override
    public T getEntryForXPos(float xPos) {
        return getEntryForXPos(xPos, Rounding.CLOSEST);
    }

    @Override
    public T getEntryForIndex(int index) {
        return mValues.get(index);
    }

    @Override
    public int getEntryIndex(float xPos, Rounding rounding) {

        if (mValues == null || mValues.isEmpty())
            return -1;

        int low = 0;
        int high = mValues.size() - 1;

        while (low < high) {
            int m = (low + high) / 2;

            float d1 = Math.abs(mValues.get(m).getX() - xPos);
            float d2 = Math.abs(mValues.get(m + 1).getX() - xPos);

            if (d2 <= d1) {
                low = m + 1;
            } else {
                high = m;
            }
        }

        if (high != -1) {
            float closestXPos = mValues.get(high).getX();
            if (rounding == Rounding.UP) {
                if (closestXPos < xPos && high < mValues.size() - 1) {
                    ++high;
                }
            } else if (rounding == Rounding.DOWN) {
                if (closestXPos > xPos && high > 0) {
                    --high;
                }
            }
        }

        return high;
    }

    /**
     * Returns all Entry objects at the given xIndex. INFORMATION: This method
     * does calculations at runtime. Do not over-use in performance critical
     * situations.
     *
     * @param xVal
     * @return
     */
    @Override
    public List<T> getEntriesForXPos(float xVal) {

        List<T> entries = new ArrayList<T>();

        int low = 0;
        int high = mValues.size() - 1;

        while (low <= high) {
            int m = (high + low) / 2;
            T entry = mValues.get(m);

            if (xVal == entry.getX()) {
                while (m > 0 && mValues.get(m - 1).getX() == xVal)
                    m--;

                high = mValues.size();
                for (; m < high; m++) {
                    entry = mValues.get(m);
                    if (entry.getX() == xVal) {
                        entries.add(entry);
                    } else {
                        break;
                    }
                }

                break;
            } else {
                if (xVal > entry.getX())
                    low = m + 1;
                else
                    high = m - 1;
            }
        }

        return entries;
    }

    /**
     * Determines how to round DataSet index values for
     * {@link DataSet#getEntryIndex(float, Rounding)} DataSet.getEntryIndex()}
     * when an exact x-index is not found.
     */
    public enum Rounding {
        UP,
        DOWN,
        CLOSEST,
    }
}