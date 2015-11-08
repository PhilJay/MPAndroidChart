package com.github.mikephil.charting.data.realm;

import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;

import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.interfaces.datasets.ICandleDataSet;

import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.dynamic.DynamicRealmObject;

/**
 * Created by Philipp Jahoda on 07/11/15.
 */
public class RealmCandleDataSet<T extends RealmObject> extends RealmBaseDataSet<T, CandleEntry> implements ICandleDataSet {

    private String mHighField;
    private String mLowField;
    private String mOpenField;
    private String mCloseField;

    public RealmCandleDataSet(RealmResults<T> result, String highField, String lowField, String openField, String closeField, String xIndexField) {
        super(result, "", xIndexField);
        this.mHighField = highField;
        this.mLowField = lowField;
        this.mOpenField = openField;
        this.mCloseField = closeField;
    }

    @Override
    public void build(RealmResults<T> results) {
        for (T object : results) {

            DynamicRealmObject dynamicObject = new DynamicRealmObject(object);
            mValues.add(new CandleEntry(dynamicObject.getInt(mIndexField), dynamicObject.getFloat(mHighField), dynamicObject.getFloat(mLowField),
                    dynamicObject.getFloat(mOpenField), dynamicObject.getFloat(mCloseField)));
        }
    }

    @Override
    public float getBodySpace() {
        return 0;
    }

    @Override
    public float getShadowWidth() {
        return 2;
    }

    @Override
    public int getShadowColor() {
        return Color.BLACK;
    }

    @Override
    public int getDecreasingColor() {
        return Color.GREEN;
    }

    @Override
    public int getIncreasingColor() {
        return Color.RED;
    }

    @Override
    public Paint.Style getDecreasingPaintStyle() {
        return null;
    }

    @Override
    public Paint.Style getIncreasingPaintStyle() {
        return null;
    }

    @Override
    public boolean getShadowColorSameAsCandle() {
        return false;
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
