
package com.github.mikephil.charting.data;

import com.github.mikephil.charting.interfaces.datasets.IBubbleDataSet;
import com.github.mikephil.charting.utils.Utils;

import java.util.ArrayList;
import java.util.List;

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
    public void calcMinMax() {

        if (mValues == null || mValues.isEmpty())
            return;

        mYMax = Float.MIN_VALUE;
        mYMin = Float.MAX_VALUE;
        mXMax = Float.MIN_VALUE;
        mXMin = Float.MAX_VALUE;

        // need chart width to guess this properly

        for (BubbleEntry entry : mValues) {

            float ymin = entry.getY();
            float ymax = entry.getY();

            if (ymin < mYMin) {
                mYMin = ymin;
            }

            if (ymax > mYMax) {
                mYMax = ymax;
            }

            final float xmin = entry.getX();
            final float xmax = entry.getX();

            if (xmin < mXMin) {
                mXMin = xmin;
            }

            if (xmax > mXMax) {
                mXMax = xmax;
            }

            final float size = entry.getSize();

            if (size > mMaxSize) {
                mMaxSize = size;
            }
        }
    }

    @Override
    public DataSet<BubbleEntry> copy() {

        List<BubbleEntry> yVals = new ArrayList<BubbleEntry>();

        for (int i = 0; i < mValues.size(); i++) {
            yVals.add(mValues.get(i).copy());
        }

        BubbleDataSet copied = new BubbleDataSet(yVals, getLabel());
        copied.mColors = mColors;
        copied.mHighLightColor = mHighLightColor;

        return copied;
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
}
