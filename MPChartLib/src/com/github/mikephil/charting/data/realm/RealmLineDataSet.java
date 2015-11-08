package com.github.mikephil.charting.data.realm;

import android.graphics.Color;
import android.graphics.DashPathEffect;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.DefaultFillFormatter;
import com.github.mikephil.charting.formatter.FillFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.dynamic.DynamicRealmObject;

/**
 * Created by Philipp Jahoda on 21/10/15.
 */
public class RealmLineDataSet<T extends RealmObject> extends RealmLineRadarDataSet<T, Entry> implements ILineDataSet {

    private FillFormatter mFillFormatter = new DefaultFillFormatter();

    public RealmLineDataSet(RealmResults<T> result, String yValuesField, String xIndexField) {
        super(result, yValuesField, xIndexField);
    }

    @Override
    public void build(RealmResults<T> results) {

        for (T object : results) {

            DynamicRealmObject dynamicObject = new DynamicRealmObject(object);
            mValues.add(new Entry(dynamicObject.getFloat(mValuesField), dynamicObject.getInt(mIndexField)));
        }
    }

    @Override
    public float getCubicIntensity() {
        return 0.2f;
    }

    @Override
    public boolean isDrawCubicEnabled() {
        return false;
    }

    @Override
    public float getCircleSize() {
        return 10;
    }

    @Override
    public int getCircleColor(int index) {
        return Color.BLACK;
    }

    @Override
    public boolean isDrawCirclesEnabled() {
        return false;
    }

    @Override
    public int getCircleHoleColor() {
        return 0;
    }

    @Override
    public boolean isDrawCircleHoleEnabled() {
        return false;
    }

    @Override
    public DashPathEffect getDashPathEffect() {
        return null;
    }

    @Override
    public boolean isDashedLineEnabled() {
        return false;
    }

    @Override
    public FillFormatter getFillFormatter() {
        return mFillFormatter;
    }
}
