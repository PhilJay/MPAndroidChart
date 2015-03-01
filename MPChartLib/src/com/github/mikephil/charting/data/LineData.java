
package com.github.mikephil.charting.data;

import java.util.ArrayList;

/**
 * Data object that encapsulates all data associated with a LineChart.
 * 
 * @author Philipp Jahoda
 */
public class LineData extends BarLineScatterCandleData<LineDataSet> {

    public LineData() {
        super();
    }

    public LineData(ArrayList<String> xVals) {
        super(xVals);
    }

    public LineData(String[] xVals) {
        super(xVals);
    }

    public LineData(ArrayList<String> xVals, ArrayList<LineDataSet> dataSets) {
        super(xVals, dataSets);
    }

    public LineData(String[] xVals, ArrayList<LineDataSet> dataSets) {
        super(xVals, dataSets);
    }

    public LineData(ArrayList<String> xVals, LineDataSet dataSet) {
        super(xVals, toArrayList(dataSet));
    }

    public LineData(String[] xVals, LineDataSet dataSet) {
        super(xVals, toArrayList(dataSet));
    }

    private static ArrayList<LineDataSet> toArrayList(LineDataSet dataSet) {
        ArrayList<LineDataSet> sets = new ArrayList<LineDataSet>();
        sets.add(dataSet);
        return sets;
    }
}
