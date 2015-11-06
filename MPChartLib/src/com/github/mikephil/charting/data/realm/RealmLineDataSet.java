package com.github.mikephil.charting.data.realm;

import android.graphics.Color;
import android.graphics.DashPathEffect;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.DefaultFillFormatter;
import com.github.mikephil.charting.formatter.FillFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.List;

import io.realm.RealmObject;
import io.realm.RealmResults;

/**
 * Created by Philipp Jahoda on 21/10/15.
 */
public class RealmLineDataSet<T extends RealmObject> extends RealmBaseDataSet implements ILineDataSet {

    private List<Entry> mValues = new ArrayList<>();

    private FillFormatter mFillFormatter = new DefaultFillFormatter();

    public RealmLineDataSet(RealmResults<T> result, String yValuesField, String xIndexField) {
        super(result, yValuesField, xIndexField);
    }

    @Override
    public float getCubicIntensity() {
        return 0.2f;
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
        return false;
    }

    @Override
    public int getCircleHoleColor() {
        return 0;
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
}
