package com.github.mikephil.charting.data;

import java.util.ArrayList;

/**
 * DataSet for the OHLCChart.
 *
 * @author Maximilian Peitz
 */
public class OHLCData extends BarLineScatterCandleData<OHLCDataSet> {

    public OHLCData() {
        super();
    }

    public OHLCData(ArrayList<String> xVals) {
        super(xVals);
    }

    public OHLCData(String[] xVals) {
        super(xVals);
    }

    public OHLCData(ArrayList<String> xVals, ArrayList<OHLCDataSet> dataSets) {
        super(xVals, dataSets);
    }

    public OHLCData(String[] xVals, ArrayList<OHLCDataSet> dataSets) {
        super(xVals, dataSets);
    }

    public OHLCData(ArrayList<String> xVals, OHLCDataSet dataSet) {
        super(xVals, toArrayList(dataSet));
    }

    public OHLCData(String[] xVals, OHLCDataSet dataSet) {
        super(xVals, toArrayList(dataSet));
    }

    private static ArrayList<OHLCDataSet> toArrayList(OHLCDataSet dataSet) {
        ArrayList<OHLCDataSet> sets = new ArrayList<OHLCDataSet>();
        sets.add(dataSet);
        return sets;
    }
}
