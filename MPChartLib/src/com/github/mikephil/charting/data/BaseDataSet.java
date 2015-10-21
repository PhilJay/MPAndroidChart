package com.github.mikephil.charting.data;

import com.github.mikephil.charting.interfaces.datainterfaces.datasets.IDataSet;

import java.util.List;

/**
 * Created by philipp on 21/10/15.
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
     * calc minimum and maximum y value
     */
    protected void calcMinMax(List<T> values, int start, int end) {

        if(values == null)
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


}
