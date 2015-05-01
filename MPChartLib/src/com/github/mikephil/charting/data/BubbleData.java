
package com.github.mikephil.charting.data;

import java.util.ArrayList;
import java.util.List;

public class BubbleData extends BarLineScatterCandleData<BubbleDataSet> {

    public BubbleData() {
        super();
    }

    public BubbleData(List<String> xVals) {
        super(xVals);
    }

    public BubbleData(String[] xVals) {
        super(xVals);
    }

    public BubbleData(List<String> xVals, List<BubbleDataSet> dataSets) {
        super(xVals, dataSets);
    }

    public BubbleData(String[] xVals, List<BubbleDataSet> dataSets) {
        super(xVals, dataSets);
    }

    public BubbleData(List<String> xVals, BubbleDataSet dataSet) {
        super(xVals, toList(dataSet));
    }

    public BubbleData(String[] xVals, BubbleDataSet dataSet) {
        super(xVals, toList(dataSet));
    }

    private static List<BubbleDataSet> toList(BubbleDataSet dataSet) {
        List<BubbleDataSet> sets = new ArrayList<BubbleDataSet>();
        sets.add(dataSet);
        return sets;
    }

    /**
     * Sets the width of the circle that surrounds the bubble when highlighted
     * for all DataSet objects this data object contains, in dp.
     * 
     * @param width
     */
    public void setHighlightCircleWidth(float width) {
        for (BubbleDataSet set : mDataSets) {
            set.setHighlightCircleWidth(width);
        }
    }
}
