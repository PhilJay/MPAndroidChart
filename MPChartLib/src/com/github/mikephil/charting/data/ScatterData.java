
package com.github.mikephil.charting.data;

import java.util.ArrayList;

public class ScatterData extends BarLineScatterCandleRadarData {
    
    public ScatterData(ArrayList<String> xVals, ArrayList<ScatterDataSet> dataSets) {
        super(xVals, dataSets);
    }

    public ScatterData(String[] xVals, ArrayList<ScatterDataSet> dataSets) {
        super(xVals, dataSets);
    }
    
    public ScatterData(ArrayList<String> xVals, ScatterDataSet dataSet) {
        super(xVals, toArrayList(dataSet));        
    }
    
    public ScatterData(String[] xVals, ScatterDataSet dataSet) {
        super(xVals, toArrayList(dataSet));
    }
}
