package com.github.mikephil.charting.data.realm;

import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BaseDataSet;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.interfaces.datainterfaces.datasets.ILineDataSet;

import java.util.List;

/**
 * Created by Philipp Jahoda on 21/10/15.
 */
public class RealmLineDataSet extends BaseDataSet<Entry> implements ILineDataSet {


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
    public float getYValueSum() {
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
    public Entry getEntryForXIndex(int x) {
        return null;
    }

    @Override
    public boolean contains(Entry e) {
        return false;
    }
}
