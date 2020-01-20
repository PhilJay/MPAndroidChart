
package com.github.mikephil.charting.data;

import android.util.Log;

import com.github.mikephil.charting.interfaces.datasets.IBubbleDataSet;
import com.github.mikephil.charting.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class BubbleDataSet extends BarLineScatterCandleBubbleDataSet<BubbleEntry> implements IBubbleDataSet {

    protected float mMaxSize;
    protected boolean mNormalizeSize = true;

    private float mHighlightCircleWidth = 2.5f;

    public BubbleDataSet(List<BubbleEntry> yVals, String label) {
        super(yVals, label);
    }

    @Override
    public void setHighlightCircleWidth(float width) {
        mHighlightCircleWidth = Utils.convertDpToPixel(width);
    }

    @Override
    public float getHighlightCircleWidth() {
        return mHighlightCircleWidth;
    }

    @Override
    protected void calcMinMax(BubbleEntry e) {
        super.calcMinMax(e);

        final float size = e.getSize();

        if (size > mMaxSize) {
            mMaxSize = size;
        }
    }

    @Override
    public DataSet<BubbleEntry> copy() {
        List<BubbleEntry> entries = new ArrayList<BubbleEntry>();
        for (int i = 0; i < mValues.size(); i++) {
            entries.add(mValues.get(i).copy());
        }
        BubbleDataSet copied = new BubbleDataSet(entries, getLabel());
        copyFieldsTo(copied);
        return copied;
    }

    protected void copy(BubbleDataSet bubbleDataSet) {
        copyFieldsFrom(bubbleDataSet);
    }

    @Override
    public float getMaxSize() {
        return mMaxSize;
    }

    @Override
    public boolean isNormalizeSizeEnabled() {
        return mNormalizeSize;
    }

    public void setNormalizeSizeEnabled(boolean normalizeSize) {
        mNormalizeSize = normalizeSize;
    }


    /**
     * Returns all Entry objects found at the given x-value,
     * or an empty array if no Entry object at that x-value.
     *
     * We cannot run a binary search here: consider the case
     * where there is a huge bubble centered at (0, 0)
     * covering the entire chart - it must be included
     * in every search result.
     *
     * @param xValue
     * @return closest entries
     */
    @Override
    public List<BubbleEntry> getEntriesForXValue(float xValue) {
        List<BubbleEntry> entries = new ArrayList<>();

        for (BubbleEntry entry : mValues) {
            if (entry.containsX(xValue))
                entries.add(entry);
        }

        return entries;
    }

    /**
     * Returns the first Entry object whose center is closest to the given xValue and yValue.
     * Rounding is ignored.
     *
     * INFORMATION: This method does calculations at runtime. Do
     * not over-use in performance critical situations.
     *
     * @param xValue the x-value
     * @param yValue the y-value
     * @param rounding ignored
     * @return index of closest entry
     *
     *
     */
    @Override
    public int getEntryIndex(float xValue, float yValue, Rounding rounding) {
        float distance = Float.POSITIVE_INFINITY;
        int index = -1;

        for (int i = 0; i < mValues.size(); ++i) {
            BubbleEntry entry = mValues.get(i);
            float d = distance(entry, xValue, yValue);
            if (d < distance) {
                distance = d;
                index = i;
            }
        }

        return index;
    }

    private float distance(BubbleEntry entry, float x, float y) {
        if (Float.isNaN(y))
            return Math.abs(x - entry.getX());
        else
            return (float) Math.hypot(entry.getX() - x, entry.getY() - y);
    }

    /**
     * Copies all the mutable fields from this data set
     *
     * @param bubbleDataSet to that data set
     */
    public void copyFieldsTo(BubbleDataSet bubbleDataSet) {
        bubbleDataSet.setValueTypeface(getValueTypeface());
        bubbleDataSet.setValueTextSize(getValueTextSize());
        bubbleDataSet.setValueFormatter(getValueFormatter());
        bubbleDataSet.setValueTextColors(getValueColors());
        bubbleDataSet.setHighLightColor(getHighLightColor());
        bubbleDataSet.setColors(getColors());
        // the setter changes the value, so access directly
        bubbleDataSet.mHighlightCircleWidth = getHighlightCircleWidth();
        bubbleDataSet.setHighlightEnabled(isHighlightEnabled());
        bubbleDataSet.setDrawIcons(isDrawIconsEnabled());
        bubbleDataSet.setDrawValues(isDrawValuesEnabled());
        bubbleDataSet.setVisible(isVisible());
        bubbleDataSet.setNormalizeSizeEnabled(isNormalizeSizeEnabled());
        bubbleDataSet.setAxisDependency(getAxisDependency());
        bubbleDataSet.setForm(getForm());
        bubbleDataSet.setGradientColors(getGradientColors());
    }

    public void copyFieldsFrom(BubbleDataSet from) {
        setValueTypeface(from.getValueTypeface());
        setValueTextSize(from.getValueTextSize());
        setValueFormatter(from.getValueFormatter());
        setValueTextColors(from.getColors());
        setHighLightColor(from.getHighLightColor());
        setColors(from.getColors());
        // the setter changes the value, so access directly
        mHighlightCircleWidth = from.getHighlightCircleWidth();
        setHighlightEnabled(from.isHighlightEnabled());
        setDrawIcons(from.isDrawIconsEnabled());
        setDrawValues(from.isDrawValuesEnabled());
        setVisible(from.isVisible());
        setNormalizeSizeEnabled(from.isNormalizeSizeEnabled());
        setAxisDependency(from.getAxisDependency());
        setForm(from.getForm());
        setGradientColors(from.getGradientColors());
    }
}
