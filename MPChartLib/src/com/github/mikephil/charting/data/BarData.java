
package com.github.mikephil.charting.data;

import java.util.ArrayList;

public class BarData extends BarLineScatterData {
    
    public BarData(ArrayList<String> xVals, ArrayList<BarDataSet> dataSets) {
        super(xVals, dataSets);
    }

    public BarData(String[] xVals, ArrayList<BarDataSet> dataSets) {
        super(xVals, dataSets);
    }
    
    public BarData(ArrayList<String> xVals, BarDataSet dataSet) {
        super(xVals, toArrayList(dataSet));        
    }
    
    public BarData(String[] xVals, BarDataSet dataSet) {
        super(xVals, toArrayList(dataSet));
    }
}
