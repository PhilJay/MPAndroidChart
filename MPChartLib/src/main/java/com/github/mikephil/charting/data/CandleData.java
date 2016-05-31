package com.github.mikephil.charting.data;

import com.github.mikephil.charting.interfaces.datasets.ICandleDataSet;

import java.util.ArrayList;
import java.util.List;

public class CandleData extends BarLineScatterCandleBubbleData<ICandleDataSet> {

    public CandleData() {
        super();
    }
    
    public CandleData(List<XAxisValue> xVals) {
        super(xVals);
    }
    
    public CandleData(XAxisValue[] xVals) {
        super(xVals);
    }
    
    public CandleData(List<XAxisValue> xVals, List<ICandleDataSet> dataSets) {
        super(xVals, dataSets);
    }

    public CandleData(XAxisValue[] xVals, List<ICandleDataSet> dataSets) {
        super(xVals, dataSets);
    }
    
    public CandleData(List<XAxisValue> xVals, ICandleDataSet dataSet) {
        super(xVals, toList(dataSet));        
    }
    
    public CandleData(XAxisValue[] xVals, ICandleDataSet dataSet) {
        super(xVals, toList(dataSet));
    }
    
    private static List<ICandleDataSet> toList(ICandleDataSet dataSet) {
        List<ICandleDataSet> sets = new ArrayList<ICandleDataSet>();
        sets.add(dataSet);
        return sets;
    }
}
