package com.github.mikephil.charting.data.realm;

import android.graphics.DashPathEffect;

import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BaseDataSet;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.FillFormatter;
import com.github.mikephil.charting.interfaces.datainterfaces.datasets.ILineDataSet;

import java.util.List;

/**
 * Created by Philipp Jahoda on 21/10/15.
 */
public class RealmLineDataSet extends BaseDataSet<Entry> implements ILineDataSet {

    public RealmLineDataSet() {

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
        return 0;
    }

    @Override
    public int getCircleColor(int index) {
        return 0;
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
        return null;
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
        return 0;
    }

    @Override
    public boolean isDrawFilledEnabled() {
        return false;
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
    public String getLabel() {
        return null;
    }

    @Override
    public List<Entry> getYVals() {
        return null;
    }

    @Override
    public float getYMin() {
        return 0;
    }

    @Override
    public float getYMax() {
        return 0;
    }

    @Override
    public int getEntryCount() {
        return 0;
    }

    @Override
    public YAxis.AxisDependency getAxisDependency() {
        return null;
    }

    @Override
    public List<Integer> getColors() {
        return null;
    }

    @Override
    public int getColor() {
        return 0;
    }

    @Override
    public int getColor(int index) {
        return 0;
    }

    @Override
    public boolean contains(Entry e) {
        return false;
    }
}
