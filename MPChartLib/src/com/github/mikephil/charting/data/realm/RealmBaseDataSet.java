package com.github.mikephil.charting.data.realm;

import com.github.mikephil.charting.data.BaseDataSet;
import com.github.mikephil.charting.data.Entry;

import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.dynamic.DynamicRealmObject;

/**
 * Created by Philipp Jahoda on 06/11/15.
 */
public abstract class RealmBaseDataSet<T extends RealmObject> extends BaseDataSet<Entry> {

    private RealmResults<T> results;
    private String yValuesField;
    private String xIndexField;

    public RealmBaseDataSet(RealmResults<T> results, String yValuesField, String xIndexField) {
        this.results = results;
        this.yValuesField = yValuesField;
        this.xIndexField = xIndexField;

        this.results.sort(xIndexField, true);

        for (T object : results) {

            DynamicRealmObject dynamicObject = new DynamicRealmObject(object);
            //mValues.add(new Entry(dynamicObject.getFloat(yValuesField), dynamicObject.getInt(xIndexField)));
        }
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
        return null;
    }

    @Override
    public int getEntryIndex(int xIndex) {
        return 0;
    }

    @Override
    public int getEntryIndex(Entry e) {
        return 0;
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
