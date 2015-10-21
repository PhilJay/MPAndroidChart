package com.github.mikephil.charting.data.realm;

import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datainterfaces.data.ILineData;

import java.util.List;

/**
 * Created by Philipp Jahoda on 21/10/15.
 */
public class RealmLineData implements ILineData {


    @Override
    public List<LineDataSet> getDataSets() {
        return null;
    }

    @Override
    public LineDataSet getDataSetByIndex(int index) {
        return null;
    }

    @Override
    public int getIndexOfDataSet(LineDataSet dataSet) {
        return 0;
    }

    @Override
    public int getYValCount() {
        return 0;
    }
}
