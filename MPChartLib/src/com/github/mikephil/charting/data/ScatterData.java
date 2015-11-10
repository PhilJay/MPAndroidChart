
package com.github.mikephil.charting.data;

import com.github.mikephil.charting.interfaces.datasets.IScatterDataSet;

import java.util.ArrayList;
import java.util.List;

import io.realm.RealmObject;
import io.realm.RealmResults;

public class ScatterData extends BarLineScatterCandleBubbleData<IScatterDataSet> {

    public ScatterData(RealmResults<? extends RealmObject> result, String xValuesField, List<IScatterDataSet> dataSets) {
        super(toXVals(result, xValuesField), dataSets);
    }

    public ScatterData() {
        super();
    }
    
    public ScatterData(List<String> xVals) {
        super(xVals);
    }

    public ScatterData(String[] xVals) {
        super(xVals);
    }

    public ScatterData(List<String> xVals, List<IScatterDataSet> dataSets) {
        super(xVals, dataSets);
    }

    public ScatterData(String[] xVals, List<IScatterDataSet> dataSets) {
        super(xVals, dataSets);
    }

    public ScatterData(List<String> xVals, IScatterDataSet dataSet) {
        super(xVals, toList(dataSet));
    }

    public ScatterData(String[] xVals, IScatterDataSet dataSet) {
        super(xVals, toList(dataSet));
    }

    private static List<IScatterDataSet> toList(IScatterDataSet dataSet) {
        List<IScatterDataSet> sets = new ArrayList<IScatterDataSet>();
        sets.add(dataSet);
        return sets;
    }

    /**
     * Returns the maximum shape-size across all DataSets.
     * 
     * @return
     */
    public float getGreatestShapeSize() {

        float max = 0f;

        for (IScatterDataSet set : mDataSets) {
            float size = set.getScatterShapeSize();

            if (size > max)
                max = size;
        }

        return max;
    }
}
