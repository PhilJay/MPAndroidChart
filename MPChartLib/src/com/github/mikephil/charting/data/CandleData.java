package com.github.mikephil.charting.data;

import java.util.ArrayList;

public class CandleData extends BarLineScatterData {

    public CandleData(ArrayList<String> xVals, ArrayList<CandleDataSet> dataSets) {
        super(xVals, dataSets);
    }

    public CandleData(String[] xVals, ArrayList<CandleDataSet> dataSets) {
        super(xVals, dataSets);
    }
    
    public CandleData(ArrayList<String> xVals, CandleDataSet dataSet) {
        super(xVals, toArrayList(dataSet));        
    }
    
    public CandleData(String[] xVals, CandleDataSet dataSet) {
        super(xVals, toArrayList(dataSet));
    }
}
