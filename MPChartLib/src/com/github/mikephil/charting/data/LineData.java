
package com.github.mikephil.charting.data;

import java.util.ArrayList;

public class LineData extends ChartData {

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
}
