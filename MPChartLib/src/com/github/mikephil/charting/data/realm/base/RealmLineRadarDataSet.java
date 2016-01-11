package com.github.mikephil.charting.data.realm.base;

import android.graphics.Color;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.interfaces.datasets.ILineRadarDataSet;
import com.github.mikephil.charting.utils.Utils;

import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.dynamic.DynamicRealmObject;

/**
 * Created by Philipp Jahoda on 08/11/15.
 */
public abstract class RealmLineRadarDataSet<T extends RealmObject> extends RealmLineScatterCandleRadarDataSet<T, Entry> implements ILineRadarDataSet<Entry> {

    /** the color that is used for filling the line surface */
    private int mFillColor = Color.rgb(140, 234, 255);

    /** transparency used for filling line surface */
    private int mFillAlpha = 85;

    /** the width of the drawn data lines */
    private float mLineWidth = 2.5f;

    /** if true, the data will also be drawn filled */
    private boolean mDrawFilled = false;


    public RealmLineRadarDataSet(RealmResults<T> results, String yValuesField) {
        super(results, yValuesField);
    }

    /**
     * Constructor that takes the realm RealmResults, sorts & stores them.
     *
     * @param results
     * @param yValuesField
     * @param xIndexField
     */
    public RealmLineRadarDataSet(RealmResults<T> results, String yValuesField, String xIndexField) {
        super(results, yValuesField, xIndexField);
    }

    @Override
    public void build(RealmResults<T> results) {

        if (mIndexField == null) { // x-index not available

            int xIndex = 0;

            for (T object : results) {

                DynamicRealmObject dynamicObject = new DynamicRealmObject(object);
                mValues.add(new Entry(dynamicObject.getFloat(mValuesField), xIndex));
                xIndex++;
            }

        } else {

            for (T object : results) {

                DynamicRealmObject dynamicObject = new DynamicRealmObject(object);
                mValues.add(new Entry(dynamicObject.getFloat(mValuesField), dynamicObject.getInt(mIndexField)));
            }
        }
    }

    @Override
    public int getFillColor() {
        return mFillColor;
    }

    /**
     * sets the color that is used for filling the line surface
     *
     * @param color
     */
    public void setFillColor(int color) {
        mFillColor = color;
    }

    @Override
    public int getFillAlpha() {
        return mFillAlpha;
    }

    /**
     * sets the alpha value (transparency) that is used for filling the line
     * surface (0-255), default: 85
     *
     * @param alpha
     */
    public void setFillAlpha(int alpha) {
        mFillAlpha = alpha;
    }

    /**
     * set the line width of the chart (min = 0.2f, max = 10f); default 1f NOTE:
     * thinner line == better performance, thicker line == worse performance
     *
     * @param width
     */
    public void setLineWidth(float width) {

        if (width < 0.2f)
            width = 0.2f;
        if (width > 10.0f)
            width = 10.0f;
        mLineWidth = Utils.convertDpToPixel(width);
    }

    @Override
    public float getLineWidth() {
        return mLineWidth;
    }

    @Override
    public void setDrawFilled(boolean filled) {
        mDrawFilled = filled;
    }

    @Override
    public boolean isDrawFilledEnabled() {
        return mDrawFilled;
    }
}
