
package com.github.mikephil.charting.data;

import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.List;

/**
 * Data object that encapsulates all data associated with a LineChart.
 * 
 * @author Philipp Jahoda
 */
public class LineData extends BarLineScatterCandleBubbleData<ILineDataSet> {

    public LineData() {
        super();
    }

    public LineData(List<XAxisValue> xVals) {
        super(xVals);
    }

    public LineData(XAxisValue[] xVals) {
        super(xVals);
    }

    public LineData(List<XAxisValue> xVals, List<ILineDataSet> dataSets) {
        super(xVals, dataSets);
    }

    public LineData(XAxisValue[] xVals, List<ILineDataSet> dataSets) {
        super(xVals, dataSets);
    }

    public LineData(List<XAxisValue> xVals, ILineDataSet dataSet) {
        super(xVals, toList(dataSet));
    }

    public LineData(XAxisValue[] xVals, ILineDataSet dataSet) {
        super(xVals, toList(dataSet));
    }

    private static List<ILineDataSet> toList(ILineDataSet dataSet) {
        List<ILineDataSet> sets = new ArrayList<ILineDataSet>();
        sets.add(dataSet);
        return sets;
    }
}
