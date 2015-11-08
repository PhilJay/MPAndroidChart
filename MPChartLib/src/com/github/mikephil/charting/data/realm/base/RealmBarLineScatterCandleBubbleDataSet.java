package com.github.mikephil.charting.data.realm.base;

import android.graphics.Color;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.interfaces.datasets.IBarLineScatterCandleBubbleDataSet;

import io.realm.RealmObject;
import io.realm.RealmResults;

/**
 * Created by Philipp Jahoda on 08/11/15.
 */
public abstract class RealmBarLineScatterCandleBubbleDataSet<T extends RealmObject, S extends Entry> extends RealmBaseDataSet<T, S> implements IBarLineScatterCandleBubbleDataSet<S> {

    /** default highlight color */
    protected int mHighLightColor = Color.rgb(255, 187, 115);

    /**
     * Constructor that takes the realm RealmResults, sorts & stores them.
     *
     * @param results
     * @param yValuesField
     * @param xIndexField
     */
    public RealmBarLineScatterCandleBubbleDataSet(RealmResults<T> results, String yValuesField, String xIndexField) {
        super(results, yValuesField, xIndexField);
    }

    /**
     * Sets the color that is used for drawing the highlight indicators. Dont
     * forget to resolve the color using getResources().getColor(...) or
     * Color.rgb(...).
     *
     * @param color
     */
    public void setHighLightColor(int color) {
        mHighLightColor = color;
    }

    @Override
    public int getHighLightColor() {
        return mHighLightColor;
    }
}
