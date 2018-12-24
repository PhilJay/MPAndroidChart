
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

    @Override
    public void calcMinMaxY(float fromX, float toX) {

        if (mValues == null || mValues.isEmpty())
            return;

        mYMax = -Float.MAX_VALUE;
        mYMin = Float.MAX_VALUE;

        int indexFrom = getEntryIndex(fromX, Float.NaN, Rounding.DOWN);
        int indexTo = getEntryIndex(toX, Float.NaN, Rounding.UP);

        for (int i = indexFrom; i <= indexTo; i++) {

            // only recalculate y
            calcMinMaxY(mValues.get(i));
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

        calcMinMaxX(e);

        calcMinMaxY(e);
    }

    protected void calcMinMaxX(T e) {

        if (e.getX() < mXMin)
            mXMin = e.getX();

        if (e.getX() > mXMax)
            mXMax = e.getX();
    }

    protected void calcMinMaxY(T e) {

        if (e.getY() < mYMin)
            mYMin = e.getY();

        if (e.getY() > mYMax)
            mYMax = e.getY();
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

    /**
     *
     * @param dataSet
     */
    protected void copy(DataSet dataSet) {
        super.copy(dataSet);
    }

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
            int closestIndex = getEntryIndex(e.getX(), e.getY(), Rounding.UP);
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
    public T getEntryForXValue(float xValue, float closestToY, Rounding rounding) {

        int index = getEntryIndex(xValue, closestToY, rounding);
        if (index > -1)
            return mValues.get(index);
        return null;
    }

    @Override
    public T getEntryForXValue(float xValue, float closestToY) {
        return getEntryForXValue(xValue, closestToY, Rounding.CLOSEST);
    }

    @Override
    public T getEntryForIndex(int index) {
        return mValues.get(index);
    }

    @Override
    public int getEntryIndex(float xValue, float closestToY, Rounding rounding) {

        if (mValues == null || mValues.isEmpty())
            return -1;

        int closest = -1;

        float minDistance = Float.MAX_VALUE;
        for (int i = 0; i < mValues.size(); i++) {
            float val = mValues.get(i).getX();
            float distance = Math.abs(val - xValue);
            if (distance < minDistance) {
                switch (rounding) {
                    case CLOSEST: {
                        minDistance = distance;
                        closest = i;
                        break;
                    }
                    case UP: {
                        if (val >= xValue) {
                            minDistance = distance;
                            closest = i;
                        }
                        break;
                    }
                    case DOWN: {
                        if (val <= xValue) {
                            minDistance = distance;
                            closest = i;
                        }
                        break;
                    }
                }
            }
        }

        if (closest != -1) {
            float closestXValue = mValues.get(closest).getX();

            // Search by closest to y-value
            if (!Float.isNaN(closestToY)) {
                while (closest > 0 && mValues.get(closest - 1).getX() == closestXValue)
                    closest -= 1;

                float closestYValue = mValues.get(closest).getY();
                int closestYIndex = closest;

                while (true) {
                    closest += 1;
                    if (closest >= mValues.size())
                        break;

                    final Entry value = mValues.get(closest);

                    if (value.getX() != closestXValue)
                        break;

                    if (Math.abs(value.getY() - closestToY) < Math.abs(closestYValue - closestToY)) {
                        closestYValue = value.getY();
                        closestYIndex = closest;
                    }
                }

                closest = closestYIndex;
            }
        }

        return closest;
    }

    @Override
    public List<T> getEntriesForXValue(float xValue) {
        List<T> entries = new ArrayList<T>();

        for (T entry : mValues) {
            if (entry.getX() == xValue) {
                entries.add(entry);
            }
        }
        return entries;
    }

    /**
     * Determines how to round DataSet index values for
     * {@link DataSet#getEntryIndex(float, float, Rounding)} DataSet.getEntryIndex()}
     * when an exact x-index is not found.
     */
    public enum Rounding {
        UP,
        DOWN,
        CLOSEST,
    }
}