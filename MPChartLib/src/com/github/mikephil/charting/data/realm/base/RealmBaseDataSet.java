package com.github.mikephil.charting.data.realm.base;

import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.BaseDataSet;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.Entry;

import java.util.ArrayList;
import java.util.List;

import io.realm.DynamicRealmObject;
import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Created by Philipp Jahoda on 06/11/15.
 */
public abstract class RealmBaseDataSet<T extends RealmObject, S extends Entry> extends BaseDataSet<S> {

    /**
     * a list of queried realm objects
     */
    protected RealmResults<T> results;

    /**
     * a cached list of all data read from the database
     */
    protected List<S> mValues;

    /**
     * maximum y-value in the y-value array
     */
    protected float mYMax = 0.0f;

    /**
     * the minimum y-value in the y-value array
     */
    protected float mYMin = 0.0f;

    /**
     * fieldname of the column that contains the y-values of this dataset
     */
    protected String mValuesField;

    /**
     * fieldname of the column that contains the x-indices of this dataset
     */
    protected String mIndexField;

    public RealmBaseDataSet(RealmResults<T> results, String yValuesField) {
        this.results = results;
        this.mValuesField = yValuesField;
        this.mValues = new ArrayList<S>();

        if (mIndexField != null)
            this.results.sort(mIndexField, Sort.ASCENDING);
    }

    /**
     * Constructor that takes the realm RealmResults, sorts & stores them.
     *
     * @param results
     * @param yValuesField
     * @param xIndexField
     */
    public RealmBaseDataSet(RealmResults<T> results, String yValuesField, String xIndexField) {
        this.results = results;
        this.mValuesField = yValuesField;
        this.mIndexField = xIndexField;
        this.mValues = new ArrayList<S>();

        if (mIndexField != null)
            this.results.sort(mIndexField, Sort.ASCENDING);
    }

    /**
     * Rebuilds the DataSet based on the given RealmResults.
     */
    public void build(RealmResults<T> results) {

        int xIndex = 0;
        for (T object : results) {
            mValues.add(buildEntryFromResultObject(object, xIndex++));
        }
    }

    public S buildEntryFromResultObject(T realmObject, int xIndex) {
        DynamicRealmObject dynamicObject = new DynamicRealmObject(realmObject);

        return (S)new Entry(dynamicObject.getFloat(mValuesField),
                mIndexField == null ? xIndex : dynamicObject.getInt(mIndexField));
    }

    @Override
    public float getYMin() {
        //return results.min(mValuesField).floatValue();
        return mYMin;
    }

    @Override
    public float getYMax() {
        //return results.max(mValuesField).floatValue();
        return mYMax;
    }

    @Override
    public int getEntryCount() {
        return mValues.size();
    }

    @Override
    public void calcMinMax(int start, int end) {

        if (mValues == null)
            return;

        final int yValCount = mValues.size();

        if (yValCount == 0)
            return;

        int endValue;

        if (end == 0 || end >= yValCount)
            endValue = yValCount - 1;
        else
            endValue = end;

        mYMin = Float.MAX_VALUE;
        mYMax = -Float.MAX_VALUE;

        for (int i = start; i <= endValue; i++) {

            S e = mValues.get(i);

            if (e != null && !Float.isNaN(e.getVal())) {

                if (e.getVal() < mYMin)
                    mYMin = e.getVal();

                if (e.getVal() > mYMax)
                    mYMax = e.getVal();
            }
        }

        if (mYMin == Float.MAX_VALUE) {
            mYMin = 0.f;
            mYMax = 0.f;
        }
    }

    @Override
    public S getEntryForXIndex(int xIndex) {
        //DynamicRealmObject o = new DynamicRealmObject(results.where().equalTo(mIndexField, xIndex).findFirst());
        //return new Entry(o.getFloat(mValuesField), o.getInt(mIndexField));
        return getEntryForXIndex(xIndex, DataSet.Rounding.CLOSEST);
    }

    @Override
    public S getEntryForXIndex(int xIndex, DataSet.Rounding rounding) {
        int index = getEntryIndex(xIndex, rounding);
        if (index > -1)
            return mValues.get(index);
        return null;
    }

    @Override
    public S getEntryForIndex(int index) {
        //DynamicRealmObject o = new DynamicRealmObject(results.get(index));
        //return new Entry(o.getFloat(mValuesField), o.getInt(mIndexField));
        return mValues.get(index);
    }

    @Override
    public int getEntryIndex(int x, DataSet.Rounding rounding) {

        int low = 0;
        int high = mValues.size() - 1;
        int closest = -1;

        while (low <= high) {
            int m = (high + low) / 2;

            S entry = mValues.get(m);

            if (x == entry.getXIndex()) {
                while (m > 0 && mValues.get(m - 1).getXIndex() == x)
                    m--;

                return m;
            }

            if (x > entry.getXIndex())
                low = m + 1;
            else
                high = m - 1;

            closest = m;
        }

        if (closest != -1) {
            int closestXIndex = mValues.get(closest).getXIndex();
            if (rounding == DataSet.Rounding.UP) {
                if (closestXIndex < x && closest < mValues.size() - 1) {
                    ++closest;
                }
            } else if (rounding == DataSet.Rounding.DOWN) {
                if (closestXIndex > x && closest > 0) {
                    --closest;
                }
            }
        }

        return closest;
    }

    @Override
    public int getEntryIndex(S e) {
        return mValues.indexOf(e);
    }

    @Override
    public float getYValForXIndex(int xIndex) {
        //return new DynamicRealmObject(results.where().greaterThanOrEqualTo(mIndexField, xIndex).findFirst()).getFloat(mValuesField);
        Entry e = getEntryForXIndex(xIndex);

        if (e != null && e.getXIndex() == xIndex)
            return e.getVal();
        else
            return Float.NaN;
    }

    @Override
    public boolean addEntry(S e) {

        if (e == null)
            return false;

        float val = e.getVal();

        if (mValues == null) {
            mValues = new ArrayList<S>();
        }

        if (mValues.size() == 0) {
            mYMax = val;
            mYMin = val;
        } else {
            if (mYMax < val)
                mYMax = val;
            if (mYMin > val)
                mYMin = val;
        }

        // add the entry
        mValues.add(e);
        return true;
    }

    @Override
    public boolean removeEntry(S e) {

        if (e == null)
            return false;

        if (mValues == null)
            return false;

        // remove the entry
        boolean removed = mValues.remove(e);

        if (removed) {
            calcMinMax(0, mValues.size());
        }

        return removed;
    }

    @Override
    public void addEntryOrdered(S e) {

        if (e == null)
            return;

        float val = e.getVal();

        if (mValues == null) {
            mValues = new ArrayList<S>();
        }

        if (mValues.size() == 0) {
            mYMax = val;
            mYMin = val;
        } else {
            if (mYMax < val)
                mYMax = val;
            if (mYMin > val)
                mYMin = val;
        }

        if (mValues.size() > 0 && mValues.get(mValues.size() - 1).getXIndex() > e.getXIndex()) {
            int closestIndex = getEntryIndex(e.getXIndex(), DataSet.Rounding.UP);
            mValues.add(closestIndex, e);
            return;
        }

        mValues.add(e);
    }

    /**
     * Returns the List of values that has been extracted from the RealmResults
     * using the provided fieldnames.
     *
     * @return
     */
    public List<S> getValues() {
        return mValues;
    }

    @Override
    public void clear() {
        mValues.clear();
        notifyDataSetChanged();
    }

    public RealmResults<T> getResults() {
        return results;
    }

    /**
     * Returns the fieldname that represents the "y-values" in the realm-data.
     *
     * @return
     */
    public String getValuesField() {
        return mValuesField;
    }

    /**
     * Sets the field name that is used for getting the y-values out of the RealmResultSet.
     *
     * @param yValuesField
     */
    public void setValuesField(String yValuesField) {
        this.mValuesField = yValuesField;
    }

    /**
     * Returns the fieldname that represents the "x-index" in the realm-data.
     *
     * @return
     */
    public String getIndexField() {
        return mIndexField;
    }

    /**
     * Sets the field name that is used for getting the x-indices out of the RealmResultSet.
     *
     * @param xIndexField
     */
    public void setIndexField(String xIndexField) {
        this.mIndexField = xIndexField;
    }
}
