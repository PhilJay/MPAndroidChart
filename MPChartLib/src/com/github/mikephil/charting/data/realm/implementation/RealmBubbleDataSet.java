package com.github.mikephil.charting.data.realm.implementation;

import android.graphics.Color;

import com.github.mikephil.charting.data.BubbleEntry;
import com.github.mikephil.charting.data.realm.base.RealmBarLineScatterCandleBubbleDataSet;
import com.github.mikephil.charting.interfaces.datasets.IBubbleDataSet;
import com.github.mikephil.charting.utils.Utils;

import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.dynamic.DynamicRealmObject;

/**
 * Created by Philipp Jahoda on 07/11/15.
 */
public class RealmBubbleDataSet<T extends RealmObject> extends RealmBarLineScatterCandleBubbleDataSet<T, BubbleEntry> implements IBubbleDataSet {

    private String mSizeField;

    protected float mXMax;
    protected float mXMin;
    protected float mMaxSize;

    private float mHighlightCircleWidth = 2.5f;

    /**
     * Constructor for creating a CandleDataSet with realm data.
     *
     * @param result       the queried results from the realm database
     * @param yValuesField the name of the field in your data object that represents the y-value
     * @param sizeField    the name of the field in your data object that represents the bubble size
     */
    public RealmBubbleDataSet(RealmResults<T> result, String yValuesField, String sizeField) {
        super(result, yValuesField);
        this.mSizeField = sizeField;

        build(this.results);
        calcMinMax(0, results.size());
    }

    /**
     * Constructor for creating a CandleDataSet with realm data.
     *
     * @param result       the queried results from the realm database
     * @param yValuesField the name of the field in your data object that represents the y-value
     * @param xIndexField  the name of the field in your data object that represents the x-index
     * @param sizeField    the name of the field in your data object that represents the bubble size
     */
    public RealmBubbleDataSet(RealmResults<T> result, String yValuesField, String xIndexField, String sizeField) {
        super(result, yValuesField, xIndexField);
        this.mSizeField = sizeField;

        build(this.results);
        calcMinMax(0, results.size());
    }

    @Override
    public void build(RealmResults<T> results) {

        if(mIndexField == null) {

            int xIndex = 0;

            for (T object : results) {

                DynamicRealmObject dynamicObject = new DynamicRealmObject(object);
                mValues.add(new BubbleEntry(xIndex, dynamicObject.getFloat(mValuesField), dynamicObject.getFloat(mSizeField)));
                xIndex++;
            }
        } else {

            for (T object : results) {

                DynamicRealmObject dynamicObject = new DynamicRealmObject(object);
                mValues.add(new BubbleEntry(dynamicObject.getInt(mIndexField), dynamicObject.getFloat(mValuesField), dynamicObject.getFloat(mSizeField)));
            }
        }
    }

    @Override
    public void calcMinMax(int start, int end) {

        if (mValues == null)
            return;

        if (mValues.size() == 0)
            return;

        int endValue;

        if (end == 0 || end >= mValues.size())
            endValue = mValues.size() - 1;
        else
            endValue = end;

        mYMin = yMin(mValues.get(start));
        mYMax = yMax(mValues.get(start));

        // need chart width to guess this properly

        for (int i = start; i < endValue; i++) {

            final BubbleEntry entry = mValues.get(i);

            final float ymin = yMin(entry);
            final float ymax = yMax(entry);

            if (ymin < mYMin) {
                mYMin = ymin;
            }

            if (ymax > mYMax) {
                mYMax = ymax;
            }

            final float xmin = xMin(entry);
            final float xmax = xMax(entry);

            if (xmin < mXMin) {
                mXMin = xmin;
            }

            if (xmax > mXMax) {
                mXMax = xmax;
            }

            final float size = largestSize(entry);

            if (size > mMaxSize) {
                mMaxSize = size;
            }
        }
    }

    @Override
    public float getXMax() {
        return mXMax;
    }

    @Override
    public float getXMin() {
        return mXMin;
    }

    @Override
    public float getMaxSize() {
        return mMaxSize;
    }

    private float yMin(BubbleEntry entry) {
        return entry.getVal();
    }

    private float yMax(BubbleEntry entry) {
        return entry.getVal();
    }

    private float xMin(BubbleEntry entry) {
        return (float) entry.getXIndex();
    }

    private float xMax(BubbleEntry entry) {
        return (float) entry.getXIndex();
    }

    private float largestSize(BubbleEntry entry) {
        return entry.getSize();
    }

    @Override
    public void setHighlightCircleWidth(float width) {
        mHighlightCircleWidth = Utils.convertDpToPixel(width);
    }

    @Override
    public float getHighlightCircleWidth() {
        return mHighlightCircleWidth;
    }

    /**
     * Sets the database fieldname for the bubble size.
     *
     * @param sizeField
     */
    public void setSizeField(String sizeField) {
        this.mSizeField = sizeField;
    }

    /**
     * Returns the database fieldname that stores bubble size.
     *
     * @return
     */
    public String getSizeField() {
        return mSizeField;
    }
}
