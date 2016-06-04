package com.github.mikephil.charting.data.realm.implementation;

import com.github.mikephil.charting.data.BubbleEntry;
import com.github.mikephil.charting.data.realm.base.RealmBarLineScatterCandleBubbleDataSet;
import com.github.mikephil.charting.interfaces.datasets.IBubbleDataSet;
import com.github.mikephil.charting.utils.Utils;

import io.realm.DynamicRealmObject;
import io.realm.RealmObject;
import io.realm.RealmResults;

/**
 * Created by Philipp Jahoda on 07/11/15.
 */
public class RealmBubbleDataSet<T extends RealmObject> extends RealmBarLineScatterCandleBubbleDataSet<T, BubbleEntry> implements IBubbleDataSet {

    private String mSizeField;

    protected float mMaxSize;
    protected boolean mNormalizeSize = true;

    private float mHighlightCircleWidth = 2.5f;

    /**
     * Constructor for creating a CandleDataSet with realm data.
     *
     * @param result       the queried results from the realm database
     * @param yValuesField the name of the field in your data object that represents the y-yValue
     * @param sizeField    the name of the field in your data object that represents the bubble size
     */
    public RealmBubbleDataSet(RealmResults<T> result, String yValuesField, String sizeField) {
        super(result, yValuesField);
        this.mSizeField = sizeField;

        build(this.results);
        calcMinMax();
    }

    /**
     * Constructor for creating a CandleDataSet with realm data.
     *
     * @param result       the queried results from the realm database
     * @param yValuesField the name of the field in your data object that represents the y-yValue
     * @param xIndexField  the name of the field in your data object that represents the x-index
     * @param sizeField    the name of the field in your data object that represents the bubble size
     */
    public RealmBubbleDataSet(RealmResults<T> result, String yValuesField, String xIndexField, String sizeField) {
        super(result, yValuesField, xIndexField);
        this.mSizeField = sizeField;

        build(this.results);
        calcMinMax();
    }

    @Override
    public BubbleEntry buildEntryFromResultObject(T realmObject, float x) {
        DynamicRealmObject dynamicObject = new DynamicRealmObject(realmObject);

        return new BubbleEntry(
                mXValuesField == null ? x : dynamicObject.getFloat(mXValuesField),
                dynamicObject.getFloat(mYValuesField),
                dynamicObject.getFloat(mSizeField));
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
