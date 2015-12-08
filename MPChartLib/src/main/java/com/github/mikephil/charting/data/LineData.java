
package com.github.mikephil.charting.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Data object that encapsulates all data associated with a LineChart.
 * 
 * @author Philipp Jahoda
 */
public class LineData extends BarLineScatterCandleBubbleData<LineDataSet> {

    public LineData() {
        super();
    }

    public LineData(List<String> xVals) {
        super(xVals);
    }

    public LineData(String[] xVals) {
        super(xVals);
    }

    public LineData(List<String> xVals, List<LineDataSet> dataSets) {
        super(xVals, dataSets);
    }

    public LineData(String[] xVals, List<LineDataSet> dataSets) {
        super(xVals, dataSets);
    }

    public LineData(List<String> xVals, LineDataSet dataSet) {
        super(xVals, toList(dataSet));
    }

    public LineData(String[] xVals, LineDataSet dataSet) {
        super(xVals, toList(dataSet));
    }

    private static List<LineDataSet> toList(LineDataSet dataSet) {
        List<LineDataSet> sets = new ArrayList<LineDataSet>();
        sets.add(dataSet);
        return sets;
    }
}
