
package com.github.mikephil.charting.data;

import java.util.ArrayList;

public class LineData extends BarLineScatterCandleData<LineDataSet> {
    
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
