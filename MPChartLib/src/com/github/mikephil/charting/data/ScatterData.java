
package com.github.mikephil.charting.data;

import java.util.ArrayList;
import java.util.List;

public class ScatterData extends BarLineScatterCandleBubbleData<ScatterDataSet> {

    public ScatterData() {
        super();
    }
    
    public ScatterData(List<String> xVals) {
        super(xVals);
    }

    public ScatterData(String[] xVals) {
        super(xVals);
    }

    public ScatterData(List<String> xVals, List<ScatterDataSet> dataSets) {
        super(xVals, dataSets);
    }

    public ScatterData(String[] xVals, List<ScatterDataSet> dataSets) {
        super(xVals, dataSets);
    }

    public ScatterData(List<String> xVals, ScatterDataSet dataSet) {
        super(xVals, toList(dataSet));
    }

    public ScatterData(String[] xVals, ScatterDataSet dataSet) {
        super(xVals, toList(dataSet));
    }

    private static List<ScatterDataSet> toList(ScatterDataSet dataSet) {
        List<ScatterDataSet> sets = new ArrayList<ScatterDataSet>();
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

        for (ScatterDataSet set : mDataSets) {
            float size = set.getScatterShapeSize();

            if (size > max)
                max = size;
        }

        return max;
    }
}
