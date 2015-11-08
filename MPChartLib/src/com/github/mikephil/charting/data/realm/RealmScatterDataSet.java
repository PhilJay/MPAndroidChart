package com.github.mikephil.charting.data.realm;

import android.graphics.DashPathEffect;

import com.github.mikephil.charting.charts.ScatterChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.interfaces.datasets.IScatterDataSet;

import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.dynamic.DynamicRealmObject;

/**
 * Created by Philipp Jahoda on 07/11/15.
 */
public class RealmScatterDataSet<T extends RealmObject> extends RealmBaseDataSet<T, Entry> implements IScatterDataSet {

    public RealmScatterDataSet(RealmResults<T> result, String yValuesField, String xIndexField) {
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
    public float getScatterShapeSize() {
        return 0;
    }

    @Override
    public ScatterChart.ScatterShape getScatterShape() {
        return null;
    }

    @Override
    public boolean isVerticalHighlightIndicatorEnabled() {
        return false;
    }

    @Override
    public boolean isHorizontalHighlightIndicatorEnabled() {
        return false;
    }

    @Override
    public float getHighlightLineWidth() {
        return 0;
    }

    @Override
    public DashPathEffect getDashPathEffectHighlight() {
        return null;
    }

    @Override
    public int getHighLightColor() {
        return 0;
    }
}
