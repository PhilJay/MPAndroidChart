package com.github.mikephil.charting.data.realm.implementation;

import android.graphics.Color;

import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
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
public class RealmBarDataSet<T extends RealmObject> extends RealmBarLineScatterCandleBubbleDataSet<T, BarEntry> implements IBarDataSet {

    private String mStackValueFieldName;

    /**
     * space indicator between the bars 0.1f == 10 %
     */
    private float mBarSpace = 0.15f;

    /**
     * the maximum number of bars that are stacked upon each other, this value
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
     * the alpha value used to draw the highlight indicator bar
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
        calcMinMax(0, results.size());
    }

    /**
     * Constructor for supporting stacked values.
     *
     * @param results
     * @param yValuesField
     * @param xIndexField
     * @param stackValueFieldName
     */
    public RealmBarDataSet(RealmResults<T> results, String yValuesField, String xIndexField, String stackValueFieldName) {
        super(results, yValuesField, xIndexField);
        this.mStackValueFieldName = stackValueFieldName;
        mHighLightColor = Color.rgb(0, 0, 0);

        build(this.results);
        calcMinMax(0, results.size());
    }

    @Override
    public void build(RealmResults<T> results) {

        super.build(results);

        calcStackSize();
    }

    @Override
    public BarEntry buildEntryFromResultObject(T realmObject, int xIndex) {
        DynamicRealmObject dynamicObject = new DynamicRealmObject(realmObject);

        if (dynamicObject.getFieldType(mValuesField) == RealmFieldType.LIST) {

            RealmList<DynamicRealmObject> list = dynamicObject.getList(mValuesField);
            float[] values = new float[list.size()];

            int i = 0;
            for (DynamicRealmObject o : list) {
                values[i] = o.getFloat(mStackValueFieldName);
                i++;
            }

            return new BarEntry(values,
                    mIndexField == null ? xIndex : dynamicObject.getInt(mIndexField));
        } else {
            float value = dynamicObject.getFloat(mValuesField);
            return new BarEntry(value,
                    mIndexField == null ? xIndex : dynamicObject.getInt(mIndexField));
        }
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

            BarEntry e = mValues.get(i);

            if (e != null && !Float.isNaN(e.getVal())) {

                if (e.getVals() == null) {

                    if (e.getVal() < mYMin)
                        mYMin = e.getVal();

                    if (e.getVal() > mYMax)
                        mYMax = e.getVal();
                } else {

                    if (-e.getNegativeSum() < mYMin)
                        mYMin = -e.getNegativeSum();

                    if (e.getPositiveSum() > mYMax)
                        mYMax = e.getPositiveSum();
                }
            }
        }

        if (mYMin == Float.MAX_VALUE) {
            mYMin = 0.f;
            mYMax = 0.f;
        }
    }

    private void calcStackSize() {

        for (int i = 0; i < mValues.size(); i++) {

            float[] vals = mValues.get(i).getVals();

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
     * returns the space between bars in percent of the whole width of one value
     *
     * @return
     */
    public float getBarSpacePercent() {
        return mBarSpace * 100f;
    }

    @Override
    public float getBarSpace() {
        return mBarSpace;
    }

    /**
     * sets the space between the bars in percent (0-100) of the total bar width
     *
     * @param percent
     */
    public void setBarSpacePercent(float percent) {
        mBarSpace = percent / 100f;
    }

    /**
     * Sets the color used for drawing the bar-shadows. The bar shadows is a
     * surface behind the bar that indicates the maximum value. Don't for get to
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
     * Set the alpha value (transparency) that is used for drawing the highlight
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
