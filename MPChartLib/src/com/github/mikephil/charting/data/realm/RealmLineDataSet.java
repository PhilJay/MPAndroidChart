package com.github.mikephil.charting.data.realm;

import android.graphics.Color;
import android.graphics.DashPathEffect;

import com.github.mikephil.charting.data.BaseDataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.DefaultFillFormatter;
import com.github.mikephil.charting.formatter.FillFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.List;

import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.dynamic.DynamicRealmObject;

/**
 * Created by Philipp Jahoda on 21/10/15.
 */
public class RealmLineDataSet<T extends RealmObject> extends BaseDataSet<Entry> implements ILineDataSet {

    private List<Entry> mValues = new ArrayList<>();

    private FillFormatter mFillFormatter = new DefaultFillFormatter();

    private RealmResults<T> results;
    private String yValuesField;
    private String xIndexField;

    public RealmLineDataSet(RealmResults<T> result, String yValuesField, String xIndexField) {
        super("");
        this.results = result;
        this.yValuesField = yValuesField;
        this.xIndexField = xIndexField;

        results.sort(xIndexField, true);

        for (T object : results) {

            DynamicRealmObject dynamicObject = new DynamicRealmObject(object);

            mValues.add(new Entry(dynamicObject.getFloat(yValuesField), dynamicObject.getInt(xIndexField)));
        }

        calcMinMax(0, mValues.size());
    }


    @Override
    public float getCubicIntensity() {
        return 0;
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
        return true;
    }

    @Override
    public int getCircleHoleColor() {
        return Color.BLACK;
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

    @Override
    public int getFillColor() {
        return 0;
    }

    @Override
    public int getFillAlpha() {
        return 0;
    }

    @Override
    public float getLineWidth() {
        return 5;
    }

    @Override
    public boolean isDrawFilledEnabled() {
        return false;
    }

    @Override
    public void setDrawFilled(boolean enabled) {

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

    @Override
    public float getYMin() {
        return results.min(yValuesField).floatValue();
    }

    @Override
    public float getYMax() {
        return results.max(yValuesField).floatValue();
    }

    @Override
    public int getEntryCount() {
        return results.size();
    }

    @Override
    public void calcMinMax(int start, int end) {

    }

    @Override
    public Entry getEntryForXIndex(int xIndex) {

        DynamicRealmObject o = new DynamicRealmObject(results.where().equalTo(xIndexField, xIndex).findFirst());
        return new Entry(o.getFloat(yValuesField), o.getInt(xIndexField));
    }

    @Override
    public Entry getEntryForIndex(int index) {
        return mValues.get(index);
    }

    @Override
    public int getEntryIndex(int xIndex) {
        return 0;
    }

    @Override
    public int getEntryIndex(Entry e) {
        return mValues.indexOf(e);
    }

    @Override
    public float getYValForXIndex(int xIndex) {
        return new DynamicRealmObject(results.where().greaterThanOrEqualTo(xIndexField, xIndex).findFirst()).getFloat(yValuesField);
    }

    @Override
    public void addEntry(Entry e) {

    }

    @Override
    public boolean removeEntry(Entry e) {
        return false;
    }
}
