package com.github.mikephil.charting.data;

import java.util.ArrayList;
import java.util.List;

public class CandleData extends BarLineScatterCandleData<CandleDataSet> {

    public CandleData() {
        super();
    }
    
    public CandleData(List<String> xVals) {
        super(xVals);
    }
    
    public CandleData(String[] xVals) {
        super(xVals);
    }
    
    public CandleData(List<String> xVals, List<CandleDataSet> dataSets) {
        super(xVals, dataSets);
    }

    public CandleData(String[] xVals, List<CandleDataSet> dataSets) {
        super(xVals, dataSets);
    }
    
    public CandleData(List<String> xVals, CandleDataSet dataSet) {
        super(xVals, toList(dataSet));        
    }
    
    public CandleData(String[] xVals, CandleDataSet dataSet) {
        super(xVals, toList(dataSet));
    }
    
    private static List<CandleDataSet> toList(CandleDataSet dataSet) {
        List<CandleDataSet> sets = new ArrayList<CandleDataSet>();
        sets.add(dataSet);
        return sets;
    }
}
