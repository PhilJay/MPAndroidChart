package com.github.mikephil.charting.data.realm;

import android.graphics.Color;

import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;

import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.dynamic.DynamicRealmObject;

/**
 * Created by Philipp Jahoda on 07/11/15.
 */
public class RealmBarDataSet<T extends RealmObject> extends RealmBarLineScatterCandleBubbleDataSet<T, BarEntry> implements IBarDataSet {

    public RealmBarDataSet(RealmResults<T> results, String yValuesField, String xIndexField) {
        super(results, yValuesField, xIndexField);
    }

    @Override
    public void build(RealmResults<T> results) {

        for (T object : results) {

            DynamicRealmObject dynamicObject = new DynamicRealmObject(object);
            mValues.add(new BarEntry(dynamicObject.getFloat(mValuesField), dynamicObject.getInt(mIndexField)));
        }
    }

    @Override
    public float getBarSpace() {
        return 0;
    }

    @Override
    public boolean isStacked() {
        return false;
    }

    @Override
    public int getStackSize() {
        return 0;
    }

    @Override
    public int getBarShadowColor() {
        return Color.BLACK;
    }

    @Override
    public int getHighLightAlpha() {
        return 120;
    }

    @Override
    public String[] getStackLabels() {
        return null;
    }

}
