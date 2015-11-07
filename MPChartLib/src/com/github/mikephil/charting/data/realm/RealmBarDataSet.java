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
public class RealmBarDataSet<T extends RealmObject> extends RealmBaseDataSet<T, BarEntry> implements IBarDataSet {

    public RealmBarDataSet(RealmResults<T> results, String yValuesField, String xIndexField) {
        super(results, yValuesField, xIndexField);

        for (T object : this.results) {

            DynamicRealmObject dynamicObject = new DynamicRealmObject(object);
            mValues.add(new BarEntry(dynamicObject.getFloat(yValuesField), dynamicObject.getInt(xIndexField)));
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
        return 0;
    }

    @Override
    public String[] getStackLabels() {
        return null;
    }

    @Override
    public int getHighLightColor() {
        return 0;
    }
}
