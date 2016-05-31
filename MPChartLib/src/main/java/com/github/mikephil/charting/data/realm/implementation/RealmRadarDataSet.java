package com.github.mikephil.charting.data.realm.implementation;

import android.graphics.Color;

import com.github.mikephil.charting.data.realm.base.RealmLineRadarDataSet;
import com.github.mikephil.charting.interfaces.datasets.IRadarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import io.realm.RealmObject;
import io.realm.RealmResults;

/**
 * Created by Philipp Jahoda on 07/11/15.
 */
public class RealmRadarDataSet<T extends RealmObject> extends RealmLineRadarDataSet<T> implements IRadarDataSet {

    /// flag indicating whether highlight circle should be drawn or not
    protected boolean mDrawHighlightCircleEnabled = false;

    protected int mHighlightCircleFillColor = Color.WHITE;

    /// The stroke color for highlight circle.
    /// If Utils.COLOR_NONE, the color of the dataset is taken.
    protected int mHighlightCircleStrokeColor = ColorTemplate.COLOR_NONE;

    protected int mHighlightCircleStrokeAlpha = (int)(0.3 * 255);
    protected float mHighlightCircleInnerRadius = 3.0f;
    protected float mHighlightCircleOuterRadius = 4.0f;
    protected float mHighlightCircleStrokeWidth = 2.0f;

    /**
     * Constructor for creating a RadarDataSet with realm data.
     *
     * @param result       the queried results from the realm database
     * @param yValuesField the name of the field in your data object that represents the y-value
     */
    public RealmRadarDataSet(RealmResults<T> result, String yValuesField) {
        super(result, yValuesField);

        build(this.results);
        calcMinMax();
    }

    /**
     * Constructor for creating a RadarDataSet with realm data.
     *
     * @param result       the queried results from the realm database
     * @param yValuesField the name of the field in your data object that represents the y-value
     * @param xIndexField  the name of the field in your data object that represents the x-index
     */
    public RealmRadarDataSet(RealmResults<T> result, String yValuesField, String xIndexField) {
        super(result, yValuesField, xIndexField);

        build(this.results);
        calcMinMax();
    }

    /// Returns true if highlight circle should be drawn, false if not
    @Override
    public boolean isDrawHighlightCircleEnabled()
    {
        return mDrawHighlightCircleEnabled;
    }

    /// Sets whether highlight circle should be drawn or not
    @Override
    public void setDrawHighlightCircleEnabled(boolean enabled)
    {
        mDrawHighlightCircleEnabled = enabled;
    }

    @Override
    public int getHighlightCircleFillColor()
    {
        return mHighlightCircleFillColor;
    }

    public void setHighlightCircleFillColor(int color)
    {
        mHighlightCircleFillColor = color;
    }

    /// Returns the stroke color for highlight circle.
    /// If Utils.COLOR_NONE, the color of the dataset is taken.
    @Override
    public int getHighlightCircleStrokeColor()
    {
        return mHighlightCircleStrokeColor;
    }

    /// Sets the stroke color for highlight circle.
    /// Set to Utils.COLOR_NONE in order to use the color of the dataset;
    public void setHighlightCircleStrokeColor(int color)
    {
        mHighlightCircleStrokeColor = color;
    }

    @Override
    public int getHighlightCircleStrokeAlpha()
    {
        return mHighlightCircleStrokeAlpha;
    }

    public void setHighlightCircleStrokeAlpha(int alpha)
    {
        mHighlightCircleStrokeAlpha = alpha;
    }

    @Override
    public float getHighlightCircleInnerRadius()
    {
        return mHighlightCircleInnerRadius;
    }

    public void setHighlightCircleInnerRadius(float radius)
    {
        mHighlightCircleInnerRadius = radius;
    }

    @Override
    public float getHighlightCircleOuterRadius()
    {
        return mHighlightCircleOuterRadius;
    }

    public void setHighlightCircleOuterRadius(float radius)
    {
        mHighlightCircleOuterRadius = radius;
    }

    @Override
    public float getHighlightCircleStrokeWidth()
    {
        return mHighlightCircleStrokeWidth;
    }

    public void setHighlightCircleStrokeWidth(float strokeWidth)
    {
        mHighlightCircleStrokeWidth = strokeWidth;
    }
}
