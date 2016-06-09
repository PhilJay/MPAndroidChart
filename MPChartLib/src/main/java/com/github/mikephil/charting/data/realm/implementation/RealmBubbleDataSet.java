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
     * @param yValuesField the name of the field in your data object that represents the y-value
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
     * @param yValuesField the name of the field in your data object that represents the y-value
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

        if (mValues == null || mValues.isEmpty())
            return;

        mYMax = -Float.MAX_VALUE;
        mYMin = Float.MAX_VALUE;
        mXMax = -Float.MAX_VALUE;
        mXMin = Float.MAX_VALUE;

        for (BubbleEntry e : mValues) {

            calcMinMax(e);

            final float size = e.getSize();

            if (size > mMaxSize) {
                mMaxSize = size;
            }
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
