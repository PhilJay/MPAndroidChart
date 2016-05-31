
package com.github.mikephil.charting.data;

import android.graphics.Typeface;
import android.util.Log;

import com.github.mikephil.charting.components.YAxis.AxisDependency;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.IDataSet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Class that holds all relevant data that represents the chart. That involves
 * at least one (or more) DataSets, and an array of xPx-values.
 *
 * @author Philipp Jahoda
 */
public abstract class ChartData<T extends IDataSet<? extends Entry>> {

    /**
     * maximum yPx-yValue in the yValue array across all axes
     */
    protected float mYMax = 0.0f;

    /**
     * the minimum yPx-yValue in the yValue array across all axes
     */
    protected float mYMin = 0.0f;

    /**
     * maximum xPx-yValue in the yValue array
     */
    protected float mXMax = 0f;

    /**
     * minimum xPx-yValue in the yValue array
     */
    protected float mXMin = 0f;

    protected float mLeftAxisMax = 0.0f;

    protected float mLeftAxisMin = 0.0f;

    protected float mRightAxisMax = 0.0f;

    protected float mRightAxisMin = 0.0f;

    /**
     * total number of yPx-values across all DataSet objects
     */
    private int mYValCount = 0;

    /**
     * contains the maximum length (in characters) an entry in the xPx-vals array
     * has
     */
    private float mXValMaximumLength = 0;

    /**
     * array that holds all DataSets the ChartData object represents
     */
    protected List<T> mDataSets;

    public ChartData() {
        mDataSets = new ArrayList<T>();
    }

    public ChartData(T... dataSets) {
        mDataSets = Arrays.asList(dataSets);
        init();
    }

    /**
     * constructor for chart data
     *
     * @param sets the dataset array
     */
    public ChartData(List<T> sets) {
        this.mDataSets = sets;
        init();
    }

    /**
     * performs all kinds of initialization calculations, such as min-max and
     * yValue count and sum
     */
    protected void init() {

        calcYValueCount();
        calcMinMax();
    }

    /**
     * Call this method to let the ChartData know that the underlying data has
     * changed. Calling this performs all necessary recalculations needed when
     * the contained data has changed.
     */
    public void notifyDataChanged() {
        init();
    }

    /**
     * calc minimum and maximum values (both xPx and yPx) over all DataSets
     */
    public void calcMinMax() {

        if (mDataSets == null || mDataSets.size() < 1) {

            mYMax = 0f;
            mYMin = 0f;

            mXMax = 0f;
            mXMin = 0f;

        } else {

            mYMin = Float.MAX_VALUE;
            mYMax = -Float.MAX_VALUE;

            mXMin = Float.MAX_VALUE;
            mXMax = -Float.MAX_VALUE;

            for (int i = 0; i < mDataSets.size(); i++) {

                IDataSet set = mDataSets.get(i);
                set.calcMinMax();

                if (set.getYMin() < mYMin)
                    mYMin = set.getYMin();

                if (set.getYMax() > mYMax)
                    mYMax = set.getYMax();

                if (set.getXMin() < mXMin)
                    mXMin = set.getXMin();

                if (set.getXMax() > mXMax)
                    mXMax = set.getXMax();
            }

            if (mYMin == Float.MAX_VALUE) {
                mYMin = 0.f;
                mYMax = 0.f;
            }

            // left axis
            T firstLeft = getFirstLeft();

            if (firstLeft != null) {

                mLeftAxisMax = firstLeft.getYMax();
                mLeftAxisMin = firstLeft.getYMin();

                for (IDataSet dataSet : mDataSets) {
                    if (dataSet.getAxisDependency() == AxisDependency.LEFT) {
                        if (dataSet.getYMin() < mLeftAxisMin)
                            mLeftAxisMin = dataSet.getYMin();

                        if (dataSet.getYMax() > mLeftAxisMax)
                            mLeftAxisMax = dataSet.getYMax();
                    }
                }
            }

            // right axis
            T firstRight = getFirstRight();

            if (firstRight != null) {

                mRightAxisMax = firstRight.getYMax();
                mRightAxisMin = firstRight.getYMin();

                for (IDataSet dataSet : mDataSets) {
                    if (dataSet.getAxisDependency() == AxisDependency.RIGHT) {
                        if (dataSet.getYMin() < mRightAxisMin)
                            mRightAxisMin = dataSet.getYMin();

                        if (dataSet.getYMax() > mRightAxisMax)
                            mRightAxisMax = dataSet.getYMax();
                    }
                }
            }

            // in case there is only one axis, adjust the second axis
            handleEmptyAxis(firstLeft, firstRight);
        }
    }

    /**
     * Calculates the total number of yPx-values across all DataSets the ChartData
     * represents.
     *
     * @return
     */
    protected void calcYValueCount() {

        mYValCount = 0;

        if (mDataSets == null)
            return;

        int count = 0;

        for (int i = 0; i < mDataSets.size(); i++) {
            count += mDataSets.get(i).getEntryCount();
        }

        mYValCount = count;
    }

    /** ONLY GETTERS AND SETTERS BELOW THIS */

    /**
     * returns the number of LineDataSets this object contains
     *
     * @return
     */
    public int getDataSetCount() {
        if (mDataSets == null)
            return 0;
        return mDataSets.size();
    }

    /**
     * Returns the smallest yPx-yValue the data object contains.
     *
     * @return
     */
    public float getYMin() {
        return mYMin;
    }

    /**
     * Returns the minimum yPx-yValue for the specified axis.
     *
     * @param axis
     * @return
     */
    public float getYMin(AxisDependency axis) {
        if (axis == AxisDependency.LEFT)
            return mLeftAxisMin;
        else
            return mRightAxisMin;
    }

    /**
     * Returns the greatest yPx-yValue the data object contains.
     *
     * @return
     */
    public float getYMax() {
        return mYMax;
    }

    /**
     * Returns the maximum yPx-yValue for the specified axis.
     *
     * @param axis
     * @return
     */
    public float getYMax(AxisDependency axis) {
        if (axis == AxisDependency.LEFT)
            return mLeftAxisMax;
        else
            return mRightAxisMax;
    }

    /**
     * Returns the minimum xPx-yValue this data object contains.
     *
     * @return
     */
    public float getXMin() {
        return mXMin;
    }

    /**
     * Returns the maximum xPx-yValue this data object contains.
     *
     * @return
     */
    public float getXMax() {
        return mXMax;
    }

    /**
     * returns the maximum length (in characters) across all values in the
     * xPx-vals array
     *
     * @return
     */
    public float getXValMaximumLength() {
        return mXValMaximumLength;
    }

    /**
     * Returns the total number of yPx-values across all DataSet objects the this
     * object represents.
     *
     * @return
     */
    public int getYValCount() {
        return mYValCount;
    }

    public List<T> getDataSets() {
        return mDataSets;
    }

    /**
     * Retrieve the index of a DataSet with a specific label from the ChartData.
     * Search can be case sensitive or not. IMPORTANT: This method does
     * calculations at runtime, do not over-use in performance critical
     * situations.
     *
     * @param dataSets   the DataSet array to search
     * @param label
     * @param ignorecase if true, the search is not case-sensitive
     * @return
     */
    protected int getDataSetIndexByLabel(List<T> dataSets, String label,
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
        if (highlight.getDataSetIndex() >= mDataSets.size())
            return null;
        else {
            return mDataSets.get(highlight.getDataSetIndex()).getEntryForXPos(highlight.getX());
        }
    }
//    public Entry getEntryForHighlight(Highlight highlight) {
//        if (highlight.getDataSetIndex() >= mDataSets.size())
//            return null;
//        else {
//            // The yValue of the highlighted entry could be NaN -
//            //   if we are not interested in highlighting a specific yValue.
//
//            List<?> entries = mDataSets.get(highlight.getDataSetIndex())
//                    .getEntriesForXPos(highlight.getX());
//            for (Object entry : entries)
//                if (((Entry)entry).getY() == highlight.getY() ||
//                        Float.isNaN(highlight.getY()))
//                    return (Entry)entry;
//
//            return null;
//        }
//    }

    /**
     * Returns the DataSet object with the given label. Search can be case
     * sensitive or not. IMPORTANT: This method does calculations at runtime.
     * Use with care in performance critical situations.
     *
     * @param label
     * @param ignorecase
     * @return
     */
    public T getDataSetByLabel(String label, boolean ignorecase) {

        int index = getDataSetIndexByLabel(mDataSets, label, ignorecase);

        if (index < 0 || index >= mDataSets.size())
            return null;
        else
            return mDataSets.get(index);
    }

    public T getDataSetByIndex(int index) {

        if (mDataSets == null || index < 0 || index >= mDataSets.size())
            return null;

        return mDataSets.get(index);
    }

    /**
     * Adds a DataSet dynamically.
     *
     * @param d
     */
    public void addDataSet(T d) {

        if (d == null)
            return;

        mYValCount += d.getEntryCount();

        if (mDataSets.size() <= 0) {

            mYMax = d.getYMax();
            mYMin = d.getYMin();

            if (d.getAxisDependency() == AxisDependency.LEFT) {

                mLeftAxisMax = d.getYMax();
                mLeftAxisMin = d.getYMin();
            } else {
                mRightAxisMax = d.getYMax();
                mRightAxisMin = d.getYMin();
            }
        } else {

            if (mYMax < d.getYMax())
                mYMax = d.getYMax();
            if (mYMin > d.getYMin())
                mYMin = d.getYMin();

            if (d.getAxisDependency() == AxisDependency.LEFT) {

                if (mLeftAxisMax < d.getYMax())
                    mLeftAxisMax = d.getYMax();
                if (mLeftAxisMin > d.getYMin())
                    mLeftAxisMin = d.getYMin();
            } else {
                if (mRightAxisMax < d.getYMax())
                    mRightAxisMax = d.getYMax();
                if (mRightAxisMin > d.getYMin())
                    mRightAxisMin = d.getYMin();
            }
        }

        mDataSets.add(d);

        handleEmptyAxis(getFirstLeft(), getFirstRight());
    }

    /**
     * This adjusts the other axis if one axis is empty and the other is not.
     *
     * @param firstLeft
     * @param firstRight
     */
    private void handleEmptyAxis(T firstLeft, T firstRight) {

        // in case there is only one axis, adjust the second axis
        if (firstLeft == null) {
            mLeftAxisMax = mRightAxisMax;
            mLeftAxisMin = mRightAxisMin;
        } else if (firstRight == null) {
            mRightAxisMax = mLeftAxisMax;
            mRightAxisMin = mLeftAxisMin;
        }
    }

    /**
     * Removes the given DataSet from this data object. Also recalculates all
     * minimum and maximum values. Returns true if a DataSet was removed, false
     * if no DataSet could be removed.
     *
     * @param d
     */
    public boolean removeDataSet(T d) {

        if (d == null)
            return false;

        boolean removed = mDataSets.remove(d);

        // if a DataSet was removed
        if (removed) {

            mYValCount -= d.getEntryCount();

            calcMinMax();
        }

        return removed;
    }

    /**
     * Removes the DataSet at the given index in the DataSet array from the data
     * object. Also recalculates all minimum and maximum values. Returns true if
     * a DataSet was removed, false if no DataSet could be removed.
     *
     * @param index
     */
    public boolean removeDataSet(int index) {

        if (index >= mDataSets.size() || index < 0)
            return false;

        T set = mDataSets.get(index);
        return removeDataSet(set);
    }

    /**
     * Adds an Entry to the DataSet at the specified index.
     * Entries are added to the end of the list.
     *
     * @param e
     * @param dataSetIndex
     */
    public void addEntry(Entry e, int dataSetIndex) {

        if (mDataSets.size() > dataSetIndex && dataSetIndex >= 0) {

            IDataSet set = mDataSets.get(dataSetIndex);
            // add the entry to the dataset
            if (!set.addEntry(e))
                return;

            float val = e.getY();

            if (mYValCount == 0) {
                mYMin = val;
                mYMax = val;

                if (set.getAxisDependency() == AxisDependency.LEFT) {

                    mLeftAxisMax = e.getY();
                    mLeftAxisMin = e.getY();
                } else {
                    mRightAxisMax = e.getY();
                    mRightAxisMin = e.getY();
                }
            } else {

                if (mYMax < val)
                    mYMax = val;
                if (mYMin > val)
                    mYMin = val;

                if (set.getAxisDependency() == AxisDependency.LEFT) {

                    if (mLeftAxisMax < e.getY())
                        mLeftAxisMax = e.getY();
                    if (mLeftAxisMin > e.getY())
                        mLeftAxisMin = e.getY();
                } else {
                    if (mRightAxisMax < e.getY())
                        mRightAxisMax = e.getY();
                    if (mRightAxisMin > e.getY())
                        mRightAxisMin = e.getY();
                }
            }

            mYValCount += 1;

            handleEmptyAxis(getFirstLeft(), getFirstRight());

        } else {
            Log.e("addEntry", "Cannot add Entry because dataSetIndex too high or too low.");
        }
    }

    /**
     * Removes the given Entry object from the DataSet at the specified index.
     *
     * @param e
     * @param dataSetIndex
     */
    public boolean removeEntry(Entry e, int dataSetIndex) {

        // entry null, outofbounds
        if (e == null || dataSetIndex >= mDataSets.size())
            return false;

        IDataSet set = mDataSets.get(dataSetIndex);

        if (set != null) {
            // remove the entry from the dataset
            boolean removed = set.removeEntry(e);

            if (removed) {
                mYValCount -= 1;

                calcMinMax();
            }

            return removed;
        } else
            return false;
    }

    /**
     * Removes the Entry object at the given xIndex from the DataSet at the
     * specified index. Returns true if an Entry was removed, false if no Entry
     * was found that meets the specified requirements.
     *
     * @param xIndex
     * @param dataSetIndex
     * @return
     */
    public boolean removeEntry(int xIndex, int dataSetIndex) {

        if (dataSetIndex >= mDataSets.size())
            return false;

        IDataSet dataSet = mDataSets.get(dataSetIndex);
        Entry e = dataSet.getEntryForXPos(xIndex);

        if (e == null || e.getX() != xIndex)
            return false;

        return removeEntry(e, dataSetIndex);
    }

    /**
     * Returns the DataSet that contains the provided Entry, or null, if no
     * DataSet contains this Entry.
     *
     * @param e
     * @return
     */
    public T getDataSetForEntry(Entry e) {

        if (e == null)
            return null;

        for (int i = 0; i < mDataSets.size(); i++) {

            T set = mDataSets.get(i);

            for (int j = 0; j < set.getEntryCount(); j++) {
                if (e.equalTo(set.getEntryForXPos(e.getX())))
                    return set;
            }
        }

        return null;
    }

    /**
     * Returns all colors used across all DataSet objects this object
     * represents.
     *
     * @return
     */
    public int[] getColors() {

        if (mDataSets == null)
            return null;

        int clrcnt = 0;

        for (int i = 0; i < mDataSets.size(); i++) {
            clrcnt += mDataSets.get(i).getColors().size();
        }

        int[] colors = new int[clrcnt];
        int cnt = 0;

        for (int i = 0; i < mDataSets.size(); i++) {

            List<Integer> clrs = mDataSets.get(i).getColors();

            for (Integer clr : clrs) {
                colors[cnt] = clr;
                cnt++;
            }
        }

        return colors;
    }

    public int getIndexOfDataSet(T dataSet) {
        for (int i = 0; i < mDataSets.size(); i++) {
            if (mDataSets.get(i) == dataSet)
                return i;
        }

        return -1;
    }

    /**
     * Returns the first DataSet from the datasets-array that has it's dependency on the left axis.
     * Returns null if no DataSet with left dependency could be found.
     *
     * @return
     */
    public T getFirstLeft() {
        for (T dataSet : mDataSets) {
            if (dataSet.getAxisDependency() == AxisDependency.LEFT)
                return dataSet;
        }

        return null;
    }

    /**
     * Returns the first DataSet from the datasets-array that has it's dependency on the right axis.
     * Returns null if no DataSet with right dependency could be found.
     *
     * @return
     */
    public T getFirstRight() {
        for (T dataSet : mDataSets) {
            if (dataSet.getAxisDependency() == AxisDependency.RIGHT)
                return dataSet;
        }

        return null;
    }

    /**
     * Generates an xPx-values array filled with numbers in range specified by the
     * parameters. Can be used for convenience.
     *
     * @return
     */
    public static List<XAxisValue> generateXVals(int from, int to) {

        List<XAxisValue> xvals = new ArrayList<XAxisValue>();

        for (int i = from; i < to; i++) {
            xvals.add(new XAxisValue(i, i + ""));
        }

        return xvals;
    }

    /**
     * Sets a custom ValueFormatter for all DataSets this data object contains.
     *
     * @param f
     */
    public void setValueFormatter(ValueFormatter f) {
        if (f == null)
            return;
        else {
            for (IDataSet set : mDataSets) {
                set.setValueFormatter(f);
            }
        }
    }

    /**
     * Sets the color of the yValue-text (color in which the yValue-labels are
     * drawn) for all DataSets this data object contains.
     *
     * @param color
     */
    public void setValueTextColor(int color) {
        for (IDataSet set : mDataSets) {
            set.setValueTextColor(color);
        }
    }

    /**
     * Sets the same list of yValue-colors for all DataSets this
     * data object contains.
     *
     * @param colors
     */
    public void setValueTextColors(List<Integer> colors) {
        for (IDataSet set : mDataSets) {
            set.setValueTextColors(colors);
        }
    }

    /**
     * Sets the Typeface for all yValue-labels for all DataSets this data object
     * contains.
     *
     * @param tf
     */
    public void setValueTypeface(Typeface tf) {
        for (IDataSet set : mDataSets) {
            set.setValueTypeface(tf);
        }
    }

    /**
     * Sets the size (in dp) of the yValue-text for all DataSets this data object
     * contains.
     *
     * @param size
     */
    public void setValueTextSize(float size) {
        for (IDataSet set : mDataSets) {
            set.setValueTextSize(size);
        }
    }

    /**
     * Enables / disables drawing values (yValue-text) for all DataSets this data
     * object contains.
     *
     * @param enabled
     */
    public void setDrawValues(boolean enabled) {
        for (IDataSet set : mDataSets) {
            set.setDrawValues(enabled);
        }
    }

    /**
     * Enables / disables highlighting values for all DataSets this data object
     * contains. If set to true, this means that values can
     * be highlighted programmatically or by touch gesture.
     */
    public void setHighlightEnabled(boolean enabled) {
        for (IDataSet set : mDataSets) {
            set.setHighlightEnabled(enabled);
        }
    }

    /**
     * Returns true if highlighting of all underlying values is enabled, false
     * if not.
     *
     * @return
     */
    public boolean isHighlightEnabled() {
        for (IDataSet set : mDataSets) {
            if (!set.isHighlightEnabled())
                return false;
        }
        return true;
    }

    /**
     * Clears this data object from all DataSets and removes all Entries. Don't
     * forget to invalidate the chart after this.
     */
    public void clearValues() {
        mDataSets.clear();
        notifyDataChanged();
    }

//    /**
//     * Checks if this data object contains the specified Entry. Returns true if
//     * so, false if not. NOTE: Performance is pretty bad on this one, do not
//     * over-use in performance critical situations.
//     *
//     * @param e
//     * @return
//     */
//    public boolean contains(Entry e) {
//
//        for (T set : mDataSets) {
//            if (set.contains(e))
//                return true;
//        }
//
//        return false;
//    }

    /**
     * Checks if this data object contains the specified DataSet. Returns true
     * if so, false if not.
     *
     * @param dataSet
     * @return
     */
    public boolean contains(T dataSet) {

        for (T set : mDataSets) {
            if (set.equals(dataSet))
                return true;
        }

        return false;
    }

    /**
     * Returns the total entry count across all DataSet objects this data object contains.
     *
     * @return
     */
    public int getEntryCount() {

        int count = 0;

        for (T set : mDataSets) {
            count += set.getEntryCount();
        }

        return count;
    }

    /**
     * Returns the DataSet object with the maximum number of entries.
     *
     * @return
     */
    public T getMaxEntryCountSet() {

        T max = mDataSets.get(0);

        for (T set : mDataSets) {

            if (set.getEntryCount() > max.getEntryCount())
                max = set;
        }

        return max;
    }
}
