
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
        copy(copied);
        return copied;
    }

    protected void copy(BubbleDataSet bubbleDataSet) {
        bubbleDataSet.mHighlightCircleWidth = mHighlightCircleWidth;
        bubbleDataSet.mNormalizeSize = mNormalizeSize;
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

        // debug message
        StringBuilder sb = new StringBuilder(String.format(Locale.getDefault(),
                "getEntriesForXValue(%.3f) found %d -> ", xValue, entries.size()));
        for (BubbleEntry e : entries) {
            sb.append(String.format(Locale.getDefault(), "x= %.1f, y= %.1f, xr= %.1f, yr= %.1f  ", e.getX(), e.getY(), e.getDrawnXRadius(), e.getDrawnYRadius()));
        }
        Log.i("BubbleDataSet", sb.toString());

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

        // debug message
        Log.i("BubbleDataSet", String.format(Locale.getDefault(), "getEntryIndex(%.2f, %.2f) found %s", xValue, yValue, mValues.get(index)));
        return index;
    }

    private float distance(BubbleEntry entry, float x, float y) {
        if (Float.isNaN(y))
            return Math.abs(x - entry.getX());
        else
            return (float) Math.hypot(entry.getX() - x, entry.getY() - y);
    }

}
