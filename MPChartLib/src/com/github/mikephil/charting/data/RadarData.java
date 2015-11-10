
package com.github.mikephil.charting.data;

import com.github.mikephil.charting.interfaces.datasets.IRadarDataSet;

import java.util.ArrayList;
import java.util.List;

import io.realm.RealmObject;
import io.realm.RealmResults;

/**
 * Data container for the RadarChart.
 *
 * @author Philipp Jahoda
 */
public class RadarData extends ChartData<IRadarDataSet> {

    public RadarData(RealmResults<? extends RealmObject> result, String xValuesField, List<IRadarDataSet> dataSets) {
        super(toXVals(result, xValuesField), dataSets);
    }

    public RadarData() {
        super();
    }

    public RadarData(List<String> xVals) {
        super(xVals);
    }

    public RadarData(String[] xVals) {
        super(xVals);
    }

    public RadarData(List<String> xVals, List<IRadarDataSet> dataSets) {
        super(xVals, dataSets);
    }

    public RadarData(String[] xVals, List<IRadarDataSet> dataSets) {
        super(xVals, dataSets);
    }

    public RadarData(List<String> xVals, IRadarDataSet dataSet) {
        super(xVals, toList(dataSet));
    }

    public RadarData(String[] xVals, IRadarDataSet dataSet) {
        super(xVals, toList(dataSet));
    }

    private static List<IRadarDataSet> toList(IRadarDataSet dataSet) {
        List<IRadarDataSet> sets = new ArrayList<IRadarDataSet>();
        sets.add(dataSet);
        return sets;
    }
}
