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

import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.dynamic.DynamicRealmObject;

/**
 * Created by Philipp Jahoda on 21/10/15.
 */
public class RealmLineDataSet<T extends RealmObject> extends BaseDataSet<Entry> implements ILineDataSet {

    private List<Integer> mColors = new ArrayList<>();
    private List<Entry> mValues = new ArrayList<>();

    private FillFormatter mFillFormatter = new DefaultFillFormatter();

    public RealmLineDataSet(RealmResults<T> result, String yValuesField, String xIndexField) {
        mColors.add(Color.BLACK);

        result.sort(xIndexField, true);

        for(T object : result) {

            DynamicRealmObject dynamicObject = new DynamicRealmObject(object);

            mValues.add(new Entry(dynamicObject.getFloat(yValuesField), dynamicObject.getInt(xIndexField)));
        }

        //calcMinMax(mValues, mLastStart, mLastEnd);
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
        return 8f;
    }

    @Override
    public int getCircleColor(int index) {
        return Color.BLACK;
    }

    @Override
    public boolean isDrawCirclesEnabled() {
        return true;
    }

    @Override
    public int getCircleHoleColor() {
        return Color.BLACK;
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
        return Color.WHITE;
    }

    @Override
    public int getFillAlpha() {
        return 100;
    }

    @Override
    public float getLineWidth() {
        return 4f;
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
