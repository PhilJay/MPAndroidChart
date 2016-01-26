package com.github.mikephil.charting.data.realm.implementation;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.realm.base.RealmBaseDataSet;
import com.github.mikephil.charting.interfaces.datasets.IPieDataSet;
import com.github.mikephil.charting.utils.Utils;

import io.realm.DynamicRealmObject;
import io.realm.RealmObject;
import io.realm.RealmResults;

/**
 * Created by Philipp Jahoda on 07/11/15.
 */
public class RealmPieDataSet<T extends RealmObject> extends RealmBaseDataSet<T, Entry> implements IPieDataSet {

    /**
     * the space in degrees between the chart-slices, default 0f
     */
    private float mSliceSpace = 0f;

    /**
     * indicates the selection distance of a pie slice
     */
    private float mShift = 18f;


    /**
     * Constructor for creating a PieDataSet with realm data.
     *
     * @param result       the queried results from the realm database
     * @param yValuesField the name of the field in your data object that represents the y-value
     */
    public RealmPieDataSet(RealmResults<T> result, String yValuesField) {
        super(result, yValuesField);

        build(this.results);
        calcMinMax(0, results.size());
    }

    /**
     * Constructor for creating a PieDataSet with realm data.
     *
     * @param result       the queried results from the realm database
     * @param yValuesField the name of the field in your data object that represents the y-value
     * @param xIndexField  the name of the field in your data object that represents the x-index
     */
    public RealmPieDataSet(RealmResults<T> result, String yValuesField, String xIndexField) {
        super(result, yValuesField, xIndexField);

        build(this.results);
        calcMinMax(0, results.size());
    }

    @Override
    public void build(RealmResults<T> results) {

        if (mIndexField == null) { // x-index not available

            int xIndex = 0;

            for (T object : results) {

                DynamicRealmObject dynamicObject = new DynamicRealmObject(object);
                mValues.add(new Entry(dynamicObject.getFloat(mValuesField), xIndex));
                xIndex++;
            }

        } else {

            for (T object : results) {

                DynamicRealmObject dynamicObject = new DynamicRealmObject(object);
                mValues.add(new Entry(dynamicObject.getFloat(mValuesField), dynamicObject.getInt(mIndexField)));
            }
        }
    }

    /**
     * sets the space that is left out between the piechart-slices, default: 0°
     * --> no space, maximum 45, minimum 0 (no space)
     *
     * @param degrees
     */
    public void setSliceSpace(float degrees) {

        if (degrees > 45)
            degrees = 45f;
        if (degrees < 0)
            degrees = 0f;

        mSliceSpace = degrees;
    }

    @Override
    public float getSliceSpace() {
        return mSliceSpace;
    }

    /**
     * sets the distance the highlighted piechart-slice of this DataSet is
     * "shifted" away from the center of the chart, default 12f
     *
     * @param shift
     */
    public void setSelectionShift(float shift) {
        mShift = Utils.convertDpToPixel(shift);
    }

    @Override
    public float getSelectionShift() {
        return mShift;
    }
}
