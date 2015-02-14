
package com.github.mikephil.charting.data;

import java.util.ArrayList;

/**
 * Data container for the RadarChart.
 * 
 * @author Philipp Jahoda
 */
public class RadarData extends ChartData<RadarDataSet> {

    public RadarData() {
        super();
    }
    
    public RadarData(ArrayList<String> xVals) {
        super(xVals);
    }
    
    public RadarData(String[] xVals) {
        super(xVals);
    }
    
    public RadarData(ArrayList<String> xVals, ArrayList<RadarDataSet> dataSets) {
        super(xVals, dataSets);
    }

    public RadarData(String[] xVals, ArrayList<RadarDataSet> dataSets) {
        super(xVals, dataSets);
    }

    public RadarData(ArrayList<String> xVals, RadarDataSet dataSet) {
        super(xVals, toArrayList(dataSet));
    }

    public RadarData(String[] xVals, RadarDataSet dataSet) {
        super(xVals, toArrayList(dataSet));
    }
    
    private static ArrayList<RadarDataSet> toArrayList(RadarDataSet dataSet) {
        ArrayList<RadarDataSet> sets = new ArrayList<RadarDataSet>();
        sets.add(dataSet);
        return sets;
    }
}
