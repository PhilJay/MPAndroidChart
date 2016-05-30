
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
    protected List<T> mValues = null;

    /**
     * maximum y-value in the value array
     */
    protected float mYMax = 0.0f;

    /**
     * minimum y-value in the value array
     */
    protected float mYMin = 0.0f;

    /**
     * maximum x-value in the value array
     */
    protected float mXMax = 0.0f;

    /**
     * minimum x-value in the value array
     */
    protected float mXMin = 0.0f;


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
        this.mValues = yVals;

        if (mValues == null)
            mValues = new ArrayList<T>();

        calcMinMax();
    }

    @Override
    public void calcMinMax() {

        if (mValues == null)
            return;

        if (mValues.size() == 0)
            return;

        mYMin = Float.MAX_VALUE;
        mYMax = -Float.MAX_VALUE;

        mXMin = Float.MAX_VALUE;
        mXMax = -Float.MAX_VALUE;

        for (T e : mValues) {

            if (e != null && !Float.isNaN(e.getY())) {

                if (e.getY() < mYMin)
                    mYMin = e.getY();

                if (e.getY() > mYMax)
                    mYMax = e.getY();

                if (e.getX() < mXMin)
                    mXMin = e.getX();

                if (e.getX() > mXMax)
                    mXMax = e.getX();
            }
        }

        if (mYMin == Float.MAX_VALUE) {
            mYMin = 0.f;
            mYMax = 0.f;
        }

        if(mXMin == Float.MAX_VALUE) {
            mXMin = 0.f;
            mXMax = 0.f;
        }
    }

    @Override
    public int getEntryCount() {
        return mValues.size();
    }

    /**
     * Returns the array of y-values that this DataSet represents.
     *
     * @return
     */
    public List<T> getYVals() {
        return mValues;
    }

    /**
     * Sets the array of y-values that this DataSet represents, and calls notifyDataSetChanged()
     *
     * @return
     */
    public void setYVals(List<T> yVals) {
        mValues = yVals;
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

        float val = e.getY();

        if (mValues == null) {
            mValues = new ArrayList<T>();
        }

        if (mValues.size() == 0) {
            mYMax = val;
            mYMin = val;
        } else {
            if (mYMax < val)
                mYMax = val;
            if (mYMin > val)
                mYMin = val;
        }

        if (mValues.size() > 0 && mValues.get(mValues.size() - 1).getX() > e.getX()) {
            int closestIndex = getEntryIndex(e.getX(), Rounding.UP);
            mValues.add(closestIndex, e);
            return;
        }

        mValues.add(e);
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

        float val = e.getY();

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

        int low = 0;
        int high = mValues.size() - 1;
        int closest = -1;

        while (low <= high) {
            int m = (high + low) / 2;

            if (xPos == mValues.get(m).getX()) {
                while (m > 0 && mValues.get(m - 1).getX() == xPos)
                    m--;

                return m;
            }

            if (xPos > mValues.get(m).getX())
                low = m + 1;
            else
                high = m - 1;

            closest = m;
        }

        if (closest != -1) {
            float closestXPos = mValues.get(closest).getX();
            if (rounding == Rounding.UP) {
                if (closestXPos < xPos && closest < mValues.size() - 1) {
                    ++closest;
                }
            } else if (rounding == Rounding.DOWN) {
                if (closestXPos > xPos && closest > 0) {
                    --closest;
                }
            }
        }

        return closest;
    }

    @Override
    public float getYValForXIndex(int xIndex) {

        Entry e = getEntryForXPos(xIndex);

        if (e != null && e.getX() == xIndex)
            return e.getY();
        else
            return Float.NaN;
    }

    @Override
    public float[] getYValsForXIndex(int xIndex) {

        List<T> entries = getEntriesForXIndex(xIndex);

        float[] yVals = new float[entries.size()];
        int i = 0;

        for (T e : entries)
            yVals[i++] = e.getY();

        return yVals;
    }

    /**
     * Returns all Entry objects at the given xIndex. INFORMATION: This method
     * does calculations at runtime. Do not over-use in performance critical
     * situations.
     *
     * @param xIndex
     * @return
     */
    @Override
    public List<T> getEntriesForXIndex(int xIndex) {

        List<T> entries = new ArrayList<T>();

        int low = 0;
        int high = mValues.size() - 1;

        while (low <= high) {
            int m = (high + low) / 2;
            T entry = mValues.get(m);

            if (xIndex == entry.getX()) {
                while (m > 0 && mValues.get(m - 1).getX() == xIndex)
                    m--;

                high = mValues.size();
                for (; m < high; m++) {
                    entry = mValues.get(m);
                    if (entry.getX() == xIndex) {
                        entries.add(entry);
                    } else {
                        break;
                    }
                }

                break;
            } else {
                if (xIndex > entry.getX())
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