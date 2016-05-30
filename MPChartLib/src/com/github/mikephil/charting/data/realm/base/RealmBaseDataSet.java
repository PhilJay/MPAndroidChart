package com.github.mikephil.charting.data.realm.base;

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
     * maximum x-value in the value array
     */
    protected float mXMax = 0.0f;

    /**
     * minimum x-value in the value array
     */
    protected float mXMin = 0.0f;

    /**
     * fieldname of the column that contains the y-values of this dataset
     */
    protected String mYValuesField;

    /**
     * fieldname of the column that contains the x-values of this dataset
     */
    protected String mXValuesField;

    public RealmBaseDataSet(RealmResults<T> results, String yValuesField) {
        this.results = results;
        this.mYValuesField = yValuesField;
        this.mValues = new ArrayList<S>();

        if (mXValuesField != null)
            this.results.sort(mXValuesField, Sort.ASCENDING);
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
        this.mYValuesField = yValuesField;
        this.mXValuesField = xIndexField;
        this.mValues = new ArrayList<S>();

        if (mXValuesField != null)
            this.results.sort(mXValuesField, Sort.ASCENDING);
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

    public S buildEntryFromResultObject(T realmObject, float x) {
        DynamicRealmObject dynamicObject = new DynamicRealmObject(realmObject);

        return (S) new Entry(dynamicObject.getFloat(mYValuesField),
                mXValuesField == null ? x : dynamicObject.getInt(mXValuesField));
    }

    @Override
    public float getYMin() {
        //return results.min(mYValuesField).floatValue();
        return mYMin;
    }

    @Override
    public float getYMax() {
        //return results.max(mYValuesField).floatValue();
        return mYMax;
    }

    @Override
    public float getXMin() {
        return mXMin;
    }

    @Override
    public float getXMax() {
        return mXMax;
    }

    @Override
    public int getEntryCount() {
        return mValues.size();
    }

    @Override
    public void calcMinMax() {

        if (mValues == null)
            return;

        if (mValues.size() == 0)
            return;

        mYMin = Float.MAX_VALUE;
        mYMax = -Float.MAX_VALUE;

        mXMin = Float.MAX_VALUE;
        mXMax = -Float.MAX_VALUE;

        for (S e : mValues) {

            if (e != null && !Float.isNaN(e.getY())) {

                if (e.getY() < mYMin)
                    mYMin = e.getY();

                if (e.getY() > mYMax)
                    mYMax = e.getY();

                if (e.getX() < mXMin)
                    mXMin = e.getX();

                if (e.getX() > mXMax)
                    mXMax = e.getX();
            }
        }

        if (mYMin == Float.MAX_VALUE) {
            mYMin = 0.f;
            mYMax = 0.f;
        }

        if (mXMin == Float.MAX_VALUE) {
            mXMin = 0.f;
            mXMax = 0.f;
        }
    }

    @Override
    public S getEntryForXPos(float xPos) {
        //DynamicRealmObject o = new DynamicRealmObject(results.where().equalTo(mXValuesField, xIndex).findFirst());
        //return new Entry(o.getFloat(mYValuesField), o.getInt(mXValuesField));
        return getEntryForXPos(xPos, DataSet.Rounding.CLOSEST);
    }

    @Override
    public S getEntryForXPos(float xPos, DataSet.Rounding rounding) {
        int index = getEntryIndex(xPos, rounding);
        if (index > -1)
            return mValues.get(index);
        return null;
    }

    @Override
    public List<S> getEntriesForXPos(float xVal) {

        List<S> entries = new ArrayList<>();

//        {
//            T object = results.get(xVal);
//            if (object != null)
//                entries.add(buildEntryFromResultObject(object, xVal));
//        } else

        if (mXValuesField != null) {
            RealmResults<T> foundObjects = results.where().equalTo(mXValuesField, xVal).findAll();

            for (T e : foundObjects)
                entries.add(buildEntryFromResultObject(e, xVal));
        }

        return entries;
    }

    @Override
    public S getEntryForIndex(int index) {
        //DynamicRealmObject o = new DynamicRealmObject(results.get(index));
        //return new Entry(o.getFloat(mYValuesField), o.getInt(mXValuesField));
        return mValues.get(index);
    }

    @Override
    public int getEntryIndex(float xPos, DataSet.Rounding rounding) {

        int low = 0;
        int high = mValues.size() - 1;
        int closest = -1;

        while (low <= high) {
            int m = (high + low) / 2;

            if (xPos == mValues.get(m).getX()) {
                while (m > 0 && mValues.get(m - 1).getX() == xPos)
                    m--;

                return m;
            }

            if (xPos > mValues.get(m).getX())
                low = m + 1;
            else
                high = m - 1;

            closest = m;
        }

        if (closest != -1) {
            float closestXPos = mValues.get(closest).getX();
            if (rounding == DataSet.Rounding.UP) {
                if (closestXPos < xPos && closest < mValues.size() - 1) {
                    ++closest;
                }
            } else if (rounding == DataSet.Rounding.DOWN) {
                if (closestXPos > xPos && closest > 0) {
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
    public float getYValueForXValue(float xVal) {
        //return new DynamicRealmObject(results.where().greaterThanOrEqualTo(mXValuesField, xIndex).findFirst())
        // .getFloat(mYValuesField);
        Entry e = getEntryForXPos(xVal);

        if (e != null && e.getX() == xVal)
            return e.getY();
        else
            return Float.NaN;
    }

    @Override
    public float[] getYValuesForXPos(float xVal) {

        List<S> entries = getEntriesForXPos(xVal);

        float[] yVals = new float[entries.size()];
        int i = 0;

        for (S e : entries)
            yVals[i++] = e.getY();

        return yVals;
    }

    @Override
    public boolean addEntry(S e) {

        if (e == null)
            return false;

        float val = e.getY();

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
            calcMinMax();
        }

        return removed;
    }

    @Override
    public void addEntryOrdered(S e) {

        if (e == null)
            return;

        float val = e.getY();

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

        if (mValues.size() > 0 && mValues.get(mValues.size() - 1).getX() > e.getX()) {
            int closestIndex = getEntryIndex(e.getX(), DataSet.Rounding.UP);
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
    public String getYValuesField() {
        return mYValuesField;
    }

    /**
     * Sets the field name that is used for getting the y-values out of the RealmResultSet.
     *
     * @param yValuesField
     */
    public void setYValuesField(String yValuesField) {
        this.mYValuesField = yValuesField;
    }

    /**
     * Returns the fieldname that represents the "x-values" in the realm-data.
     *
     * @return
     */
    public String getXValuesField() {
        return mXValuesField;
    }

    /**
     * Sets the field name that is used for getting the x-values out of the RealmResultSet.
     *
     * @param xValuesField
     */
    public void setXValuesField(String xValuesField) {
        this.mXValuesField = xValuesField;
    }
}
