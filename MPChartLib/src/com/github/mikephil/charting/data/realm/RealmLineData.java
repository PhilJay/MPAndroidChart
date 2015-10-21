package com.github.mikephil.charting.data.realm;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datainterfaces.data.ILineData;
import com.github.mikephil.charting.interfaces.datainterfaces.datasets.IDataSet;

import java.util.List;

/**
 * Created by Philipp Jahoda on 21/10/15.
 */
public class RealmLineData implements ILineData {


    @Override
    public List getDataSets() {
        return null;
    }

    @Override
    public IDataSet<? extends Entry> getDataSetByIndex(int index) {
        return null;
    }

    @Override
    public int getYValCount() {
        return 0;
    }

    @Override
    public int getIndexOfDataSet(IDataSet dataSet) {
        return 0;
    }
}
