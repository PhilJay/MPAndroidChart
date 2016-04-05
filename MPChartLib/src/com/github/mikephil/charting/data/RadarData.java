
package com.github.mikephil.charting.data;

import com.github.mikephil.charting.interfaces.datasets.IRadarDataSet;

import java.util.ArrayList;
import java.util.List;

/**
 * Data container for the RadarChart.
 *
 * @author Philipp Jahoda
 */
public class RadarData extends ChartData<IRadarDataSet> {

    public RadarData() {
        super();
    }

    public RadarData(List<XAxisValue> xVals) {
        super(xVals);
    }

    public RadarData(XAxisValue[] xVals) {
        super(xVals);
    }

    public RadarData(List<XAxisValue> xVals, List<IRadarDataSet> dataSets) {
        super(xVals, dataSets);
    }

    public RadarData(XAxisValue[] xVals, List<IRadarDataSet> dataSets) {
        super(xVals, dataSets);
    }

    public RadarData(List<XAxisValue> xVals, IRadarDataSet dataSet) {
        super(xVals, toList(dataSet));
    }

    public RadarData(XAxisValue[] xVals, IRadarDataSet dataSet) {
        super(xVals, toList(dataSet));
    }

    private static List<IRadarDataSet> toList(IRadarDataSet dataSet) {
        List<IRadarDataSet> sets = new ArrayList<IRadarDataSet>();
        sets.add(dataSet);
        return sets;
    }
}
