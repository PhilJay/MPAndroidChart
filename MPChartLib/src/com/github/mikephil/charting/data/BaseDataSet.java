package com.github.mikephil.charting.data;

import android.graphics.Color;
import android.graphics.Typeface;

import com.github.mikephil.charting.formatter.DefaultValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datainterfaces.datasets.IDataSet;
import com.github.mikephil.charting.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Philipp Jahoda on 21/10/15.
 * This is the base dataset of all DataSets. It's purpose is to implement critical methods
 * provided by the IDataSet interface.
 */
public abstract class BaseDataSet<T extends Entry> implements IDataSet<T> {

    /**
     * maximum y-value in the y-value array
     */
    protected float mYMax = 0.0f;

    /**
     * the minimum y-value in the y-value array
     */
    protected float mYMin = 0.0f;

    /**
     * the last start value used for calcMinMax
     */
    protected int mLastStart = 0;

    /**
     * the last end value used for calcMinMax
     */
    protected int mLastEnd = 0;

    /**
     * if true, value highlightning is enabled
     */
    protected boolean mHighlightEnabled = true;

    /**
     * custom formatter that is used instead of the auto-formatter if set
     */
    protected transient ValueFormatter mValueFormatter;

    /**
     * the color used for the value-text
     */
    protected int mValueColor = Color.BLACK;

    /**
     * the typeface used for the value text
     */
    protected Typeface mValueTypeface;

    /**
     * if true, y-values are drawn on the chart
     */
    protected boolean mDrawValues = true;

    /**
     * the size of the value-text labels
     */
    protected float mValueTextSize = 17f;

    /**
     * flag that indicates if the DataSet is visible or not
     */
    protected boolean mVisible = true;

    @Override
    public float getYMin() {
        return mYMin;
    }

    @Override
    public float getYMax() {
        return mYMax;
    }


    @Override
    public void calcMinMax(List<T> values, int start, int end) {

        if (values == null)
            return;

        final int yValCount = values.size();

        if (yValCount == 0)
            return;

        int endValue;

        if (end == 0 || end >= yValCount)
            endValue = yValCount - 1;
        else
            endValue = end;

        mLastStart = start;
        mLastEnd = endValue;

        mYMin = Float.MAX_VALUE;
        mYMax = -Float.MAX_VALUE;

        for (int i = start; i <= endValue; i++) {

            T e = values.get(i);

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
    public void setHighlightEnabled(boolean enabled) {
        mHighlightEnabled = enabled;
    }

    @Override
    public boolean isHighlightEnabled() {
        return mHighlightEnabled;
    }

    @Override
    public void addEntry(T e) {

        if (e == null)
            return;

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
    }

    @Override
    public boolean removeEntry(T e) {

        if (e == null)
            return false;

        List<T> yVals = getYVals();

        if (yVals == null)
            return false;

        // remove the entry
        boolean removed = yVals.remove(e);

        if (removed) {
            calcMinMax(yVals, mLastStart, mLastEnd);
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

    @Override
    public int getEntryPosition(T e) {

        List<T> values = getYVals();

        for (int i = 0; i < values.size(); i++) {
            if (e.equalTo(values.get(i)))
                return i;
        }

        return -1;
    }

    @Override
    public void setValueFormatter(ValueFormatter f) {

        if (f == null)
            return;
        else
            mValueFormatter = f;
    }

    @Override
    public ValueFormatter getValueFormatter() {
        if (mValueFormatter == null)
            return new DefaultValueFormatter(1);
        return mValueFormatter;
    }

    @Override
    public void setValueTextColor(int color) {
        mValueColor = color;
    }

    @Override
    public void setValueTypeface(Typeface tf) {
        mValueTypeface = tf;
    }

    @Override
    public void setValueTextSize(float size) {
        mValueTextSize = Utils.convertDpToPixel(size);
    }

    @Override
    public int getValueTextColor() {
        return mValueColor;
    }

    @Override
    public Typeface getValueTypeface() {
        return mValueTypeface;
    }

    @Override
    public float getValueTextSize() {
        return mValueTextSize;
    }

    @Override
    public void setDrawValues(boolean enabled) {
        this.mDrawValues = enabled;
    }

    @Override
    public boolean isDrawValuesEnabled() {
        return mDrawValues;
    }

    @Override
    public void setVisible(boolean visible) {
        mVisible = visible;
    }

    @Override
    public boolean isVisible() {
        return mVisible;
    }

    @Override
    public T getEntryForXIndex(int x) {

        List<T> values = getYVals();

        int index = getEntryIndex(x);
        if (index > -1)
            return values.get(index);
        return null;
    }

    @Override
    public int getEntryIndex(int x) {

        List<T> values = getYVals();

        int low = 0;
        int high = values.size() - 1;
        int closest = -1;

        while (low <= high) {
            int m = (high + low) / 2;

            if (x == values.get(m).getXIndex()) {
                while (m > 0 && values.get(m - 1).getXIndex() == x)
                    m--;

                return m;
            }

            if (x > values.get(m).getXIndex())
                low = m + 1;
            else
                high = m - 1;

            closest = m;
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

    @Override
    public boolean contains(Entry e) {

        List<T> values = getYVals();

        for (Entry entry : values) {
            if (entry.equals(e))
                return true;
        }

        return false;
    }
}
