package com.github.mikephil.charting.data;

import com.github.mikephil.charting.interfaces.datasets.ICandleDataSet;

import java.util.ArrayList;
import java.util.List;

import io.realm.RealmObject;
import io.realm.RealmResults;

public class CandleData extends BarLineScatterCandleBubbleData<ICandleDataSet> {

    public CandleData(RealmResults<? extends RealmObject> result, String xValuesField, List<ICandleDataSet> dataSets) {
        super(toXVals(result, xValuesField), dataSets);
    }

    public CandleData() {
        super();
    }
    
    public CandleData(List<String> xVals) {
        super(xVals);
    }
    
    public CandleData(String[] xVals) {
        super(xVals);
    }
    
    public CandleData(List<String> xVals, List<ICandleDataSet> dataSets) {
        super(xVals, dataSets);
    }

    public CandleData(String[] xVals, List<ICandleDataSet> dataSets) {
        super(xVals, dataSets);
    }
    
    public CandleData(List<String> xVals, ICandleDataSet dataSet) {
        super(xVals, toList(dataSet));        
    }
    
    public CandleData(String[] xVals, ICandleDataSet dataSet) {
        super(xVals, toList(dataSet));
    }
    
    private static List<ICandleDataSet> toList(ICandleDataSet dataSet) {
        List<ICandleDataSet> sets = new ArrayList<ICandleDataSet>();
        sets.add(dataSet);
        return sets;
    }
}
