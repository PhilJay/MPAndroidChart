
package com.github.mikephil.charting.data;

import java.util.ArrayList;

public class PieData extends ChartData {
    
    public PieData(ArrayList<String> xVals, ArrayList<PieDataSet> dataSets) {
        super(xVals, dataSets);
    }

    public PieData(String[] xVals, ArrayList<PieDataSet> dataSets) {
        super(xVals, dataSets);
    }
    
    public PieData(ArrayList<String> xVals, PieDataSet dataSet) {
        super(xVals, toArrayList(dataSet));        
    }
    
    public PieData(String[] xVals, PieDataSet dataSet) {
        super(xVals, toArrayList(dataSet));
    }
}
