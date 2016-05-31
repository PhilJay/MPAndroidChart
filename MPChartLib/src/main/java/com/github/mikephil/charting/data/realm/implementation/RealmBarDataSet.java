package com.github.mikephil.charting.data.realm.implementation;

import android.graphics.Color;

import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.realm.base.RealmBarLineScatterCandleBubbleDataSet;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;

import io.realm.DynamicRealmObject;
import io.realm.RealmFieldType;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.RealmResults;

/**
 * Created by Philipp Jahoda on 07/11/15.
 */
public class RealmBarDataSet<T extends RealmObject> extends RealmBarLineScatterCandleBubbleDataSet<T, BarEntry>
        implements IBarDataSet {

    private String mStackValueFieldName;

    /**
     * the maximum number of bars that are stacked upon each other, this yValue
     * is calculated from the Entries that are added to the DataSet
     */
    private int mStackSize = 1;

    /**
     * the color used for drawing the bar shadows
     */
    private int mBarShadowColor = Color.rgb(215, 215, 215);

    private float mBarBorderWidth = 0.0f;

    private int mBarBorderColor = Color.BLACK;

    /**
     * the alpha yValue used to draw the highlight indicator bar
     */
    private int mHighLightAlpha = 120;

    /**
     * array of labels used to describe the different values of the stacked bars
     */
    private String[] mStackLabels = new String[]{
            "Stack"
    };

    public RealmBarDataSet(RealmResults<T> results, String yValuesField, String xIndexField) {
        super(results, yValuesField, xIndexField);
        mHighLightColor = Color.rgb(0, 0, 0);

        build(this.results);
        calcMinMax();
    }

    /**
     * Constructor for supporting stacked values.
     *
     * @param results
     * @param yValuesField
     * @param xIndexField
     * @param stackValueFieldName
     */
    public RealmBarDataSet(RealmResults<T> results, String yValuesField, String xIndexField, String
            stackValueFieldName) {
        super(results, yValuesField, xIndexField);
        this.mStackValueFieldName = stackValueFieldName;
        mHighLightColor = Color.rgb(0, 0, 0);

        build(this.results);
        calcMinMax();
    }

    @Override
    public void build(RealmResults<T> results) {

        super.build(results);

        calcStackSize();
    }

    @Override
    public BarEntry buildEntryFromResultObject(T realmObject, float x) {
        DynamicRealmObject dynamicObject = new DynamicRealmObject(realmObject);

        if (dynamicObject.getFieldType(mYValuesField) == RealmFieldType.LIST) {

            RealmList<DynamicRealmObject> list = dynamicObject.getList(mYValuesField);
            float[] values = new float[list.size()];

            int i = 0;
            for (DynamicRealmObject o : list) {
                values[i] = o.getFloat(mStackValueFieldName);
                i++;
            }

            return new BarEntry(
                    mXValuesField == null ? x : dynamicObject.getInt(mXValuesField), values);
        } else {
            float value = dynamicObject.getFloat(mYValuesField);
            return new BarEntry(mXValuesField == null ? x : dynamicObject.getInt(mXValuesField), value);
        }
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

        for (BarEntry e : mValues) {

            if (e != null && !Float.isNaN(e.getY())) {

                if (e.getYVals() == null) {

                    if (e.getY() < mYMin)
                        mYMin = e.getY();

                    if (e.getY() > mYMax)
                        mYMax = e.getY();
                } else {

                    if (-e.getNegativeSum() < mYMin)
                        mYMin = -e.getNegativeSum();

                    if (e.getPositiveSum() > mYMax)
                        mYMax = e.getPositiveSum();
                }

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

    private void calcStackSize() {

        for (int i = 0; i < mValues.size(); i++) {

            float[] vals = mValues.get(i).getYVals();

            if (vals != null && vals.length > mStackSize)
                mStackSize = vals.length;
        }
    }

    @Override
    public int getStackSize() {
        return mStackSize;
    }

    @Override
    public boolean isStacked() {
        return mStackSize > 1 ? true : false;
    }

    /**
     * Sets the color used for drawing the bar-shadows. The bar shadows is a
     * surface behind the bar that indicates the maximum yValue. Don't for get to
     * use getResources().getColor(...) to set this. Or Color.rgb(...).
     *
     * @param color
     */
    public void setBarShadowColor(int color) {
        mBarShadowColor = color;
    }

    @Override
    public int getBarShadowColor() {
        return mBarShadowColor;
    }

    /**
     * Sets the width used for drawing borders around the bars.
     * If borderWidth == 0, no border will be drawn.
     *
     * @return
     */
    public void setBarBorderWidth(float width) {
        mBarBorderWidth = width;
    }

    /**
     * Returns the width used for drawing borders around the bars.
     * If borderWidth == 0, no border will be drawn.
     *
     * @return
     */
    @Override
    public float getBarBorderWidth() {
        return mBarBorderWidth;
    }

    /**
     * Sets the color drawing borders around the bars.
     *
     * @return
     */
    public void setBarBorderColor(int color) {
        mBarBorderColor = color;
    }

    /**
     * Returns the color drawing borders around the bars.
     *
     * @return
     */
    @Override
    public int getBarBorderColor() {
        return mBarBorderColor;
    }

    /**
     * Set the alpha yValue (transparency) that is used for drawing the highlight
     * indicator bar. min = 0 (fully transparent), max = 255 (fully opaque)
     *
     * @param alpha
     */
    public void setHighLightAlpha(int alpha) {
        mHighLightAlpha = alpha;
    }

    @Override
    public int getHighLightAlpha() {
        return mHighLightAlpha;
    }

    /**
     * Sets labels for different values of bar-stacks, in case there are one.
     *
     * @param labels
     */
    public void setStackLabels(String[] labels) {
        mStackLabels = labels;
    }

    @Override
    public String[] getStackLabels() {
        return mStackLabels;
    }

}
