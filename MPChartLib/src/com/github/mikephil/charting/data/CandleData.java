package com.github.mikephil.charting.data;

import java.util.ArrayList;

public class CandleData extends BarLineScatterCandleRadarData<CandleDataSet> {

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
    
    private static ArrayList<CandleDataSet> toArrayList(CandleDataSet dataSet) {
        ArrayList<CandleDataSet> sets = new ArrayList<CandleDataSet>();
        sets.add(dataSet);
        return sets;
    }
}
