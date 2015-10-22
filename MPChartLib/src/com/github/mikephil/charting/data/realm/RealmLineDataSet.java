package com.github.mikephil.charting.data.realm;

import android.graphics.Color;
import android.graphics.DashPathEffect;

import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BaseDataSet;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.DefaultFillFormatter;
import com.github.mikephil.charting.formatter.FillFormatter;
import com.github.mikephil.charting.interfaces.datainterfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Philipp Jahoda on 21/10/15.
 */
public class RealmLineDataSet extends BaseDataSet<Entry> implements ILineDataSet {

    private List<Integer> mColors = new ArrayList<>();

    private List<Entry> mValues = new ArrayList<>();

    public RealmLineDataSet() {
        mColors.add(Color.WHITE);

        
    }

    @Override
    public float getCubicIntensity() {
        return .2f;
    }

    @Override
    public boolean isDrawCubicEnabled() {
        return false;
    }

    @Override
    public float getCircleSize() {
        return 5f;
    }

    @Override
    public int getCircleColor(int index) {
        return Color.WHITE;
    }

    @Override
    public boolean isDrawCirclesEnabled() {
        return true;
    }

    @Override
    public int getCircleHoleColor() {
        return Color.WHITE;
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
        return new DefaultFillFormatter();
    }

    @Override
    public int getFillColor() {
        return Color.WHITE;
    }

    @Override
    public int getFillAlpha() {
        return 100;
    }

    @Override
    public float getLineWidth() {
        return 3f;
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
        return Color.WHITE;
    }

    @Override
    public String getLabel() {
        return "Realm Data";
    }

    @Override
    public List<Entry> getYVals() {
        return mValues;
    }

    @Override
    public int getEntryCount() {
        return mValues.size();
    }

    @Override
    public YAxis.AxisDependency getAxisDependency() {
        return YAxis.AxisDependency.LEFT;
    }

    @Override
    public List<Integer> getColors() {
        return mColors;
    }

    @Override
    public int getColor() {
        return mColors.get(0);
    }

    @Override
    public int getColor(int index) {
        return mColors.get(index % mColors.size());
    }
}
