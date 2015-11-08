package com.github.mikephil.charting.data.realm;

import com.github.mikephil.charting.data.BubbleEntry;
import com.github.mikephil.charting.interfaces.datasets.IBubbleDataSet;

import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.dynamic.DynamicRealmObject;

/**
 * Created by Philipp Jahoda on 07/11/15.
 */
public class RealmBubbleDataSet<T extends RealmObject> extends RealmBarLineScatterCandleBubbleDataSet<T, BubbleEntry> implements IBubbleDataSet {

    private String mSizeField;

    public RealmBubbleDataSet(RealmResults<T> result, String yValuesField, String xIndexField, String sizeField) {
        super(result, yValuesField, xIndexField);
        this.mSizeField = sizeField;
    }

    @Override
    public void build(RealmResults<T> results) {

        for (T object : results) {

            DynamicRealmObject dynamicObject = new DynamicRealmObject(object);
            mValues.add(new BubbleEntry(dynamicObject.getInt(mIndexField), dynamicObject.getFloat(mValuesField), dynamicObject.getFloat(mSizeField)));
        }
    }

    @Override
    public void setHighlightCircleWidth(float width) {

    }

    @Override
    public float getXMax() {
        return 0;
    }

    @Override
    public float getXMin() {
        return 0;
    }

    @Override
    public float getMaxSize() {
        return 0;
    }

    @Override
    public float getHighlightCircleWidth() {
        return 0;
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
